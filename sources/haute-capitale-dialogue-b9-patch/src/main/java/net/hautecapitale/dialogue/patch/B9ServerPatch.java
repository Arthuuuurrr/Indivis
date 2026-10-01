package net.hautecapitale.dialogue.patch;

import com.mojang.brigadier.arguments.StringArgumentType;
import java.text.Normalizer;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.hautecapitale.dialogue.session.DialogueManager;
import net.hautecapitale.dialogue.session.DialogueMode;
import net.minecraft.entity.LivingEntity;
import net.minecraft.registry.Registries;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public final class B9ServerPatch implements ModInitializer {
    private static final double MAX_RANGE = 14.0D;

    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
                dispatcher.register(
                        CommandManager.literal("hcd_interpellation")
                                .requires(source -> source.getEntity() instanceof ServerPlayerEntity)
                                .then(
                                        CommandManager.literal("auto_guard")
                                                .executes(context ->
                                                        open(
                                                                context.getSource().getPlayerOrThrow(),
                                                                null,
                                                                true)))
                                .then(
                                        CommandManager.literal("nearest_named")
                                                .then(
                                                        CommandManager.argument(
                                                                        "name",
                                                                        StringArgumentType.greedyString())
                                                                .executes(context ->
                                                                        open(
                                                                                context.getSource().getPlayerOrThrow(),
                                                                                StringArgumentType.getString(
                                                                                        context,
                                                                                        "name"),
                                                                                false))))));
    }

    private static int open(
            ServerPlayerEntity player,
            String wantedName,
            boolean guardOnly) {
        if (player == null || DialogueManager.enSession(player)) {
            return 0;
        }

        Box area = player.getBoundingBox().expand(MAX_RANGE);
        List<LivingEntity> candidates =
                player.getServerWorld().getEntitiesByClass(
                        LivingEntity.class,
                        area,
                        entity ->
                                entity != player
                                        && entity.isAlive()
                                        && isEasyNpc(entity)
                                        && matchesRole(entity, wantedName, guardOnly));

        if (candidates.isEmpty()) {
            return 0;
        }

        LivingEntity target =
                candidates.stream()
                        .min(Comparator.<LivingEntity>comparingDouble(entity -> cameraTargetScore(player, entity))
                                .thenComparingDouble(entity -> player.squaredDistanceTo(entity))
                                .thenComparingInt(LivingEntity::getId))
                        .orElse(null);

        if (target == null || player.squaredDistanceTo(target) > MAX_RANGE * MAX_RANGE) {
            return 0;
        }

        return DialogueManager.ouvrir(player, target, DialogueMode.CAPTURE) != null ? 1 : 0;
    }

    private static boolean isEasyNpc(LivingEntity entity) {
        var id = Registries.ENTITY_TYPE.getId(entity.getType());
        return id != null && "easy_npc".equals(id.getNamespace());
    }

    private static boolean matchesRole(
            LivingEntity entity,
            String wantedName,
            boolean guardOnly) {
        String name = normalize(entity.getName().getString());

        if (guardOnly && !name.contains("garde")) {
            return false;
        }

        if (wantedName == null || wantedName.isBlank()) {
            return true;
        }

        String wanted = normalize(wantedName);
        if (name.equals(wanted) || name.contains(wanted)) {
            return true;
        }

        for (String token : wanted.split("\\s+")) {
            if (token.length() > 2 && !name.contains(token)) {
                return false;
            }
        }
        return true;
    }

    private static double cameraTargetScore(
            ServerPlayerEntity player,
            LivingEntity entity) {
        Vec3d from = player.getEyePos();
        Vec3d to = entity.getBoundingBox().getCenter();
        Vec3d delta = to.subtract(from);
        double distance = delta.length();
        if (distance < 0.0001D) {
            return 0.0D;
        }

        Vec3d direction = delta.multiply(1.0D / distance);
        Vec3d look = player.getRotationVec(1.0F);
        double dot = Math.max(-1.0D, Math.min(1.0D, look.dotProduct(direction)));

        // Distance keeps the physically relevant guard preferred. The view penalty
        // resolves the common symmetric-two-guards case in favour of the guard the
        // player is actually facing, without hard-rejecting side-on scripted scenes.
        return distance + (1.0D - dot) * 6.0D;
    }

    private static String normalize(String value) {
        if (value == null) {
            return "";
        }
        return Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "")
                .toLowerCase(Locale.ROOT)
                .replace('’', '\'')
                .trim();
    }
}
