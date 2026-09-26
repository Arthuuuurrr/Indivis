package net.hautecapitale.metiers.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.hautecapitale.metiers.creature.CreatureProfile;
import net.hautecapitale.metiers.data.HcmData;
import net.hautecapitale.metiers.entity.CarcassEntity;
import net.hautecapitale.metiers.skin.SkinningEngine;
import net.hautecapitale.metiers.skin.SkinningFeedback;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.command.argument.Vec3ArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registries;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * {@code /metiers carcasse …} et {@code /metiers depecer …} — poser une
 * carcasse sans tuer, les compter, les purger, et dépecer par commande pour
 * tester le Dépeceur sans client.
 */
public final class SkinCommands {

    private static final SuggestionProvider<ServerCommandSource> SKINNABLE = (context, builder) ->
            CommandSource.suggestIdentifiers(HcmData.CREATURES.all().entrySet().stream()
                    .filter(entry -> entry.getValue().isSkinnable()).map(Map.Entry::getKey), builder);

    private SkinCommands() {
    }

    public static LiteralArgumentBuilder<ServerCommandSource> carcasse() {
        return CommandManager.literal("carcasse")
                .requires(CommandManager.requirePermissionLevel(CommandManager.GAMEMASTERS_CHECK))
                .then(CommandManager.literal("creer")
                        .then(CommandManager.argument("type", IdentifierArgumentType.identifier())
                                .suggests(SKINNABLE)
                                .then(CommandManager.argument("position", Vec3ArgumentType.vec3())
                                        .executes(ctx -> create(ctx, -1))
                                        .then(CommandManager.argument("secondes", IntegerArgumentType.integer(1))
                                                .executes(ctx -> create(ctx, IntegerArgumentType.getInteger(ctx, "secondes")))))))
                .then(CommandManager.literal("liste")
                        .executes(SkinCommands::list))
                .then(CommandManager.literal("purger")
                        .executes(SkinCommands::purge));
    }

    public static LiteralArgumentBuilder<ServerCommandSource> depecer() {
        return CommandManager.literal("depecer")
                .requires(CommandManager.requirePermissionLevel(CommandManager.GAMEMASTERS_CHECK))
                .then(CommandManager.argument("joueur", EntityArgumentType.player())
                        .then(CommandManager.argument("carcasse", EntityArgumentType.entity())
                                .executes(SkinCommands::skin)));
    }

    /** {@code /metiers inspecter depecage} — les créatures dépeçables, par niveau. */
    public static LiteralArgumentBuilder<ServerCommandSource> inspectSkinning() {
        return CommandManager.literal("depecage")
                .executes(ctx -> listSkinnable(ctx.getSource()));
    }

    // ------------------------------------------------------------------

    private static int create(CommandContext<ServerCommandSource> ctx, int seconds) throws CommandSyntaxException {
        Identifier typeId = IdentifierArgumentType.getIdentifier(ctx, "type");
        Vec3d pos = Vec3ArgumentType.getVec3(ctx, "position");
        ServerWorld world = ctx.getSource().getWorld();
        EntityType<?> type = Registries.ENTITY_TYPE.getOptionalValue(typeId).orElse(null);
        if (type == null) {
            ctx.getSource().sendError(Text.literal("Type d'entité inconnu : " + typeId));
            return 0;
        }
        CreatureProfile profile = HcmData.CREATURES.get(typeId);
        if (profile == null || profile.skinning().isEmpty()) {
            ctx.getSource().sendError(Text.literal("Pas de bloc « depecage » dans la fiche de " + typeId
                    + " — la carcasse serait posée mais ne se dépècerait pas."));
            return 0;
        }
        int life = seconds > 0 ? seconds : profile.skinning().get().carcassSeconds();
        CarcassEntity carcass = CarcassEntity.create(world, type, pos, life);
        if (carcass == null) {
            ctx.getSource().sendError(Text.literal(typeId + " n'est pas une créature vivante : pas de carcasse possible."));
            return 0;
        }
        ctx.getSource().sendFeedback(() -> Text.literal("Carcasse de " + typeId + " posée en "
                + String.format(Locale.ROOT, "%.1f %.1f %.1f", pos.x, pos.y, pos.z) + ", pour " + life + " s (id "
                + carcass.getId() + ").").formatted(Formatting.GREEN), true);
        return carcass.getId();
    }

