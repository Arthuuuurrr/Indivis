package net.hautecapitale.metiers.craft;

import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.hautecapitale.metiers.HauteCapitaleMetiers;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

/**
 * Le compteur de fabrications par recette, attaché au joueur.
 *
 * <p>Un entier par recette jamais fabriquée — quelques dizaines d'octets même
 * pour un artisan de haut niveau. Persistant, conservé à la mort, jamais envoyé
 * au client : l'écran reçoit déjà l'état de maîtrise calculé par le serveur.
 *
 * <p>La carte est immuable ; chaque fabrication en produit une nouvelle. C'est
 * ce qui rend un « ajout » sûr sans verrou : il n'y a jamais de carte à moitié
 * modifiée sous les yeux de quelqu'un.
 */
public final class MasteryAttachment {

    private static final Codec<Map<Identifier, Integer>> CODEC =
            Codec.unboundedMap(Identifier.CODEC, Codec.INT);

    public static final AttachmentType<Map<Identifier, Integer>> CRAFTS =
            AttachmentRegistry.create(HauteCapitaleMetiers.id("fabrications"), builder -> builder
                    .initializer(Map::of)
                    .persistent(CODEC)
                    .copyOnDeath());

    private MasteryAttachment() {
    }

    public static void init() {
        // La constante ci-dessus s'enregistre en se chargeant.
    }

    public static int crafts(ServerPlayerEntity player, Identifier recipe) {
        return player.getAttachedOrCreate(CRAFTS).getOrDefault(recipe, 0);
    }

    /** Compte une fabrication de plus et renvoie le nouveau total. */
    public static int record(ServerPlayerEntity player, Identifier recipe, int times) {
        Map<Identifier, Integer> updated = new HashMap<>(player.getAttachedOrCreate(CRAFTS));
        int total = updated.getOrDefault(recipe, 0) + Math.max(1, times);
        updated.put(recipe, total);
        player.setAttached(CRAFTS, Map.copyOf(updated));
        return total;
    }

    public static void reset(ServerPlayerEntity player) {
        player.setAttached(CRAFTS, Map.of());
    }
}
