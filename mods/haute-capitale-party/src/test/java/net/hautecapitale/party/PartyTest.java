package net.hautecapitale.party;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import net.hautecapitale.party.invite.Invites;
import net.hautecapitale.party.party.Party;
import net.hautecapitale.party.party.PartyType;
import net.hautecapitale.party.party.Role;
import net.hautecapitale.party.request.Countdown;
import net.hautecapitale.party.request.Poll;
import net.hautecapitale.party.client.HudLayout;
import net.hautecapitale.party.client.HudSettings;
import net.hautecapitale.party.network.HudPayload;
import net.minecraft.nbt.NbtOps;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Harnais de verification hors jeu.
 *
 * <p>Un {@code main()} classique, pas du JUnit : branche sur {@code check} par la
 * tache {@code runPartyTest}, il fait echouer un {@code build} des qu'un codec ou
 * une regle du modele casse.
 *
 * <p>Ce qui demande un serveur — equipes de scoreboard, magasin persistant,
 * diffusion — est verifie en jeu par {@code /party diagnostic}. Ici on ne teste
 * que ce qui tient sans monde : le modele et la serialisation.
 */
public final class PartyTest {

    private static int passed;
    private static int failed;

    public static void main(String[] args) {
        modele();
        succession();
        roles();
        codecs();
        retroCompatibilite();
        vocabulaireTolerant();
        invitations();
        seuilsDeVote();
        verificationDePreparation();
        voteExclusion();
        compteARebours();
        miseEnPageHud();
        couleursOpaques();
        outilsHud();

        System.out.println();
        System.out.println("Harnais groupe : " + passed + "/" + (passed + failed) + " verifications reussies.");
        if (failed > 0) {
            throw new AssertionError(failed + " verification(s) en echec.");
        }
    }

    // --- modele --------------------------------------------------------------

    private static void modele() {
        UUID founder = UUID.randomUUID();
        Party party = Party.found(founder, "Arthur", PartyType.DUNGEON_5, 1000L);

        check("le fondateur est chef", party.isLeader(founder));
        check("groupe a 1 membre", party.size() == 1);
        check("epoch initial a 0", party.epoch() == 0);
        check("nom retenu", party.nameOf(founder).equals("Arthur"));

        UUID second = UUID.randomUUID();
        check("ajout d'un membre", party.add(second, "Bertrand"));
        check("epoch incremente a l'ajout", party.epoch() == 1);
        check("ajout en double sans effet", !party.add(second, "Bertrand"));
        check("epoch inchange apres un ajout sans effet", party.epoch() == 1);

        check("ordre d'anciennete respecte", party.memberIds().equals(List.of(founder, second)));

        check("retrait d'un membre", party.remove(second));
        check("epoch incremente au retrait", party.epoch() == 2);
        check("retrait d'un absent sans effet", !party.remove(second));

        // Le role ne change pas la composition : l'epoch ne doit pas bouger, sans quoi
        // un simple changement de role annulerait toutes les demandes en cours.
        int before = party.epoch();
        party.setRole(founder, Role.TANK);
        check("le role ne touche pas a l'epoch", party.epoch() == before);
    }

    private static void succession() {
        UUID a = UUID.randomUUID();
        UUID b = UUID.randomUUID();
        UUID c = UUID.randomUUID();
        Party party = Party.found(a, "A", PartyType.DUNGEON_5, 0L);
        party.add(b, "B");
        party.add(c, "C");

        check("successeur = le plus ancien restant",
                party.nextLeaderAfter(a).equals(Optional.of(b)));

        check("transfert vers un membre", party.transferTo(c));
        check("le chef a change", party.isLeader(c));
        check("transfert vers un non-membre refuse", !party.transferTo(UUID.randomUUID()));
        check("transfert vers le chef actuel refuse", !party.transferTo(c));

        // Le meme calcul doit donner le meme resultat quel que soit le chef du moment :
        // c'est ce qui rend la succession reproductible apres un redemarrage.
        check("succession independante du chef courant",
                party.nextLeaderAfter(c).equals(Optional.of(a)));

        Party solo = Party.found(a, "A", PartyType.DUNGEON_5, 0L);
        check("pas de successeur dans un groupe d'une personne",
                solo.nextLeaderAfter(a).isEmpty());
    }