    private static int list(CommandContext<ServerCommandSource> ctx) {
        ServerWorld world = ctx.getSource().getWorld();
        List<CarcassEntity> carcasses = new ArrayList<>();
        for (Entity entity : world.iterateEntities()) {
            if (entity instanceof CarcassEntity carcass && carcass.isAlive()) {
                carcasses.add(carcass);
            }
        }
        carcasses.sort(Comparator.comparingLong(CarcassEntity::createdAt));
        long now = SkinningEngine.now();
        ServerCommandSource source = ctx.getSource();
        source.sendFeedback(() -> Text.literal(carcasses.size() + " carcasse(s) dans "
                + world.getRegistryKey().getValue() + " :").formatted(Formatting.GOLD), false);
        for (CarcassEntity carcass : carcasses) {
            long remaining = Math.max(0L, (carcass.expiresAt() - now + 999L) / 1000L);
            String state = carcass.isClaimed() ? "dépecée" : carcass.channeler() != null ? "en cours" : "libre";
            source.sendFeedback(() -> Text.literal(String.format(Locale.ROOT, "  #%d  %s  %s  %s, expire dans %d s",
                    carcass.getId(), carcass.mobType(), carcass.getBlockPos().toShortString(), state, remaining))
                    .formatted(Formatting.WHITE), false);
        }
        return carcasses.size();
    }

    private static int purge(CommandContext<ServerCommandSource> ctx) {
        ServerWorld world = ctx.getSource().getWorld();
        int purged = 0;
        for (Entity entity : new ArrayList<>(world.getEntitiesByClass(CarcassEntity.class,
                new Box(-3.0E7D, -1000.0D, -3.0E7D, 3.0E7D, 1000.0D, 3.0E7D), Entity::isAlive))) {
            entity.discard();
            purged++;
        }
        int count = purged;
        ctx.getSource().sendFeedback(() -> Text.literal(count + " carcasse(s) retirée(s).").formatted(Formatting.YELLOW), true);
        return purged;
    }

    /** Le même chemin qu'un clic droit du joueur : mêmes refus, même canalisation. */
    private static int skin(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        ServerPlayerEntity player = EntityArgumentType.getPlayer(ctx, "joueur");
        Entity target = EntityArgumentType.getEntity(ctx, "carcasse");
        if (!(target instanceof CarcassEntity carcass)) {
            ctx.getSource().sendError(Text.literal("La cible n'est pas une carcasse."));
            return 0;
        }
        SkinningEngine.Refusal refusal = SkinningEngine.check(player, carcass);
        if (refusal != null) {
            ctx.getSource().sendError(Text.literal(player.getName().getString() + " : " + refusal.name() + " — ")
                    .append(SkinningFeedback.refusal(refusal, carcass)));
            return 0;
        }
        SkinningEngine.start(player, carcass);
        ctx.getSource().sendFeedback(() -> Text.literal(player.getName().getString() + " commence à dépecer la carcasse #"
                + carcass.getId() + " (" + carcass.profile().skinning().orElseThrow().channelSeconds() + " s)."), false);
        return 1;
    }

    private static int listSkinnable(ServerCommandSource source) {
        List<Map.Entry<Identifier, CreatureProfile>> skinnable = new ArrayList<>();
        for (Map.Entry<Identifier, CreatureProfile> entry : HcmData.CREATURES.all().entrySet()) {
            if (entry.getValue().isSkinnable()) {
                skinnable.add(entry);
            }
        }
        skinnable.sort(Comparator.comparingInt((Map.Entry<Identifier, CreatureProfile> e) -> e.getValue().skinning().orElseThrow().level())
                .thenComparing(e -> e.getKey().toString()));
        source.sendFeedback(() -> Text.literal(skinnable.size() + " créature(s) dépeçable(s) :").formatted(Formatting.GOLD), false);
        for (Map.Entry<Identifier, CreatureProfile> entry : skinnable) {
            CreatureProfile.SkinningEntry s = entry.getValue().skinning().orElseThrow();
            boolean present = Registries.ENTITY_TYPE.containsId(entry.getKey());
            var tool = SkinningEngine.requiredTool(s);
            source.sendFeedback(() -> Text.literal(String.format(Locale.ROOT,
                    "  niv. %2d  %4.0f XP  %s → %s%s  carcasse %d s, dépeçage %d s, outil %s%s",
                    s.level(), s.xp(), entry.getKey(), s.material().describe(),
                    s.secondary().isEmpty() ? "" : " (+" + s.secondary().size() + " secondaire(s))",
                    s.carcassSeconds(), s.channelSeconds(), tool == null ? "aucun" : "#" + tool.id(),
                    present ? "" : "  (entité absente)"))
                    .formatted(present ? Formatting.WHITE : Formatting.DARK_GRAY), false);
        }
        return skinnable.size();
    }
}
