package net.hautecapitale.dialogue;

import net.hautecapitale.dialogue.config.DialogueConfig;
import net.hautecapitale.dialogue.session.CameraProfile;
import net.hautecapitale.dialogue.session.CloseReason;
import net.hautecapitale.dialogue.session.DialogueMode;
import net.hautecapitale.dialogue.session.MouvementDetecteur;
import net.hautecapitale.dialogue.session.SessionFlags;
import net.minecraft.network.PacketByteBuf;
import io.netty.buffer.Unpooled;

import java.util.Set;

/**
 * Harnais de vérification de la logique pure — un {@code main()} classique.
 *
 * <p>Ce qui est vérifié ici ne se voit pas en jeu tant que ça ne casse pas :
 * la règle d'éligibilité, le codage des profils de caméra, les drapeaux, le
 * bornage de la configuration. Le cadrage, lui, est vérifié par le harnais
 * client ({@code CadrageTest}) parce qu'il dépend de classes client.
 */
public final class DialogueTest {

    private static int reussis = 0;
    private static int echecs = 0;

    public static void main(String[] args) {
        eligibilite();
        profil();
        drapeaux();
        vehicules();
        raisons();
        bornage();

        System.out.println();
        System.out.printf("  %d vérifications réussies, %d échecs%n", reussis, echecs);
        if (echecs > 0) {
            System.exit(1);
        }
    }

    // MARK: éligibilité

    private static void eligibilite() {
        section("Éligibilité d'un personnage");
        check("aucun tag, aucune surcharge → défaut vrai",
                DialogueConfig.decider(Set.of(), null, true, "hc_dialogue", "hc_dialogue_off"));
        check("aucun tag, aucune surcharge → défaut faux",
                !DialogueConfig.decider(Set.of(), null, false, "hc_dialogue", "hc_dialogue_off"));
        check("tag actif l'emporte sur un défaut faux",
                DialogueConfig.decider(Set.of("hc_dialogue"), null, false, "hc_dialogue", "hc_dialogue_off"));
        check("tag inactif l'emporte sur tout",
                !DialogueConfig.decider(Set.of("hc_dialogue", "hc_dialogue_off"), true, true, "hc_dialogue", "hc_dialogue_off"));
        check("surcharge fausse l'emporte sur le défaut vrai",
                !DialogueConfig.decider(Set.of("autre"), false, true, "hc_dialogue", "hc_dialogue_off"));
        check("surcharge vraie l'emporte sur le défaut faux",
                DialogueConfig.decider(Set.of(), true, false, "hc_dialogue", "hc_dialogue_off"));
        check("tags nuls tolérés",
                DialogueConfig.decider(null, null, true, "hc_dialogue", "hc_dialogue_off"));
    }

    // MARK: profil de caméra

    private static void profil() {
        section("Profil de caméra");
        CameraProfile p = new CameraProfile(1.5f, 1.75f, 1.35f, 0.8f, 10.0f, 6.0f, 1.5f, 5.0f, 0.15f, 0.85f, -0.4f, 350);

        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        p.write(buf);
        CameraProfile relu = CameraProfile.read(buf);
        check("le profil survit à l'aller-retour réseau", p.equals(relu));

        check("collé (1 bloc) : valeurs « proche »",
                proche(p.recul(1.0), 1.5) && proche(p.decalage(1.0), 1.35) && proche(p.visee(1.0), 10.0));
        check("3 blocs : à 43 % du chemin entre proche et loin",
                proche(p.recul(3.0), 1.6071) && proche(p.decalage(3.0), 1.1143) && proche(p.visee(3.0), 8.2857));
        check("loin (8 blocs) : valeurs « loin », bornées — le joueur ne sort jamais du cadre",
                proche(p.recul(8.0), 1.75) && proche(p.decalage(8.0), 0.8) && proche(p.visee(8.0), 6.0));
        CameraProfile net = new CameraProfile(1.0f, 2.0f, 1.0f, 1.0f, 0.0f, 0.0f, 3.0f, 3.0f, 0.15f, 0.85f, 0.0f, 350);
        check("bornes confondues : bascule nette à la distance donnée", net.recul(2.9) == 1.0 && net.recul(3.0) == 2.0);
    }

    // MARK: drapeaux

    private static void drapeaux() {
        section("Drapeaux de session");
        int f = SessionFlags.AUTORISER_ESC | SessionFlags.MASQUER_HUD;
        check("lecture d'un drapeau présent", SessionFlags.a(f, SessionFlags.MASQUER_HUD));
        check("lecture d'un drapeau absent", !SessionFlags.a(f, SessionFlags.VERROU_JOUEUR));
        check("description lisible", SessionFlags.decrire(f).equals("esc,hud"));
        check("aucun drapeau", SessionFlags.decrire(0).equals("-"));
        check("les huit drapeaux sont distincts",
                Integer.bitCount(SessionFlags.VERROU_JOUEUR | SessionFlags.AUTORISER_ESC | SessionFlags.MASQUER_HUD
                        | SessionFlags.FORCER_TROISIEME_PERSONNE | SessionFlags.REPRISE_APRES_CINEMATIQUE
                        | SessionFlags.EXCLUSIF | SessionFlags.SIMPLIFIE | SessionFlags.RETOUR_APRES_COMMERCE) == 8);
        check("les drapeaux simplifié et retour se décrivent",
                SessionFlags.decrire(SessionFlags.SIMPLIFIE | SessionFlags.RETOUR_APRES_COMMERCE).equals("simplifie,retour"));
        DialogueConfig defaut = new DialogueConfig();
        check("retour après commerce activé par défaut, reprise après cinématique non",
                defaut.retour_apres_commerce && !defaut.reprise_apres_cinematique);
    }