    private static void roles() {
        check("analyse d'un role", Role.parse("heal").equals(Optional.of(Role.HEAL)));
        check("analyse insensible a la casse", Role.parse("TANK").equals(Optional.of(Role.TANK)));
        check("role inconnu rejete", Role.parse("barde").isEmpty());
        check("role nul rejete", Role.parse(null).isEmpty());
        check("chaque role a un symbole",
                java.util.Arrays.stream(Role.values()).allMatch(r -> !r.symbol().isBlank()));

        check("analyse d'un type", PartyType.parse("raid").equals(Optional.of(PartyType.RAID)));
        check("donjon plafonne a 5", PartyType.DUNGEON_5.maxSize() == 5);
    }

    // --- serialisation -------------------------------------------------------

    /**
     * Les deux formats comptent.
     *
     * <p>NBT est celui qui sert reellement a la sauvegarde ; JSON sert au diagnostic
     * et a la lecture humaine. Un codec qui ne passe qu'en JSON casse en production,
     * et c'est exactement ce que produit une carte a cles non textuelles — raison
     * pour laquelle le modele n'en contient aucune.
     */
    private static void codecs() {
        UUID a = UUID.randomUUID();
        UUID b = UUID.randomUUID();
        Party party = Party.found(a, "Arthur", PartyType.RAID, 4242L);
        party.add(b, "Bertrand");
        party.setRole(a, Role.TANK);
        party.setRole(b, Role.HEAL);
        party.setPreviousTeam(b, Optional.of("equipe_rp"));

        roundTrip("JSON", JsonOps.INSTANCE, party);
        roundTrip("NBT", NbtOps.INSTANCE, party);

        // Une liste de groupes, comme celle que le magasin ecrit reellement.
        Codec<List<Party>> listCodec = Party.CODEC.listOf();
        DataResult<?> encoded = listCodec.encodeStart(NbtOps.INSTANCE, List.of(party));
        check("liste de groupes encodable en NBT", encoded.result().isPresent());
    }

    private static <T> void roundTrip(String label, DynamicOps<T> ops, Party original) {
        DataResult<T> encoded = Party.CODEC.encodeStart(ops, original);
        if (encoded.result().isEmpty()) {
            check("encodage " + label, false);
            System.out.println("      " + encoded.error().map(DataResult.Error::message).orElse("?"));
            return;
        }
        check("encodage " + label, true);

        DataResult<Party> decoded = Party.CODEC.parse(ops, encoded.result().get());
        if (decoded.result().isEmpty()) {
            check("decodage " + label, false);
            System.out.println("      " + decoded.error().map(DataResult.Error::message).orElse("?"));
            return;
        }
        Party copy = decoded.result().get();
        check("decodage " + label, true);
        check(label + " : identifiant conserve", copy.id().equals(original.id()));
        check(label + " : chef conserve", copy.leader().equals(original.leader()));
        check(label + " : membres et ordre conserves", copy.memberIds().equals(original.memberIds()));
        check(label + " : roles conserves",
                copy.roleOf(original.leader()) == original.roleOf(original.leader()));
        check(label + " : type conserve", copy.type() == original.type());
        check(label + " : epoch conserve", copy.epoch() == original.epoch());
        check(label + " : noms conserves",
                copy.nameOf(original.leader()).equals(original.nameOf(original.leader())));
        check(label + " : equipe d'origine conservee",
                copy.previousTeamOf(original.memberIds().get(1))
                        .equals(original.previousTeamOf(original.memberIds().get(1))));
    }

