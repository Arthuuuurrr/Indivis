package net.hautecapitale.metiers.creature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Identifier;

import java.util.Locale;

/**
 * Un objet qu'une créature peut donner, avec sa quantité et sa probabilité.
 *
 * <p>Sert aussi bien à une matière de dépeçage qu'à un composant secondaire, à
 * une viande ou à un butin de combat — c'est la même forme partout, ce qui
 * évite trois schémas presque identiques.
 */
public record DropEntry(Identifier item, int min, int max, double chance) {

    public static final Codec<DropEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Identifier.CODEC.fieldOf("id").forGetter(DropEntry::item),
            Codec.INT.optionalFieldOf("min", 1).forGetter(DropEntry::min),
            Codec.INT.optionalFieldOf("max", 1).forGetter(DropEntry::max),
            Codec.DOUBLE.optionalFieldOf("chance", 1.0D).forGetter(DropEntry::chance)
    ).apply(instance, DropEntry::new));

    public DropEntry {
        if (min < 0) {
            min = 0;
        }
        if (max < min) {
            max = min;
        }
        if (chance < 0.0D || !Double.isFinite(chance)) {
            chance = 0.0D;
        } else if (chance > 1.0D) {
            chance = 1.0D;
        }
    }

    public boolean isAlwaysDropped() {
        return chance >= 1.0D;
    }

    /** Description lisible pour les commandes d'inspection. */
    public String describe() {
        StringBuilder text = new StringBuilder(item.toString());
        if (min == max) {
            if (min != 1) {
                text.append(" ×").append(min);
            }
        } else {
            text.append(" ×").append(min).append('-').append(max);
        }
        if (!isAlwaysDropped()) {
            text.append(String.format(Locale.ROOT, " (%.0f %%)", chance * 100.0D));
        }
        return text.toString();
    }
}
