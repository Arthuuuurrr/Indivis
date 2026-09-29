package fr.hautecapitale.creatures.spawn;

import java.util.Map;

public final class CapitaleCreaturesWeightScale142 {
    private CapitaleCreaturesWeightScale142() {}

    public static int scale(int weight, Map<?, ?> rule) {
        if (rule == null) return Math.max(1, weight);
        Object raw = rule.get("frequency_percent");
        if (!(raw instanceof Number n)) return Math.max(1, weight);
        int percent = Math.max(1, Math.min(100, n.intValue()));
        return Math.max(1, (weight * percent + 50) / 100);
    }
}