    // MARK: véhicules

    private static void vehicules() {
        section("Véhicule en mouvement (hystérésis)");
        MouvementDetecteur.Etat arret = MouvementDetecteur.Etat.ARRET;
        check("à l'arrêt, vitesse nulle → à l'arrêt",
                !MouvementDetecteur.suivant(arret, 0.0, 0.03, 40, 5).enMouvement());
        check("à l'arrêt, seuil exactement atteint → toujours à l'arrêt",
                !MouvementDetecteur.suivant(arret, 0.03, 0.03, 40, 5).enMouvement());
        check("à l'arrêt, vitesse au-dessus du seuil → en route tout de suite",
                MouvementDetecteur.suivant(arret, 0.1, 0.03, 40, 5).enMouvement());
        MouvementDetecteur.Etat e = MouvementDetecteur.suivant(MouvementDetecteur.Etat.EN_ROUTE, 0.0, 0.03, 40, 5);
        check("en route, un contrôle immobile → toujours en route, compteur 5",
                e.enMouvement() && e.ticksImmobile() == 5);
        for (int i = 0; i < 6; i++) {
            e = MouvementDetecteur.suivant(e, 0.0, 0.03, 40, 5);
        }
        check("après 35 ticks immobile → toujours en route", e.enMouvement() && e.ticksImmobile() == 35);
        e = MouvementDetecteur.suivant(e, 0.0, 0.03, 40, 5);
        check("après 40 ticks immobile → à l'arrêt", !e.enMouvement() && e.ticksImmobile() == 0);
        MouvementDetecteur.Etat sursaut = MouvementDetecteur.suivant(new MouvementDetecteur.Etat(true, 35), 0.1, 0.03, 40, 5);
        check("un sursaut remet le compteur à zéro", sursaut.enMouvement() && sursaut.ticksImmobile() == 0);
        check("immobilité requise nulle → à l'arrêt au premier contrôle calme",
                !MouvementDetecteur.suivant(MouvementDetecteur.Etat.EN_ROUTE, 0.0, 0.03, 0, 5).enMouvement());

        check("politique : textes reconnus, accent toléré",
                DialogueConfig.politiqueVehicule("Refuser") == DialogueConfig.PolitiqueVehicule.REFUSER
                        && DialogueConfig.politiqueVehicule("complet") == DialogueConfig.PolitiqueVehicule.COMPLET
                        && DialogueConfig.politiqueVehicule("simplifié") == DialogueConfig.PolitiqueVehicule.SIMPLIFIE
                        && DialogueConfig.politiqueVehicule("autre") == null
                        && DialogueConfig.politiqueVehicule(null) == null);
        DialogueConfig c = new DialogueConfig();
        c.vehicule_en_mouvement = "n'importe quoi";
        c.vehicule_vitesse_mouvement = -1.0;
        c.vehicule_ticks_arret = 99999;
        c.borner();
        check("bornage : politique inconnue → simplifié, vitesse et ticks bornés",
                c.politiqueVehicule() == DialogueConfig.PolitiqueVehicule.SIMPLIFIE
                        && c.vehicule_vitesse_mouvement > 0.0 && c.vehicule_ticks_arret == 1200);
    }

    // MARK: raisons et modes

    private static void raisons() {
        section("Énumérations réseau");
        check("les raisons de fermeture ont un ordinal stable (16)", CloseReason.values().length == 16);
        check("trois modes de dialogue", DialogueMode.values().length == 3);
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeEnumConstant(CloseReason.CINEMATIQUE);
        check("une raison survit à l'aller-retour", buf.readEnumConstant(CloseReason.class) == CloseReason.CINEMATIQUE);
    }

    // MARK: bornage

    private static void bornage() {
        section("Bornage de la configuration");
        DialogueConfig c = new DialogueConfig();
        c.distance_ouverture = 400.0;
        c.distance_maintien = 1.0;
        c.camera.distance_loin = 0.1;
        c.camera.distance_proche = 3.0;
        c.verification_ticks = 0;
        c.tag_actif = " ";
        c.borner();
        check("distance d'ouverture plafonnée à 32", c.distance_ouverture == 32.0);
        check("distance de maintien ≥ distance d'ouverture", c.distance_maintien >= c.distance_ouverture);
        check("distance loin ≥ distance proche", c.camera.distance_loin >= c.camera.distance_proche);
        check("vérification au moins chaque tick", c.verification_ticks >= 1);
        check("tag vide remplacé", c.tag_actif.equals("hc_dialogue"));
    }

    // MARK: outils

    private static void section(String titre) {
        System.out.println();
        System.out.println("  " + titre);
    }

    private static void check(String libelle, boolean ok) {
        System.out.println("    " + (ok ? "ok   " : "ECHEC") + " " + libelle);
        if (ok) {
            reussis++;
        } else {
            echecs++;
        }
    }

    private static boolean proche(double a, double b) {
        return Math.abs(a - b) < 0.02;
    }
}
