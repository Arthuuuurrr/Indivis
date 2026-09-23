package net.hautecapitale.rpg.ability;

import java.util.List;
import java.util.Optional;
import net.fabricmc.api.ModInitializer;
import net.hautecapitale.fusils.api.FusilAPI;
import net.minecraft.class_1309;
import net.minecraft.class_1937;
import net.minecraft.class_243;
import net.minecraft.class_6880;
import net.spell_engine.api.spell.Spell;
import net.spell_engine.api.spell.event.SpellHandlers;
import net.spell_engine.internals.SpellExecution;

/**
 * Spell Engine hotbar adapter for Haute Capitale firearm skills.
 *
 * Spell Engine owns the visible slot/input path. The firearm mod remains
 * authoritative for actual gunplay through FusilAPI.useSkill(...).
 */
public final class FirearmSpellBridge implements ModInitializer {
    public static final String PREFIX = "haute_capitale_rpg:firearm_";

    @Override
    public void onInitialize() {
        register("rafale");
        register("tir_incapacitant");
        register("tir_perforant");
        register("tir_explosif");
        register("repli");
        register("marque");
    }

    private static void register(String skillId) {
        SpellHandlers.customDelivery.put(PREFIX + skillId, new Handler(skillId));
    }

    private static final class Handler implements SpellHandlers.CustomDelivery {
        private final String skillId;

        private Handler(String skillId) {
            this.skillId = skillId;
        }

        @Override
        public boolean onSpellDelivery(class_1937 world, class_6880<Spell> spellEntry, class_1309 caster,
                                       List<SpellExecution.DeliveryTarget> targets,
                                       SpellExecution.ImpactContext context, class_243 targetLocation) {
            try {
                Optional<String> error = FusilAPI.useSkill(caster, skillId, 0);
                return error.isEmpty();
            } catch (Throwable ignored) {
                return false;
            }
        }
    }
}