    /**
     * Une sauvegarde ecrite avant l'ajout d'un champ doit continuer a se relire.
     *
     * <p>Tous les champs ajoutes apres coup sont optionnels avec valeur de repli ;
     * sans cela, la premiere mise a jour du mod ferait perdre tous les groupes.
     */
    private static void retroCompatibilite() {
        UUID id = UUID.randomUUID();
        UUID member = UUID.randomUUID();

        com.google.gson.JsonObject entry = new com.google.gson.JsonObject();
        entry.add("uuid", intArray(member));

        com.google.gson.JsonArray members = new com.google.gson.JsonArray();
        members.add(entry);

        com.google.gson.JsonObject json = new com.google.gson.JsonObject();
        json.add("id", intArray(id));
        json.add("leader", intArray(member));
        json.add("members", members);

        DataResult<Party> decoded = Party.CODEC.parse(JsonOps.INSTANCE, json);
        check("groupe minimal relu", decoded.result().isPresent());
        decoded.result().ifPresent(party -> {
            check("type par defaut", party.type() == PartyType.DUNGEON_5);
            check("epoch par defaut", party.epoch() == 0);
            check("role par defaut", party.roleOf(member) == Role.UNSET);
            check("nom par defaut", party.nameOf(member).equals("?"));
            check("aucune equipe d'origine", party.previousTeamOf(member).isEmpty());
        });
    }

    private static com.google.gson.JsonArray intArray(UUID uuid) {
        long most = uuid.getMostSignificantBits();
        long least = uuid.getLeastSignificantBits();
        com.google.gson.JsonArray array = new com.google.gson.JsonArray();
        array.add((int) (most >> 32));
        array.add((int) most);
        array.add((int) (least >> 32));
        array.add((int) least);
        return array;
    }

    /** Un vocabulaire inconnu ne doit jamais faire perdre un groupe. */
    private static void vocabulaireTolerant() {
        DataResult<Role> role = Role.CODEC.parse(JsonOps.INSTANCE,
                new com.google.gson.JsonPrimitive("necromancien"));
        check("role inconnu ramene a UNSET",
                role.result().isPresent() && role.result().get() == Role.UNSET);

        DataResult<PartyType> type = PartyType.CODEC.parse(JsonOps.INSTANCE,
                new com.google.gson.JsonPrimitive("mythique"));
        check("type inconnu ramene au donjon",
                type.result().isPresent() && type.result().get() == PartyType.DUNGEON_5);
    }

    // --- invitations ---------------------------------------------------------

    private static void invitations() {
        Invites invites = new Invites();
        UUID invitee = UUID.randomUUID();
        UUID inviter = UUID.randomUUID();
        UUID partyId = UUID.randomUUID();
        long t0 = 1_000_000L;

        invites.put(invitee, new Invites.Invite(partyId, inviter, 3, t0 + 60_000L));
        check("invitation retrouvee", invites.get(invitee, t0).isPresent());
        check("invitation perimee ignoree", invites.get(invitee, t0 + 60_001L).isEmpty());

        invites.put(invitee, new Invites.Invite(partyId, inviter, 3, t0 + 60_000L));
        check("consommation rend l'invitation", invites.take(invitee, t0).isPresent());
        check("une invitation ne sert qu'une fois", invites.take(invitee, t0).isEmpty());

        // Anti-spam (regle §49).
        check("pas de delai au depart", invites.cooldownRemaining(inviter, t0) == 0L);
        invites.noteInvite(inviter, t0, 5);
        check("delai actif juste apres", invites.cooldownRemaining(inviter, t0) == 5L);
        check("delai ecoule", invites.cooldownRemaining(inviter, t0 + 5_000L) == 0L);
        invites.noteInvite(inviter, t0, 0);
        check("delai desactivable", invites.cooldownRemaining(inviter, t0) == 0L);

        // Une dissolution doit emporter les invitations vers ce groupe.
        invites.put(invitee, new Invites.Invite(partyId, inviter, 3, t0 + 60_000L));
        invites.clearForParty(partyId);
        check("invitations effacees a la dissolution", invites.get(invitee, t0).isEmpty());

        invites.put(invitee, new Invites.Invite(partyId, inviter, 3, t0 + 60_000L));
        invites.forget(invitee);
        check("deconnexion oublie l'invitation", invites.get(invitee, t0).isEmpty());
    }

    // --- consultations du groupe --------------------------------------------

