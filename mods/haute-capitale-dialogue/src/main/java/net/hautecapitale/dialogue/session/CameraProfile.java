package net.hautecapitale.dialogue.session;

import net.minecraft.network.PacketByteBuf;

/**
 * Comment cadrer une conversation. Décidé par le serveur, appliqué par le client.
 *
 * <p>Le plan est un <b>plan d'épaule ancré sur le joueur</b> : la caméra se place
 * derrière lui et sur le côté, puis regarde le personnage. Recul, décalage et
 * angle de visée glissent entre une valeur « proche » et une valeur « loin »
 * selon la distance joueur–personnage, entre {@code distanceProche} et
 * {@code distanceLoin} ; au-delà, ils ne bougent plus. Le joueur ne sort donc
 * jamais du cadre, quelle que soit la distance.
 *
 * @param reculProche    recul derrière les yeux du joueur, en blocs, à distance proche
 * @param reculLoin      le même, à distance lointaine
 * @param decalageProche décalage vers l'épaule, en blocs, à distance proche
 * @param decalageLoin   le même, à distance lointaine
 * @param viseeProche    angle, en degrés, dont le personnage est écarté du centre de l'image,
 *                       vers le côté opposé à l'épaule, à distance proche
 * @param viseeLoin      le même, à distance lointaine
 * @param distanceProche distance joueur–personnage en deçà de laquelle les valeurs « proche » s'appliquent
 * @param distanceLoin   distance au-delà de laquelle les valeurs « loin » s'appliquent
 * @param elevation      hauteur de la caméra au-dessus des yeux du joueur, en blocs
 * @param hauteurCible   point visé sur le personnage, en fraction de sa hauteur
 * @param decalageCibleY décalage vertical du point visé, en blocs
 * @param transitionMs   durée des fondus d'entrée et de sortie
 */
public record CameraProfile(
        float reculProche,
        float reculLoin,
        float decalageProche,
        float decalageLoin,
        float viseeProche,
        float viseeLoin,
        float distanceProche,
        float distanceLoin,
        float elevation,
        float hauteurCible,
        float decalageCibleY,
        int transitionMs) {

    public static final CameraProfile DEFAUT = new CameraProfile(
            1.5f, 1.75f, 1.35f, 0.8f, 10.0f, 6.0f, 1.5f, 5.0f, 0.15f, 0.85f, 0.0f, 350);

    public void write(PacketByteBuf buf) {
        buf.writeFloat(this.reculProche);
        buf.writeFloat(this.reculLoin);
        buf.writeFloat(this.decalageProche);
        buf.writeFloat(this.decalageLoin);
        buf.writeFloat(this.viseeProche);
        buf.writeFloat(this.viseeLoin);
        buf.writeFloat(this.distanceProche);
        buf.writeFloat(this.distanceLoin);
        buf.writeFloat(this.elevation);
        buf.writeFloat(this.hauteurCible);
        buf.writeFloat(this.decalageCibleY);
        buf.writeVarInt(this.transitionMs);
    }

    public static CameraProfile read(PacketByteBuf buf) {
        return new CameraProfile(
                buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat(),
                buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat(),
                buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readVarInt());
    }

    /** Où l'on est entre « proche » (0) et « loin » (1) pour cette distance joueur–personnage. */
    public double fraction(double distance) {
        double plage = this.distanceLoin - this.distanceProche;
        if (plage <= 1.0e-6) {
            return distance >= this.distanceLoin ? 1.0 : 0.0;
        }
        double t = (distance - this.distanceProche) / plage;
        return t < 0.0 ? 0.0 : Math.min(1.0, t);
    }

    public double recul(double distance) {
        return lerp(this.reculProche, this.reculLoin, fraction(distance));
    }

    public double decalage(double distance) {
        return lerp(this.decalageProche, this.decalageLoin, fraction(distance));
    }

    public double visee(double distance) {
        return lerp(this.viseeProche, this.viseeLoin, fraction(distance));
    }

    private static double lerp(double a, double b, double t) {
        return a + (b - a) * t;
    }
}
