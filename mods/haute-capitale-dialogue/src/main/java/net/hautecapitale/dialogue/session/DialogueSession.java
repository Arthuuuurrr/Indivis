package net.hautecapitale.dialogue.session;

import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.UUID;

/**
 * « Ce joueur parle à ce personnage. » L'état logique, tenu par le serveur.
 *
 * <p>Il n'y a rien ici sur le contenu de la conversation : Easy NPC ou le
 * datapack le possèdent. La session sait seulement qui, avec qui, depuis quand,
 * dans quel mode, et ce qu'il faudra défaire à la fermeture.
 */
public final class DialogueSession {

    public final int id;
    public final UUID joueur;
    public final UUID pnj;
    public final int pnjEntityId;
    public final String pnjNom;
    public final RegistryKey<World> monde;
    public final DialogueMode mode;
    public final CameraProfile profil;
    public final long ouvertureTick;

    /** Les drapeaux envoyés au client ; {@code SIMPLIFIE} peut changer en cours de route. */
    public int flags;

    /** Dernière activité connue (ouverture, changement de nœud). */
    public long derniereActiviteTick;

    /** Le joueur a-t-il eu, à un moment, un écran autre que son inventaire ? */
    public boolean ecranVu;

    /** Position du joueur au dernier contrôle. */
    public Vec3d dernierePosition;

    /** Position du personnage au dernier contrôle, pour mesurer sa vitesse. */
    public Vec3d dernierePosPnj;

    /**
     * Position du joueur relative au personnage au dernier contrôle : c'est
     * elle qui saute en cas de téléportation, et elle ne bouge pas quand les
     * deux voyagent sur le même pont.
     */
    public Vec3d derniereRelative;

    /** Le véhicule du personnage bouge-t-il ? */
    public MouvementDetecteur.Etat mouvement = MouvementDetecteur.Etat.ARRET;

    /** Dernier nœud de dialogue Easy NPC ouvert : là où l'on revient après un commerce, ou une cinématique. */
    public UUID dernierDialogue;

    /** Un écran qui n'est pas le dialogue (commerce, atelier) a pris la place du nôtre. */
    public boolean ecranEtrangerVu;

    /** Ticks consécutifs sans aucun écran, depuis le dernier vu. */
    public int ticksSansEcran;

    /** Nous avons posé l'objectif de regard sur le personnage : à retirer. */
    public boolean regardPose;

    /** Nous avons posé le verrou de déplacement : à retirer. */
    public boolean verrouPose;

    public DialogueSession(int id, UUID joueur, UUID pnj, int pnjEntityId, String pnjNom,
                           RegistryKey<World> monde, DialogueMode mode, CameraProfile profil,
                           int flags, long tick, Vec3d position) {
        this.id = id;
        this.joueur = joueur;
        this.pnj = pnj;
        this.pnjEntityId = pnjEntityId;
        this.pnjNom = pnjNom;
        this.monde = monde;
        this.mode = mode;
        this.profil = profil;
        this.flags = flags;
        this.ouvertureTick = tick;
        this.derniereActiviteTick = tick;
        this.dernierePosition = position;
    }

    public void toucher(long tick) {
        this.derniereActiviteTick = tick;
    }

    public long age(long tick) {
        return tick - this.ouvertureTick;
    }

    public boolean a(int flag) {
        return SessionFlags.a(this.flags, flag);
    }

    public boolean simplifie() {
        return a(SessionFlags.SIMPLIFIE);
    }

    @Override
    public String toString() {
        return "session #" + this.id + " " + this.mode + " pnj=" + this.pnjNom + " (" + this.pnj + ")"
                + (simplifie() ? " [simplifié]" : "");
    }
}
