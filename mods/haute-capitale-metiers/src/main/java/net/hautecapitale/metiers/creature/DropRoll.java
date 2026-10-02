package net.hautecapitale.metiers.creature;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.random.Random;

import java.util.ArrayList;
import java.util.List;

/**
 * Le tirage d'une liste d'objets donnés — le même pour un node, une proie, et
 * demain une carcasse : chance, puis quantité entre {@code min} et {@code max},
 * en ignorant les objets absents de cette installation.
 */
public final class DropRoll {

    private DropRoll() {
    }

    public static List<ItemStack> roll(List<DropEntry> entries, Random random) {
        List<ItemStack> result = new ArrayList<>();
        for (DropEntry entry : entries) {
            if (!Registries.ITEM.containsId(entry.item())) {
                continue;
            }
            if (entry.chance() < 1.0D && random.nextDouble() >= entry.chance()) {
                continue;
            }
            int count = entry.min() == entry.max() ? entry.min()
                    : entry.min() + random.nextInt(entry.max() - entry.min() + 1);
            if (count > 0) {
                Item item = Registries.ITEM.get(entry.item());
                result.add(new ItemStack(item, count));
            }
        }
        return result;
    }
}
