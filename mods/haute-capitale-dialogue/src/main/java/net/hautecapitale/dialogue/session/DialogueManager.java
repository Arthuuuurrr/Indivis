package net.hautecapitale.dialogue.session;

import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.hautecapitale.dialogue.HauteCapitaleDialogue;
import net.hautecapitale.dialogue.config.DialogueConfig;
import net.hautecapitale.dialogue.network.DialogueNetwork;
import net.minecraft.entity.Entity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Le registre des sessions, côté serveur. L'autorité.
 *
 * <p>Une session par joueur, jamais plus. Elle s'ouvre quand un pont (Easy NPC,
 * la capture) le demande, et se ferme dès qu'une des conditions du cahier des
 * charges tombe : distance, personnage disparu, mort, téléportation, changement
 * de dimension, déconnexion, cinématique signalée par le client, écran refermé,
 * véhicule parti quand la conversation n'y est pas permise. Le client est
 * prévenu de chaque fermeture avec sa raison ; il ne décide de rien.
 *
 * <p>Aucun parcours d'entités : seules les sessions actives sont examinées, tous
 * les {@code verification_ticks}.
 */
public final class DialogueManager {

    private static final Map<UUID, DialogueSession> SESSIONS = new HashMap<>();
    private static int prochainId = 1;

    /** Identifiants des modificateurs de verrou ; temporaires, jamais sauvegardés. */
    private static final Identifier VERROU_VITESSE = HauteCapitaleDialogue.id("verrou_vitesse");
    private static final Identifier VERROU_SAUT = HauteCapitaleDialogue.id("verrou_saut");

    /** Ce qu'un pont fait à l'ouverture et à la fermeture (regard du personnage, par exemple). */
    private static final List<BiConsumer<ServerPlayerEntity, DialogueSession>> A_L_OUVERTURE = new ArrayList<>();
    private static final List<BiConsumer<ServerPlayerEntity, DialogueSession>> A_LA_FERMETURE = new ArrayList<>();

    /**
     * Ce que les mods de transport savent mieux que la physique : « ce personnage
     * est à quai » ou « en route ». Vide = « je ne sais pas ».
     */
    private static final List<Function<Entity, Optional<Boolean>>> SONDES_MOUVEMENT = new ArrayList<>();

    /** Le pont dit si un écran serveur est un menu de dialogue (le nôtre ou celui d'Easy NPC). */
    private static Predicate<ScreenHandler> menuDeDialogue = handler -> false;

    /** Le pont sait rouvrir une conversation ; sans lui, rien ne se rouvre. */
    private static Rouvreur rouvreur;

    /** Le pont sait décrire un personnage, pour les commandes d'administration. */
    private static Descripteur descripteur = (pnj, dialogue) -> null;

    /** Une conversation interrompue par une cinématique, à reprendre quand elle finit. */
    private record RepriseEnAttente(int sessionId, UUID pnj, DialogueMode mode, UUID dialogue, long expireTick) {
    }

    private static final Map<UUID, RepriseEnAttente> REPRISES = new HashMap<>();

    private DialogueManager() {
    }

