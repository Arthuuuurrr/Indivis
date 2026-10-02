package net.hautecapitale.metiers.gadget;

import net.hautecapitale.metiers.config.MetiersConfig;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LightType;

import java.util.Locale;

/** Ce que les gadgets partagent : messages, lumière, effets courts, directions. */
public final class Gadgets {

    /** Un effet « tant qu'on porte » : court, renouvelé sans arrêt, sans particules. */
    static final int WORN_EFFECT_TICKS = 15 * 20;

    private Gadgets() {
    }

    public static MetiersConfig.Gadgets config() {
        return MetiersConfig.get().gadgets;
    }

    /** Un message discret, au-dessus de la barre d'inventaire. */
    public static void overlay(PlayerEntity player, Text text) {
        if (player instanceof ServerPlayerEntity server && server.networkHandler != null) {
            server.sendMessage(text, true);
        }
    }

    /** Un message dans le chat. */
    public static void say(PlayerEntity player, Text text) {
        if (player instanceof ServerPlayerEntity server && server.networkHandler != null) {
            server.sendMessage(text, false);
        }
    }

    public static MutableText text(String key, String fallback, Object... args) {
        return Text.translatableWithFallback(key, fallback, args);
    }

    /** Il fait sombre là où le joueur est. */
    public static boolean isDark(ServerWorld world, PlayerEntity player) {
        BlockPos pos = player.getBlockPos();
        int light = Math.max(world.getLightLevel(LightType.BLOCK, pos), world.getLightLevel(LightType.SKY, pos)
                - world.getAmbientDarkness());
        return light <= config().obscurite;
    }

    /** Un effet renouvelé tant qu'on porte le gadget — ambiant, sans particules ni icône criarde. */
    public static void keepEffect(PlayerEntity player, RegistryEntry<StatusEffect> effect, int amplifier) {
        StatusEffectInstance current = player.getStatusEffect(effect);
        if (current == null || current.getDuration() < WORN_EFFECT_TICKS / 2) {
            player.addStatusEffect(new StatusEffectInstance(effect, WORN_EFFECT_TICKS, amplifier, true, false, true));
        }
    }

    public static boolean isOn(ItemStack stack) {
        return Boolean.TRUE.equals(stack.get(GadgetComponents.ACTIF));
    }

    /** « nord-est, 23 blocs » — pour les détecteurs. */
    public static Text direction(Vec3d from, Vec3d to) {
        double dx = to.x - from.x;
        double dz = to.z - from.z;
        double distance = Math.sqrt(dx * dx + dz * dz);
        String[] names = {"sud", "sud-ouest", "ouest", "nord-ouest", "nord", "nord-est", "est", "sud-est"};
        String[] keys = {"sud", "sud_ouest", "ouest", "nord_ouest", "nord", "nord_est", "est", "sud_est"};
        // Minecraft : +z = sud, +x = est ; l'angle tourne dans le sens horaire vu du dessus.
        double angle = Math.toDegrees(Math.atan2(-dx, dz));
        int index = (int) Math.floor(((angle + 360.0D + 22.5D) % 360.0D) / 45.0D) % 8;
        MutableText dir = Text.translatableWithFallback("hcm.gadget.direction." + keys[index], names[index]);
        double dy = to.y - from.y;
        String vertical = dy > 3.0D ? " ↑" : dy < -3.0D ? " ↓" : "";
        return dir.append(Text.literal(String.format(Locale.ROOT, ", %.0f blocs%s", distance, vertical)));
    }

    /** Les effets « tant qu'on porte » ont tous la même forme ; ceux-ci servent à plusieurs gadgets. */
    public static RegistryEntry<StatusEffect> nightVision() {
        return StatusEffects.NIGHT_VISION;
    }
}
