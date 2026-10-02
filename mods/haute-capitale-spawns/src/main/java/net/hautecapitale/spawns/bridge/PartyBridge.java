package net.hautecapitale.spawns.bridge;

import net.fabricmc.loader.api.FabricLoader;
import net.hautecapitale.spawns.HauteCapitaleSpawns;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.List;

/**
 * Pont doux vers le mod de groupe {@code haute_capitale_party}.
 *
 * <p>Le gestionnaire ne recree aucun systeme de groupe : il demande seulement
 * « quels sont les membres en ligne du groupe de ce joueur ? » pour le credit
 * {@code party_nearby}. Sans le mod, la reponse est toujours vide et le credit
 * retombe sur les participants. La classe imbriquee {@code Impl} n'est liee
 * qu'au premier appel ; une erreur de liaison coupe le pont definitivement.
 */
public final class PartyBridge {

    private static final boolean PRESENT = FabricLoader.getInstance().isModLoaded("haute_capitale_party");
    private static volatile boolean broken;

    private PartyBridge() {
    }

    public static boolean available() {
        return PRESENT && !broken;
    }

    /** Les membres en ligne du groupe du joueur (lui compris) ; vide sans groupe ou sans mod. */
    public static List<ServerPlayerEntity> onlineMembers(MinecraftServer server, ServerPlayerEntity player) {
        if (!available()) {
            return List.of();
        }
        try {
            return Impl.members(server, player);
        } catch (Throwable t) {
            broken = true;
            HauteCapitaleSpawns.LOGGER.warn("Pont vers le mod de groupe coupe (signature changee ?) : {}", t.toString());
            return List.of();
        }
    }

    private static final class Impl {
        static List<ServerPlayerEntity> members(MinecraftServer server, ServerPlayerEntity player) {
            return net.hautecapitale.party.api.Parties.of(player)
                    .map(party -> net.hautecapitale.party.api.Parties.onlineMembers(server, party))
                    .orElse(List.of());
        }
    }
}