    public static void init() {
        ServerTickEvents.END_SERVER_TICK.register(DialogueManager::tick);
        ServerPlayConnectionEvents.DISCONNECT.register(
                (handler, server) -> oublier(handler.getPlayer(), CloseReason.DECONNEXION));
        ServerPlayerEvents.AFTER_RESPAWN.register(
                (ancien, nouveau, vivant) -> fermer(nouveau, CloseReason.MORT));
        ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD.register(
                (joueur, origine, destination) -> fermer(joueur, CloseReason.DIMENSION));
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
            SESSIONS.clear();
            REPRISES.clear();
        });
    }

    public static void menuDeDialogue(Predicate<ScreenHandler> test) {
        menuDeDialogue = test;
    }

    public static void rouvreur(Rouvreur nouveau) {
        rouvreur = nouveau;
    }

    public static void descripteur(Descripteur nouveau) {
        descripteur = nouveau;
    }

    /** Ce que le pont sait dire de ce personnage, ou {@code null}. */
    public static String decrire(Entity pnj, UUID dialogue) {
        try {
            return descripteur.decrire(pnj, dialogue);
        } catch (Exception e) {
            return "description impossible : " + e;
        }
    }

    /** Un pont s'abonne aux ouvertures et fermetures. */
    public static void onOuverture(BiConsumer<ServerPlayerEntity, DialogueSession> action) {
        A_L_OUVERTURE.add(action);
    }

    public static void onFermeture(BiConsumer<ServerPlayerEntity, DialogueSession> action) {
        A_LA_FERMETURE.add(action);
    }

    /** Un mod de transport dit si son véhicule bouge. */
    public static void sondeMouvement(Function<Entity, Optional<Boolean>> sonde) {
        SONDES_MOUVEMENT.add(sonde);
    }

    public static void retirerSondeMouvement(Function<Entity, Optional<Boolean>> sonde) {
        SONDES_MOUVEMENT.remove(sonde);
    }

    // ------------------------------------------------------------------------
    // Lecture
    // ------------------------------------------------------------------------

    public static Optional<DialogueSession> session(PlayerEntity joueur) {
        return joueur == null ? Optional.empty() : Optional.ofNullable(SESSIONS.get(joueur.getUuid()));
    }

    public static boolean enSession(PlayerEntity joueur) {
        return joueur != null && SESSIONS.containsKey(joueur.getUuid());
    }

    public static Collection<DialogueSession> toutes() {
        return Collections.unmodifiableCollection(SESSIONS.values());
    }

    /** Une autre personne parle-t-elle déjà à ce personnage ? */
    public static boolean pnjOccupe(UUID pnj, UUID saufJoueur) {
        for (DialogueSession s : SESSIONS.values()) {
            if (s.pnj.equals(pnj) && !s.joueur.equals(saufJoueur)) {
                return true;
            }
        }
        return false;
    }

    // ------------------------------------------------------------------------
    // Véhicules
    // ------------------------------------------------------------------------

    /**
     * Le personnage est-il <i>porté</i> — passager d'un véhicule, ou épinglé sans
     * IA sur un pont comme le capitaine d'un ferry ? Un personnage qui marche
     * de lui-même n'est pas un véhicule : il garde le dialogue complet.
     */
    static boolean porte(Entity pnj) {
        return pnj.hasVehicle() || (pnj instanceof MobEntity mob && mob.isAiDisabled());
    }

    private static Optional<Boolean> sonder(Entity pnj) {
        for (Function<Entity, Optional<Boolean>> sonde : SONDES_MOUVEMENT) {
            try {
                Optional<Boolean> reponse = sonde.apply(pnj);
                if (reponse != null && reponse.isPresent()) {
                    return reponse;
                }
            } catch (Exception e) {
                HauteCapitaleDialogue.LOGGER.warn("Sonde de mouvement en échec : {}", e.toString());
            }
        }
        return Optional.empty();
    }

    /** Déplacement du dernier tick, en blocs : le personnage et, s'il en a un, son véhicule. */
    static double vitesseInstantanee(Entity pnj) {
        Entity racine = pnj.getRootVehicle();
        double v = racine.getEntityPos().distanceTo(new Vec3d(racine.lastX, racine.lastY, racine.lastZ));
        if (racine != pnj) {
            v = Math.max(v, pnj.getEntityPos().distanceTo(new Vec3d(pnj.lastX, pnj.lastY, pnj.lastZ)));
        }
        return v;
    }

    /** Le véhicule du personnage bouge-t-il en ce moment ? Sondes d'abord, physique ensuite. */
    public static boolean enMouvementMaintenant(Entity pnj) {
        Optional<Boolean> sonde = sonder(pnj);
        if (sonde.isPresent()) {
            return sonde.get();
        }
        return porte(pnj) && vitesseInstantanee(pnj) > DialogueConfig.get().vehicule_vitesse_mouvement;
    }

    /**
     * La conversation est-elle interdite maintenant ? Deux causes : le
     * personnage est exclusif et parle déjà à quelqu'un d'autre ; son véhicule
     * bouge et la politique est « refuser ». Les ponts appellent ceci avant
     * d'ouvrir quoi que ce soit, pour ne pas laisser Easy NPC ouvrir son propre
     * écran à la place — un refus est un refus, pas un écran de repli.
     */
    public static Optional<Text> refus(ServerPlayerEntity joueur, Entity pnj) {
        DialogueConfig cfg = DialogueConfig.get();
        if (SessionFlags.a(cfg.flags(pnj), SessionFlags.EXCLUSIF) && pnjOccupe(pnj.getUuid(), joueur.getUuid())) {
            return Optional.of(Text.translatableWithFallback("hcd.session.exclusif",
                    "%s est déjà en conversation avec quelqu'un.", pnj.getDisplayName()).formatted(Formatting.GRAY));
        }
        if (cfg.politiqueVehicule() == DialogueConfig.PolitiqueVehicule.REFUSER && enMouvementMaintenant(pnj)) {
            return Optional.of(messageEnRoute(pnj));
        }
        return Optional.empty();
    }

    private static Text messageEnRoute(Entity pnj) {
        return Text.translatableWithFallback("hcd.session.en_mouvement",
                "%s ne peut pas vous parler en route.", pnj.getDisplayName()).formatted(Formatting.GRAY);
    }

    // ------------------------------------------------------------------------
    // Ouverture
    // ------------------------------------------------------------------------

    /**
     * Ouvre une session, ou rafraîchit celle qui existe déjà avec ce personnage.
     *
     * <p>Rafraîchir plutôt que rouvrir : un dialogue Easy NPC qui enchaîne
     * plusieurs nœuds rappelle ce point d'entrée à chaque nœud, et la caméra ne
     * doit pas repartir de zéro à chaque réplique.
     *
     * @return la session, ou {@code null} si le serveur a refusé
     */
    public static DialogueSession ouvrir(ServerPlayerEntity joueur, Entity pnj, DialogueMode mode) {
        DialogueConfig cfg = DialogueConfig.get();
        long tick = joueur.getEntityWorld().getServer().getTicks();

        DialogueSession existante = SESSIONS.get(joueur.getUuid());
        if (existante != null) {
            if (existante.pnj.equals(pnj.getUuid()) && existante.mode == mode) {
                existante.toucher(tick);
                return existante;
            }
            fermer(joueur, CloseReason.REMPLACEE);
        }

        if (joueur.isSpectator() || !pnj.isAlive()) {
            return null;
        }
        double distance = cfg.distance_ouverture;
        if (joueur.squaredDistanceTo(pnj) > distance * distance) {
            return null;
        }

        int flags = cfg.flags(pnj);
        Optional<Text> refus = refus(joueur, pnj);
        if (refus.isPresent()) {
            dire(joueur, refus.get());
            return null;
        }
        boolean enMouvement = enMouvementMaintenant(pnj);
        if (enMouvement && cfg.politiqueVehicule() == DialogueConfig.PolitiqueVehicule.SIMPLIFIE) {
            flags |= SessionFlags.SIMPLIFIE;
        }

        DialogueSession session = new DialogueSession(
                prochainId++, joueur.getUuid(), pnj.getUuid(), pnj.getId(),
                pnj.getDisplayName().getString(), joueur.getEntityWorld().getRegistryKey(),
                mode, cfg.profil(pnj), flags, tick, joueur.getEntityPos());
        session.dernierePosPnj = pnj.getEntityPos();
        session.derniereRelative = joueur.getEntityPos().subtract(pnj.getEntityPos());
        session.mouvement = enMouvement ? MouvementDetecteur.Etat.EN_ROUTE : MouvementDetecteur.Etat.ARRET;
        SESSIONS.put(joueur.getUuid(), session);
        // Une conversation neuve rend caduque toute reprise en attente.
        REPRISES.remove(joueur.getUuid());

        // Le paquet part AVANT que le pont n'ouvre son menu ou ne lance sa
        // fonction : le client doit savoir qu'il est en conversation quand
        // l'écran, ou la première réplique, lui arrive.
        DialogueNetwork.envoyerOuverture(joueur, session);

        if (session.a(SessionFlags.VERROU_JOUEUR)) {
            verrouiller(joueur, session);
        }
        for (BiConsumer<ServerPlayerEntity, DialogueSession> action : A_L_OUVERTURE) {
            try {
                action.accept(joueur, session);
            } catch (Exception e) {
                HauteCapitaleDialogue.LOGGER.warn("Action d'ouverture en échec pour {} : {}", session, e.toString());
            }
        }

        if (cfg.journaliser) {
            HauteCapitaleDialogue.LOGGER.info("Session ouverte : {} par {} [{}]",
                    session, joueur.getName().getString(), SessionFlags.decrire(flags));
        }
        return session;
    }

    // ------------------------------------------------------------------------
    // Fermeture
    // ------------------------------------------------------------------------

    public static void fermer(ServerPlayerEntity joueur, CloseReason raison) {
        DialogueSession session = SESSIONS.remove(joueur.getUuid());
        if (session == null) {
            return;
        }
        terminer(joueur, session, raison, true);
    }

    /** Déconnexion : plus personne à qui envoyer un paquet. */
    private static void oublier(ServerPlayerEntity joueur, CloseReason raison) {
        DialogueSession session = SESSIONS.remove(joueur.getUuid());
        if (session == null) {
            return;
        }
        terminer(joueur, session, raison, false);
    }

    private static void terminer(ServerPlayerEntity joueur, DialogueSession session,
                                 CloseReason raison, boolean prevenir) {
        if (session.verrouPose) {
            deverrouiller(joueur, session);
        }
        for (BiConsumer<ServerPlayerEntity, DialogueSession> action : A_LA_FERMETURE) {
            try {
                action.accept(joueur, session);
            } catch (Exception e) {
                HauteCapitaleDialogue.LOGGER.warn("Action de fermeture en échec pour {} : {}", session, e.toString());
            }
        }
        if (prevenir) {
            DialogueNetwork.envoyerFermeture(joueur, session, raison);
        }
        if (prevenir && session.mode == DialogueMode.EASYNPC_MENU && raison != CloseReason.ECRAN_FERME
                && joueur.currentScreenHandler != joueur.playerScreenHandler && joueur.networkHandler != null) {
            // La conversation est finie mais le menu d'Easy NPC est encore ouvert
            // cote serveur : on le referme, le client fermera le sien en echo.
            joueur.closeHandledScreen();
        }
        if (DialogueConfig.get().journaliser) {
            HauteCapitaleDialogue.LOGGER.info("Session fermée : {} de {} — {}",
                    session, joueur.getName().getString(), raison);
        }
        if (prevenir && raison == CloseReason.CINEMATIQUE && session.a(SessionFlags.REPRISE_APRES_CINEMATIQUE)
                && joueur.getEntityWorld().getServer() != null) {
            // La cinematique a pris la place ; on retient ou en etait la conversation
            // et on la rouvrira quand le client dira que c'est fini.
            long tick = joueur.getEntityWorld().getServer().getTicks();
            REPRISES.put(joueur.getUuid(), new RepriseEnAttente(session.id, session.pnj, session.mode,
                    session.dernierDialogue, tick + DialogueConfig.get().reprise_delai_max_ticks));
        } else {
            REPRISES.remove(joueur.getUuid());
        }
    }

    /**
     * Le client dit que la cinématique est finie : on reprend la conversation
     * qu'elle avait interrompue, si elle est encore reprenable — personnage
     * vivant et à portée, délai non écoulé, et rien d'autre ouvert entre-temps.
     */
    public static void onRepriseClient(ServerPlayerEntity joueur, int sessionId) {
        RepriseEnAttente attente = REPRISES.remove(joueur.getUuid());
        if (attente == null || attente.sessionId != sessionId || rouvreur == null) {
            return;
        }
        MinecraftServer serveur = joueur.getEntityWorld().getServer();
        if (serveur == null || serveur.getTicks() > attente.expireTick || joueur.isDead() || joueur.isSpectator()
                || enSession(joueur)) {
            return;
        }
        Entity pnj = joueur.getEntityWorld().getEntity(attente.pnj);
        double distance = DialogueConfig.get().distance_ouverture;
        if (pnj == null || !pnj.isAlive() || joueur.squaredDistanceTo(pnj) > distance * distance
                || refus(joueur, pnj).isPresent()) {
            return;
        }
        boolean rouverte;
        if (attente.mode == DialogueMode.CAPTURE) {
            rouverte = ouvrir(joueur, pnj, DialogueMode.CAPTURE) != null && rouvreur.rejouerInteraction(joueur, pnj);
        } else {
            rouverte = rouvreur.ouvrirDialogue(joueur, pnj, attente.dialogue);
        }
        if (DialogueConfig.get().journaliser) {
            HauteCapitaleDialogue.LOGGER.info("Reprise après cinématique pour {} avec {} : {}",
                    joueur.getName().getString(), pnj.getName().getString(), rouverte ? "rouverte" : "impossible");
        }
    }

    /**
     * Le client demande la fin : Échap, réponse de congé, cinématique.
     *
     * <p>On vérifie que la session est bien la sienne. Un identifiant périmé —
     * deux fermetures qui se croisent — est ignoré sans bruit.
     */
    public static void onFinClient(ServerPlayerEntity joueur, int sessionId, CloseReason raison) {
        DialogueSession session = SESSIONS.get(joueur.getUuid());
        if (session == null || session.id != sessionId) {
            return;
        }
        fermer(joueur, raison);
        // Si l'écran d'Easy NPC est encore ouvert côté serveur (le client l'a
        // fermé localement sans nous attendre), on le referme aussi.
        if (joueur.currentScreenHandler != joueur.playerScreenHandler
                && session.mode == DialogueMode.EASYNPC_MENU) {
            joueur.closeHandledScreen();
        }
    }

    // ------------------------------------------------------------------------
    // Surveillance
    // ------------------------------------------------------------------------

    private static void tick(MinecraftServer server) {
        if (SESSIONS.isEmpty()) {
            return;
        }
        DialogueConfig cfg = DialogueConfig.get();
        long tick = server.getTicks();
        if (tick % cfg.verification_ticks != 0) {
            return;
        }

        List<DialogueSession> copie = new ArrayList<>(SESSIONS.values());
        for (DialogueSession session : copie) {
            ServerPlayerEntity joueur = server.getPlayerManager().getPlayer(session.joueur);
            if (joueur == null) {
                SESSIONS.remove(session.joueur);
                continue;
            }

            CloseReason raison = verifier(cfg, tick, joueur, session);
            if (raison != null) {
                fermer(joueur, raison);
            }
        }
    }

    /** La raison de fermer, ou {@code null} si tout va bien. */
    private static CloseReason verifier(DialogueConfig cfg, long tick, ServerPlayerEntity joueur,
                                        DialogueSession session) {
        if (!joueur.getEntityWorld().getRegistryKey().equals(session.monde)) {
            return CloseReason.DIMENSION;
        }
        if (joueur.isDead()) {
            return CloseReason.MORT;
        }

        Entity pnj = joueur.getEntityWorld().getEntity(session.pnj);
        if (pnj == null || !pnj.isAlive()) {
            return CloseReason.PNJ_DISPARU;
        }

        double maintien = cfg.distance_maintien;
        if (joueur.squaredDistanceTo(pnj) > maintien * maintien) {
            return CloseReason.DISTANCE;
        }

        // Teleportation : c'est la position *relative* au personnage qui saute.
        // Deux voyageurs sur le meme pont ne bougent pas l'un par rapport a l'autre.
        Vec3d position = joueur.getEntityPos();
        Vec3d posPnj = pnj.getEntityPos();
        Vec3d relative = position.subtract(posPnj);
        if (session.derniereRelative != null
                && relative.squaredDistanceTo(session.derniereRelative) > cfg.saut_teleportation * cfg.saut_teleportation) {
            return CloseReason.TELEPORTATION;
        }
        session.dernierePosition = position;
        session.derniereRelative = relative;

        CloseReason mouvement = surveillerVehicule(cfg, joueur, session, pnj, posPnj);
        if (mouvement != null) {
            return mouvement;
        }

        if (session.mode == DialogueMode.CAPTURE && cfg.capture_fermeture_inactivite
                && tick - session.derniereActiviteTick > cfg.capture_inactivite_max_ticks) {
            return CloseReason.INACTIVITE;
        }

        if (session.mode == DialogueMode.EASYNPC_MENU) {
            ScreenHandler handler = joueur.currentScreenHandler;
            boolean ecranOuvert = handler != joueur.playerScreenHandler;
            if (ecranOuvert) {
                session.ecranVu = true;
                session.ticksSansEcran = 0;
                if (!menuDeDialogue.test(handler)) {
                    // Commerce, atelier : un ecran ouvert depuis une reponse a pris
                    // la place du dialogue. La conversation continue derriere.
                    session.ecranEtrangerVu = true;
                }
            } else if (session.ecranVu || session.age(tick) > cfg.grace_ecran_ticks) {
                // Entre deux ecrans d'Easy NPC (un noeud qui se ferme, le commerce qui
                // s'ouvre au tick suivant) le joueur n'a rien pendant un tick ou deux :
                // ce n'est pas une fermeture.
                session.ticksSansEcran += cfg.verification_ticks;
                if (session.ticksSansEcran < cfg.fermeture_ecran_ticks) {
                    return null;
                }
                if (session.ecranEtrangerVu && session.a(SessionFlags.RETOUR_APRES_COMMERCE) && rouvreur != null) {
                    // L'ecran etranger vient de se fermer : retour au noeud d'ou il a ete ouvert.
                    session.ecranEtrangerVu = false;
                    session.toucher(tick);
                    if (rouvreur.ouvrirDialogue(joueur, pnj, session.dernierDialogue)) {
                        if (cfg.journaliser) {
                            HauteCapitaleDialogue.LOGGER.info("Session {} : retour au dialogue après un commerce ou un atelier.",
                                    session.id);
                        }
                        return null;
                    }
                }
                return CloseReason.ECRAN_FERME;
            }
        }
        return null;
    }

    /**
     * Le véhicule du personnage part ou s'arrête pendant la conversation.
     *
     * <p>« simplifié » : le drapeau change et le client reçoit la session à
     * nouveau — il rend la caméra en fondu, ou la reprend. « refuser » : la
     * conversation se ferme dès le départ. « complet » : rien ne change.
     */
    private static CloseReason surveillerVehicule(DialogueConfig cfg, ServerPlayerEntity joueur,
                                                  DialogueSession session, Entity pnj, Vec3d posPnj) {
        double vitesse = session.dernierePosPnj == null ? 0.0
                : posPnj.distanceTo(session.dernierePosPnj) / Math.max(1, cfg.verification_ticks);
        session.dernierePosPnj = posPnj;

        MouvementDetecteur.Etat suivant;
        Optional<Boolean> sonde = sonder(pnj);
        if (sonde.isPresent()) {
            suivant = sonde.get() ? MouvementDetecteur.Etat.EN_ROUTE : MouvementDetecteur.Etat.ARRET;
        } else if (!porte(pnj)) {
            suivant = MouvementDetecteur.Etat.ARRET;
        } else {
            suivant = MouvementDetecteur.suivant(session.mouvement, vitesse, cfg.vehicule_vitesse_mouvement,
                    cfg.vehicule_ticks_arret, cfg.verification_ticks);
        }
        boolean avant = session.mouvement.enMouvement();
        session.mouvement = suivant;
        if (suivant.enMouvement() == avant) {
            return null;
        }

        switch (cfg.politiqueVehicule()) {
            case REFUSER -> {
                if (suivant.enMouvement()) {
                    dire(joueur, messageEnRoute(pnj));
                    return CloseReason.MOUVEMENT;
                }
            }
            case SIMPLIFIE -> {
                session.flags = suivant.enMouvement()
                        ? session.flags | SessionFlags.SIMPLIFIE
                        : session.flags & ~SessionFlags.SIMPLIFIE;
                DialogueNetwork.envoyerOuverture(joueur, session);
                if (cfg.journaliser) {
                    HauteCapitaleDialogue.LOGGER.info("Session {} : véhicule {} ({} b/t) → [{}]",
                            session.id, suivant.enMouvement() ? "en route" : "à l'arrêt",
                            String.format(Locale.ROOT, "%.3f", vitesse), SessionFlags.decrire(session.flags));
                }
            }
            default -> {
            }
        }
        return null;
    }

    // ------------------------------------------------------------------------
    // Verrou de déplacement
    // ------------------------------------------------------------------------

    private static void verrouiller(ServerPlayerEntity joueur, DialogueSession session) {
        modifier(joueur, EntityAttributes.MOVEMENT_SPEED, VERROU_VITESSE);
        modifier(joueur, EntityAttributes.JUMP_STRENGTH, VERROU_SAUT);
        session.verrouPose = true;
    }

    private static void deverrouiller(ServerPlayerEntity joueur, DialogueSession session) {
        retirer(joueur, EntityAttributes.MOVEMENT_SPEED, VERROU_VITESSE);
        retirer(joueur, EntityAttributes.JUMP_STRENGTH, VERROU_SAUT);
        session.verrouPose = false;
    }

    private static void modifier(ServerPlayerEntity joueur, RegistryEntry<EntityAttribute> attribut, Identifier id) {
        EntityAttributeInstance instance = joueur.getAttributeInstance(attribut);
        if (instance != null && !instance.hasModifier(id)) {
            // -100 % du total : le joueur ne bouge plus, sans toucher a la valeur
            // de base ni a ce que d'autres mods y ont pose. Temporaire : jamais
            // ecrit dans la sauvegarde, donc aucun joueur fige apres un crash.
            instance.addTemporaryModifier(new EntityAttributeModifier(
                    id, -1.0, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        }
    }

    private static void retirer(ServerPlayerEntity joueur, RegistryEntry<EntityAttribute> attribut, Identifier id) {
        EntityAttributeInstance instance = joueur.getAttributeInstance(attribut);
        if (instance != null) {
            instance.removeModifier(id);
        }
    }

    // ------------------------------------------------------------------------

    /** Un joueur dont la connexion est en train de tomber n'a plus de {@code networkHandler}. */
    public static void dire(ServerPlayerEntity joueur, Text texte) {
        if (joueur != null && joueur.networkHandler != null) {
            joueur.sendMessage(texte, false);
        }
    }
}
