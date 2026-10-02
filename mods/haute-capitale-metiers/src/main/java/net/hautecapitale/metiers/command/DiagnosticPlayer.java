package net.hautecapitale.metiers.command;

import com.mojang.authlib.GameProfile;
import net.fabricmc.fabric.api.entity.FakePlayer;
import net.minecraft.server.world.ServerWorld;

import java.util.UUID;

/**
 * Le joueur en mémoire des diagnostics : un {@link FakePlayer} de Fabric —
 * il a un gestionnaire réseau qui avale les paquets, donc les effets de
 * statut, les téléportations et les écouteurs d'autres mods (attributs,
 * accessoires) ne lèvent rien — mais construit à neuf à chaque appel, sans
 * passer par le cache de {@code FakePlayer.get} : un diagnostic doit toujours
 * partir d'un joueur vierge.
 */
final class DiagnosticPlayer extends FakePlayer {

    private DiagnosticPlayer(ServerWorld world, GameProfile profile) {
        super(world, profile);
    }

    static DiagnosticPlayer create(ServerWorld world, String name) {
        return new DiagnosticPlayer(world, new GameProfile(UUID.nameUUIDFromBytes(name.getBytes()), name));
    }
}
