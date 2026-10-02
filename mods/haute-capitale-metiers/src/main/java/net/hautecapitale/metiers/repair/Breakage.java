package net.hautecapitale.metiers.repair;

import net.hautecapitale.metiers.HauteCapitaleMetiers;
import net.hautecapitale.metiers.config.MetiersConfig;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.Optional;

/**
 * Brisé plutôt que détruit.
 *
 * <p>Quand un objet à durabilité arrive à zéro, Minecraft le détruit. Pour les
 * objets de la famille {@code #haute_capitale_metiers:reparable_si_brise}, on
 * l'arrête au dernier point, on lui retire ce qui le rendait utile, et on le
 * marque : il attend un forgeron. C'est le seul point d'interception, commun à
 * tous les mods — le mixin sur {@code ItemStack} appelle {@link #intercept}.
 * Les mods qui gèrent eux-mêmes la destruction de leurs objets se listent dans
 * {@code reparation.exceptions} de la configuration.
 */
public final class Breakage {

    /** Ce qui se brise au lieu de disparaître. */
    public static final TagKey<Item> REPAIRABLE_WHEN_BROKEN =
            TagKey.of(RegistryKeys.ITEM, HauteCapitaleMetiers.id("reparable_si_brise"));

    private Breakage() {
    }

    public static boolean isBroken(ItemStack stack) {
        return stack.contains(BrokenComponent.TYPE);
    }

    /** Cet objet se brise-t-il au lieu de disparaître ? Famille, moins les exceptions de la configuration. */
    public static boolean protects(ItemStack stack) {
        if (stack.isEmpty() || !stack.isDamageable() || !stack.isIn(REPAIRABLE_WHEN_BROKEN)) {
            return false;
        }
        Identifier id = Registries.ITEM.getId(stack.getItem());
        return !MetiersConfig.get().reparation.exceptions.contains(id.toString());
    }

    /**
     * Appelé juste avant que Minecraft n'écrive la nouvelle durabilité. Rend
     * {@code true} s'il a pris la main : l'objet est alors brisé, pas détruit,
     * et Minecraft ne doit rien faire de plus.
     */
    public static boolean intercept(ItemStack stack, int newDamage, ServerPlayerEntity player) {
        if (newDamage < stack.getMaxDamage() || !protects(stack)) {
            return false;
        }
        if (!isBroken(stack)) {
            markBroken(stack);
            if (player != null && player.networkHandler != null) {
                player.sendMessage(Text.translatableWithFallback("hcm.reparation.brise",
                        "%s est brisé — un forgeron peut le réparer.", stack.getName()).formatted(Formatting.RED), false);
            }
        }
        // Déjà brisé : l'objet reste à son dernier point, et ne s'use plus.
        stack.setDamage(Math.max(0, stack.getMaxDamage() - 1));
        return true;
    }

    /** Retire ce qui rend l'objet utile, en le gardant pour la réparation. */
    public static void markBroken(ItemStack stack) {
        BrokenComponent saved = new BrokenComponent(
                Optional.ofNullable(stack.get(DataComponentTypes.ATTRIBUTE_MODIFIERS)),
                Optional.ofNullable(stack.get(DataComponentTypes.TOOL)),
                Optional.ofNullable(stack.get(DataComponentTypes.WEAPON)),
                Optional.ofNullable(stack.get(DataComponentTypes.BLOCKS_ATTACKS)));
        stack.set(BrokenComponent.TYPE, saved);
        stack.set(DataComponentTypes.ATTRIBUTE_MODIFIERS, AttributeModifiersComponent.DEFAULT);
        stack.remove(DataComponentTypes.TOOL);
        stack.remove(DataComponentTypes.WEAPON);
        stack.remove(DataComponentTypes.BLOCKS_ATTACKS);
    }

    /** Rend à l'objet ce qu'il faisait, et retire la marque. */
    public static void mend(ItemStack stack) {
        BrokenComponent saved = stack.get(BrokenComponent.TYPE);
        if (saved == null) {
            return;
        }
        if (saved.attributes().isPresent()) {
            stack.set(DataComponentTypes.ATTRIBUTE_MODIFIERS, saved.attributes().get());
        } else {
            stack.remove(DataComponentTypes.ATTRIBUTE_MODIFIERS);
        }
        saved.tool().ifPresent(tool -> stack.set(DataComponentTypes.TOOL, tool));
        saved.weapon().ifPresent(weapon -> stack.set(DataComponentTypes.WEAPON, weapon));
        saved.blocks().ifPresent(blocks -> stack.set(DataComponentTypes.BLOCKS_ATTACKS, blocks));
        stack.remove(BrokenComponent.TYPE);
    }
}