    /**
     * La regle de seuil, verifiee chiffre par chiffre.
     *
     * <p>C'est le calcul le plus facile a se tromper de tout le mod, et celui dont
     * une erreur se verrait le plus mal : un seuil trop bas exclut des joueurs a
     * tort, un seuil trop haut rend le vote inutilisable.
     */
    private static void seuilsDeVote() {
        check("2 votants exigent 2 voix", Poll.majorityOf(2) == 2);
        check("3 votants exigent 2 voix", Poll.majorityOf(3) == 2);
        check("4 votants exigent 3 voix", Poll.majorityOf(4) == 3);
        check("5 votants exigent 3 voix", Poll.majorityOf(5) == 3);
        check("6 votants exigent 4 voix", Poll.majorityOf(6) == 4);

        // Le point qui compte : avec un seuil de majorite stricte, un partage exact
        // ne peut jamais valoir acceptation.
        for (int voters = 2; voters <= 10; voters++) {
            int needed = Poll.majorityOf(voters);
            check("aucune egalite decisionnelle a " + voters + " votants", needed * 2 > voters);
        }
    }

    private static void verificationDePreparation() {
        UUID a = UUID.randomUUID();
        UUID b = UUID.randomUUID();
        UUID c = UUID.randomUUID();
        List<UUID> membres = List.of(a, b, c);
        long t0 = 1_000L;

        Poll poll = Poll.readyCheck(UUID.randomUUID(), 7, membres, t0 + 30_000L);
        check("tout le monde vote", poll.voters().size() == 3);
        check("epoch memorise", poll.partyEpoch() == 7);
        check("en attente au depart", poll.outcome(t0) == Poll.Outcome.PENDING);

        poll.answer(a, true);
        poll.answer(b, true);
        check("toujours en attente tant qu'il manque une reponse",
                poll.outcome(t0) == Poll.Outcome.PENDING);
        poll.answer(c, true);
        check("tous prets", poll.outcome(t0) == Poll.Outcome.PASSED);

        // Un seul « pas pret » suffit a faire echouer la verification.
        Poll autre = Poll.readyCheck(UUID.randomUUID(), 0, membres, t0 + 30_000L);
        autre.answer(a, true);
        autre.answer(b, false);
        autre.answer(c, true);
        check("un refus fait echouer", autre.outcome(t0) == Poll.Outcome.FAILED);
        check("le compte des prets est juste", autre.yesCount() == 2);

        // Le silence vaut « pas pret » a l'expiration (regle §5).
        Poll muette = Poll.readyCheck(UUID.randomUUID(), 0, membres, t0 + 30_000L);
        muette.answer(a, true);
        check("en attente avant l'echeance", muette.outcome(t0) == Poll.Outcome.PENDING);
        check("expiree apres l'echeance", muette.outcome(t0 + 30_001L) == Poll.Outcome.FAILED);
        check("deux joueurs n'ont pas repondu", muette.pending().size() == 2);
    }

    private static void voteExclusion() {
        UUID chef = UUID.randomUUID();
        UUID cible = UUID.randomUUID();
        UUID x = UUID.randomUUID();
        UUID y = UUID.randomUUID();
        UUID z = UUID.randomUUID();
        List<UUID> membres = List.of(chef, cible, x, y, z);
        long t0 = 1_000L;

        Poll poll = Poll.kickVote(UUID.randomUUID(), 3, membres, cible, chef, t0 + 45_000L);

        check("la cible ne vote pas sur son sort", !poll.isVoter(cible));
        check("quatre votants sur cinq membres", poll.voters().size() == 4);
        check("seuil a 3", poll.threshold() == 3);
        check("la voix du chef est deja comptee", poll.hasAnswered(chef));
        check("et c'est un oui", poll.yesCount() == 1);
        check("la cible ne peut pas voter", !poll.answer(cible, false));

        check("en attente au depart", poll.outcome(t0) == Poll.Outcome.PENDING);
        poll.answer(x, true);
        check("deux voix ne suffisent pas", poll.outcome(t0) == Poll.Outcome.PENDING);
        check("on ne vote pas deux fois", !poll.answer(x, false));
        poll.answer(y, true);
        check("trois voix emportent la decision", poll.outcome(t0) == Poll.Outcome.PASSED);

        // Seuil devenu inatteignable : inutile de faire attendre le groupe 45 s.
        Poll perdu = Poll.kickVote(UUID.randomUUID(), 0, membres, cible, chef, t0 + 45_000L);
        perdu.answer(x, false);
        check("un refus laisse encore une chance", perdu.outcome(t0) == Poll.Outcome.PENDING);
        perdu.answer(y, false);
        check("deux refus rendent le seuil inatteignable",
                perdu.outcome(t0) == Poll.Outcome.FAILED);

        // Abstention = refus : a l'expiration, le statu quo l'emporte (regle §15).
        Poll silencieux = Poll.kickVote(UUID.randomUUID(), 0, membres, cible, chef, t0 + 45_000L);
        silencieux.answer(x, true);
        check("deux oui, deux silences : en attente",
                silencieux.outcome(t0) == Poll.Outcome.PENDING);
        check("a l'expiration, le silence fait echouer",
                silencieux.outcome(t0 + 45_001L) == Poll.Outcome.FAILED);

        check("la consultation sait qui elle vise", poll.targets(cible));
        check("et qui elle ne vise pas", !poll.targets(x));

        // Groupe de quatre : trois votants, seuil a deux, le chef en apporte une.
        List<UUID> petit = List.of(chef, cible, x, y);
        Poll petitVote = Poll.kickVote(UUID.randomUUID(), 0, petit, cible, chef, t0 + 45_000L);
        check("trois votants dans un groupe de quatre", petitVote.voters().size() == 3);
        check("seuil a 2", petitVote.threshold() == 2);
        petitVote.answer(x, true);
        check("une voix de plus suffit", petitVote.outcome(t0) == Poll.Outcome.PASSED);
    }

