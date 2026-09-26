package net.hautecapitale.spawns.engine;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Ownable;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Qui a frappe quel mob controle, et quand.
 *
 * <p>Alimente par l'evenement de degats, seulement pour les mobs marques : le
 * cout est proportionnel aux combats en cours, jamais au nombre de points. Sert
 * a etablir les participants d'un kill ; le tueur vient de la source du coup fatal.
 */
public final class KillTracker {

    private final Map<UUID, Map<UUID, Long>> hits = new HashMap<>();

    public void onDamage(LivingEntity victim, DamageSource source, long tick) {
        Optional<ServerPlayerEntity> player = playerFrom(source);
        if (player.isEmpty()) {
            return;
        }
        this.hits.computeIfAbsent(victim.getUuid(), k -> new LinkedHashMap<>()).put(player.get().getUuid(), tick);
    }

    /** Les joueurs ayant frappe dans la fenetre, du premier au dernier. */
    public List<UUID> participants(UUID victim, long tick, int windowTicks) {
        Map<UUID, Long> map = this.hits.get(victim);
        if (map == null) {
            return List.of();
        }
        List<UUID> out = new ArrayList<>();
        for (Map.Entry<UUID, Long> entry : map.entrySet()) {
            if (tick - entry.getValue() <= windowTicks) {
                out.add(entry.getKey());
            }
        }
        return out;
    }

    public void forget(UUID victim) {
        this.hits.remove(victim);
    }

    public int trackedCount() {
        return this.hits.size();
    }

    /** Le joueur derriere une source de degats : direct, projectile, invocation, familier. */
    public static Optional<ServerPlayerEntity> playerFrom(DamageSource source) {
        if (source == null) {
            return Optional.empty();
        }
        Optional<ServerPlayerEntity> attacker = playerFrom(source.getAttacker());
        if (attacker.isPresent()) {
            return attacker;
        }
        return playerFrom(source.getSource());
    }

    /** Remonte la chaine de propriete (profondeur 8) jusqu'a un joueur. */
    public static Optional<ServerPlayerEntity> playerFrom(Entity entity) {
        Entity current = entity;
        for (int depth = 0; depth < 8 && current != null; depth++) {
            if (current instanceof ServerPlayerEntity player) {
                return Optional.of(player);
            }
            if (current instanceof ProjectileEntity projectile) {
                current = projectile.getOwner();
            } else if (current instanceof Ownable ownable) {
                current = ownable.getOwner();
            } else {
                return Optional.empty();
            }
        }
        return Optional.empty();
    }
}
