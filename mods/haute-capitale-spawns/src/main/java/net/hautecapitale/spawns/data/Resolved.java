package net.hautecapitale.spawns.data;

import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Les reglages effectifs d'un point, une fois tous les niveaux empiles.
 *
 * <p>Calcule une fois par point et mis en cache par le registre ; recalcule a
 * chaque modification. Le moteur ne lit jamais les {@link SpawnSettings} bruts.
 */
public record Resolved(
        Identifier entity,
        Respawn respawn,
        Leash leash,
        int activationRadius,
        int wanderRadius,
        String rank,
        Map<String, String> tags,
        List<QuestDrop> drops,
        List<String> counters,
        RespawnConditions conditions,
        Optional<String> nbt,
        Optional<String> customName,
        boolean initialize,
        CreditMode creditMode,
        int level,
        Map<String, Double> attributes) {

    /**
     * Empile {@code layers} (du plus general au plus precis) sur {@code base}, qui
     * doit etre complet (c'est la configuration globale). Echoue seulement si
     * aucune couche ne donne d'entite.
     */
    public static Result of(SpawnSettings base, SpawnSettings... layers) {
        SpawnSettings merged = base;
        for (SpawnSettings layer : layers) {
            if (layer != null) {
                merged = merged.overlay(layer);
            }
        }
        List<String> problems = new ArrayList<>();
        if (merged.entity().isEmpty()) {
            problems.add("aucune entite (ni point, ni profil, ni zone)");
        }
        if (merged.respawn().isEmpty()) {
            problems.add("aucun delai de reapparition");
        }
        if (merged.leash().isEmpty()) {
            problems.add("aucune laisse");
        }
        if (!problems.isEmpty()) {
            return new Result(Optional.empty(), problems);
        }
        Resolved resolved = new Resolved(
                merged.entity().get(),
                merged.respawn().get(),
                merged.leash().get(),
                Math.max(16, merged.activationRadius().orElse(96)),
                Math.max(0, merged.wanderRadius().orElse(10)),
                merged.rank().orElse("normal"),
                merged.tags(),
                merged.drops().orElse(List.of()),
                merged.counters().orElse(List.of()),
                merged.conditions().orElse(RespawnConditions.DEFAULT),
                merged.nbt().filter(s -> !s.isBlank()),
                merged.customName().filter(s -> !s.isBlank()),
                merged.initialize().orElse(true),
                merged.creditMode().orElse(CreditMode.PARTY_NEARBY),
                Math.max(1, merged.level().orElse(1)),
                merged.attributes());
        return new Result(Optional.of(resolved), problems);
    }

    public record Result(Optional<Resolved> resolved, List<String> problems) {
        public boolean ok() {
            return this.resolved.isPresent();
        }
    }
}