    private static void compteARebours() {
        UUID partyId = UUID.randomUUID();
        Countdown countdown = new Countdown(partyId, 3);

        check("duree retenue", countdown.totalSeconds() == 3);
        check("trois secondes au depart", countdown.secondsLeft() == 3);
        check("pas encore fini", !countdown.isFinished());

        // Le premier tick doit annoncer la valeur de depart, pas la sauter.
        check("premiere annonce a 3", countdown.tick() == 3);

        int annonces = 1;
        int derniere = 3;
        for (int i = 0; i < 3 * 20; i++) {
            int valeur = countdown.tick();
            if (valeur >= 0) {
                annonces++;
                derniere = valeur;
            }
        }
        check("une annonce par seconde plus le zero", annonces == 4);
        check("la derniere annonce est zero", derniere == 0);
        check("termine", countdown.isFinished());
        check("plus rien a annoncer ensuite", countdown.tick() == -1);
    }

    // --- HUD -----------------------------------------------------------------

    /** Six pixels par caractere : la regle fixe qui remplace la police en test. */
    private static final java.util.function.ToIntFunction<String> WIDTH = s -> s.length() * 6;

    private static HudPayload.Member membre(String nom, float pv, float pvMax, int distance, int niveau,
                                            int role, String classe,
                                            boolean chef, boolean vivant, boolean enLigne, boolean soi) {
        return new HudPayload.Member(nom, pv, pvMax, distance, niveau, (byte) role, classe,
                HudPayload.Member.flags(chef, vivant, enLigne, soi));
    }

    private static HudPayload groupeTemoin() {
        return new HudPayload(List.of(
                membre("Arthur", 20f, 20f, 0, 34, 1, "Paladin", true, true, true, true),
                membre("Bertrand", 8f, 20f, 12, 30, 3, "Voleur", false, true, true, false),
                membre("Clemence", 0f, 20f, 3, 33, 2, "Prêtre", false, false, true, false),
                membre("Damien", 0f, 0f, -1, -1, 3, "", false, true, false, false),
                membre("Eleonore", 15f, 30f, -1, 12, 0, "Barde", false, true, true, false)));
    }

