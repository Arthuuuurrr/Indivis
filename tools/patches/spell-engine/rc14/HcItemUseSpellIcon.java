package net.spell_engine.client.util;

import java.util.List;
import net.minecraft.class_1799;
import net.minecraft.class_2960;
import net.spell_engine.api.spell.container.SpellContainer;
import net.spell_engine.api.spell.container.SpellContainerHelper;

/**
 * Resolves the visible icon for SpellHotbar's vanilla-use-key slot.
 * Falls back to the original ItemStack rendering when no usable spell exists.
 */
public final class HcItemUseSpellIcon {
    private HcItemUseSpellIcon() {}

    public static class_2960 resolve(class_1799 stack) {
        if (stack == null) return null;
        try {
            SpellContainer container = SpellContainerHelper.containerFromItemStack(stack);
            if (container == null || !container.isValid() || !container.isUsable()) return null;
            List<String> spellIds = container.spell_ids();
            if (spellIds == null || spellIds.isEmpty()) return null;
            String first = spellIds.get(0);
            if (first == null || first.isBlank()) return null;
            class_2960 spellId = class_2960.method_60654(first);
            return spellId == null ? null : SpellRender.iconTexture(spellId);
        } catch (Throwable ignored) {
            return null;
        }
    }
}