    private static void miseEnPageHud() {
        HudSettings reglages = new HudSettings();
        HudLayout.Layout layout = HudLayout.build(groupeTemoin(), reglages, 800, 450, WIDTH);

        check("un bloc non vide", !layout.isEmpty());
        check("largeur fixe", layout.width() == HudLayout.WIDTH);
        check("hauteur = en-tete + 5 membres",
                layout.height() == HudLayout.PADDING + HudLayout.HEADER_HEIGHT
                        + 5 * HudLayout.MEMBER_HEIGHT + HudLayout.PADDING - 3);
        check("ancre haut-gauche par defaut", layout.originX() == 4f && layout.originY() == 4f);

        List<String> textes = layout.labels().stream().map(HudLayout.Label::text).toList();
        check("en-tete avec le compte des connectes", textes.contains("Groupe  4/5 en ligne"));
        check("la couronne du chef", textes.contains("♛"));
        check("le nom du chef", textes.contains("Arthur"));
        check("classe et niveau", textes.contains("Paladin 34"));
        check("vie entiere sans decimale", textes.contains("20 / 20"));
        check("vie fractionnaire lisible", textes.contains("8 / 20"));
        check("la morte est annoncee", textes.contains("MORT"));
        check("le deconnecte est annonce", textes.contains("hors ligne"));
        check("distance en metres", textes.contains("12 m"));
        check("autre dimension", textes.contains("ailleurs"));
        check("pas de distance pour soi", !textes.contains("0 m"));

        // Le premier rectangle est le fond, et il couvre tout le bloc.
        HudLayout.Rect fond = layout.rects().get(0);
        check("le fond couvre le bloc", fond.x1() == 0 && fond.y1() == 0
                && fond.x2() == layout.width() && fond.y2() == layout.height());

        // Barres : une par membre connecte (4), plus le remplissage de ceux qui ont de la vie.
        long barresFond = layout.rects().stream().filter(r -> r.argb() == HudLayout.BAR_BACK).count();
        check("une barre par membre connecte", barresFond == 4);
        long mortes = layout.rects().stream().filter(r -> r.argb() == HudLayout.BAR_DEAD).count();
        check("une barre grise pour la morte", mortes == 1);

        // Le remplissage suit la vie : 8/20 = 40 % de la largeur interieure.
        int interieur = HudLayout.WIDTH - 2 * HudLayout.PADDING;
        boolean remplissageBertrand = layout.rects().stream().anyMatch(r ->
                r.argb() == HudLayout.BAR_MID && (r.x2() - r.x1()) == Math.round(interieur * 0.4f));
        check("le remplissage suit le ratio de vie", remplissageBertrand);

        // Soi cache : quatre lignes seulement, et plus de couronne puisque le chef c'est soi.
        HudSettings sansSoi = new HudSettings();
        sansSoi.hideSelf = true;
        HudLayout.Layout reduit = HudLayout.build(groupeTemoin(), sansSoi, 800, 450, WIDTH);
        check("sa propre ligne peut etre masquee",
                reduit.height() == layout.height() - HudLayout.MEMBER_HEIGHT);
        check("et la couronne part avec elle",
                reduit.labels().stream().noneMatch(l -> l.text().equals("♛")));

        // Ancrages : le bloc reste dans l'ecran aux quatre coins, echelle comprise.
        for (HudSettings.Anchor ancre : HudSettings.Anchor.values()) {
            HudSettings s = new HudSettings();
            s.anchor = ancre.name();
            s.scale = 1.5f;
            HudLayout.Layout l = HudLayout.build(groupeTemoin(), s, 800, 450, WIDTH);
            boolean dansEcran = l.originX() >= 0 && l.originY() >= 0
                    && l.originX() + l.width() * l.scale() <= 800
                    && l.originY() + l.height() * l.scale() <= 450;
            check("ancre " + ancre + " dans l'ecran a l'echelle 1,5", dansEcran);
        }

        // Instantane vide : rien a dessiner, et surtout aucune exception.
        HudLayout.Layout vide = HudLayout.build(HudPayload.empty(), reglages, 800, 450, WIDTH);
        check("instantane vide = rien a dessiner", vide.isEmpty());
    }

    /**
     * Le test qui protege du HUD invisible.
     *
     * <p>En 1.21.11, {@code drawText} sort sans rien dessiner si l'alpha de la couleur
     * vaut zero, sans erreur ni avertissement. Un seul {@code 0xRRGGBB} oublie et le
     * texte disparait. Aucune couleur produite par la mise en page ne doit avoir un
     * alpha nul.
     */
    private static void couleursOpaques() {
        HudLayout.Layout layout = HudLayout.build(groupeTemoin(), new HudSettings(), 800, 450, WIDTH);
        boolean etiquettes = layout.labels().stream().allMatch(l -> (l.argb() >>> 24) == 0xFF);
        check("toutes les etiquettes sont pleinement opaques", etiquettes);
        boolean rectangles = layout.rects().stream().allMatch(r -> (r.argb() >>> 24) != 0);
        check("aucun rectangle a alpha nul", rectangles);
        check("opaque() force l'alpha", HudLayout.opaque(0x123456) == 0xFF123456);
        check("opaque() ecrase un alpha existant", HudLayout.opaque(0x00123456) == 0xFF123456);
    }

    private static void outilsHud() {
        check("vie entiere", HudLayout.formatHealth(20f).equals("20"));
        check("vie a la demi", HudLayout.formatHealth(7.5f).equals("7.5"));
        check("distance zero = rien", HudLayout.distanceText(0).isEmpty());
        check("distance negative = ailleurs", HudLayout.distanceText(-1).equals("ailleurs"));
        check("distance en metres", HudLayout.distanceText(42).equals("42 m"));
        check("barre verte au-dessus de 50 %", HudLayout.barColor(0.8f) == HudLayout.BAR_HIGH);
        check("barre jaune entre 25 et 50 %", HudLayout.barColor(0.4f) == HudLayout.BAR_MID);
        check("barre rouge sous 25 %", HudLayout.barColor(0.1f) == HudLayout.BAR_LOW);
        check("symboles de role alignes sur Role",
                HudLayout.roleSymbol((byte) Role.TANK.ordinal()).equals(Role.TANK.symbol())
                        && HudLayout.roleSymbol((byte) Role.HEAL.ordinal()).equals(Role.HEAL.symbol())
                        && HudLayout.roleSymbol((byte) Role.DPS.ordinal()).equals(Role.DPS.symbol())
                        && HudLayout.roleSymbol((byte) Role.UNSET.ordinal()).isEmpty());

        check("nom court intact", HudLayout.fit("Arthur", 100, WIDTH).equals("Arthur"));
        check("nom long tronque avec ellipse", HudLayout.fit("Bartholomeus", 36, WIDTH).equals("Barth…"));
        check("aucune place = rien", HudLayout.fit("Arthur", 0, WIDTH).isEmpty());

        HudPayload.Member sansNiveau = membre("X", 1, 1, 0, -1, 0, "Barde", false, true, true, false);
        check("classe seule si pas de niveau", HudLayout.classAndLevel(sansNiveau).equals("Barde"));
        HudPayload.Member sansClasse = membre("X", 1, 1, 0, 7, 0, "", false, true, true, false);
        check("niveau seul si pas de classe", HudLayout.classAndLevel(sansClasse).equals("niv. 7"));

        // La detection de changement du serveur repose sur equals() : un instantane
        // ou seule la vie a bouge doit etre vu comme different, un identique non.
        HudPayload avant = groupeTemoin();
        HudPayload identique = groupeTemoin();
        check("deux instantanes identiques sont egaux", avant.equals(identique));
        List<HudPayload.Member> blesse = new java.util.ArrayList<>(avant.members());
        HudPayload.Member arthur = blesse.get(0);
        blesse.set(0, new HudPayload.Member(arthur.name(), 13f, arthur.maxHealth(), arthur.distance(),
                arthur.level(), arthur.role(), arthur.className(), arthur.flags()));
        check("un demi-coeur de moins suffit a le rendre different",
                !avant.equals(new HudPayload(List.copyOf(blesse))));
        check("un instantane vide n'egale jamais un plein", !avant.equals(HudPayload.empty()));

        // Les drapeaux se relisent tels qu'ecrits.
        byte drapeaux = HudPayload.Member.flags(true, false, true, false);
        HudPayload.Member m = new HudPayload.Member("X", 1, 1, 0, 0, (byte) 0, "", drapeaux);
        check("drapeaux : chef", m.leader());
        check("drapeaux : pas vivant", !m.alive());
        check("drapeaux : en ligne", m.online());
        check("drapeaux : pas soi", !m.self());
    }

    // --- outillage -----------------------------------------------------------

    private static void check(String label, boolean condition) {
        if (condition) {
            passed++;
        } else {
            failed++;
            System.out.println("  ECHEC — " + label);
        }
    }
}
