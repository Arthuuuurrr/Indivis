package net.hautecapitale.metiers.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.hautecapitale.metiers.creature.CreatureEnums.SpawnOrigin;
import net.hautecapitale.metiers.creature.CreatureProfile;
import net.hautecapitale.metiers.data.HcmData;
import net.hautecapitale.metiers.hunt.HuntFeedback;
import net.hautecapitale.metiers.hunt.OriginAttachment;
import net.hautecapitale.metiers.hunt.OriginMarker;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * {@code /metiers origine …} et {@code /metiers abattre …} — voir et régler
 * l'origine des créatures, et abattre une proie par commande pour tester le
 * Chasseur sans client.
 */
public final class HuntCommands {

    private static final SuggestionProvider<ServerCommandSource> ORIGINS = (context, builder) ->
            CommandSource.suggestMatching(Arrays.stream(SpawnOrigin.values()).map(SpawnOrigin::asString), builder);

    private HuntCommands() {
    }

    public static LiteralArgumentBuilder<ServerCommandSource> origine() {
        return CommandManager.literal("origine")
                .requires(CommandManager.requirePermissionLevel(CommandManager.GAMEMASTERS_CHECK))
                .then(CommandManager.argument("cibles", EntityArgumentType.entities())
                        .executes(HuntCommands::show)
                        .then(CommandManager.argument("origine", StringArgumentType.word())
                                .suggests(ORIGINS)
                                .executes(HuntCommands::set)));
    }

    public static LiteralArgumentBuilder<ServerCommandSource> abattre() {
        return CommandManager.literal("abattre")
                .requires(CommandManager.requirePermissionLevel(CommandManager.GAMEMASTERS_CHECK))
                .then(CommandManager.argument("joueur", EntityArgumentType.player())
                        .then(CommandManager.argument("cibles", EntityArgumentType.entities())
                                .executes(HuntCommands::slay)));
    }

    /** {@code /metiers inspecter proies} — les créatures chassables, par niveau. */
    public static LiteralArgumentBuilder<ServerCommandSource> inspectPrey() {
        return CommandManager.literal("proies")
                .executes(ctx -> listPrey(ctx.getSource()));
    }

    // ------------------------------------------------------------------

    private static int show(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        Collection<? extends Entity> targets = EntityArgumentType.getEntities(ctx, "cibles");
        int shown = 0;
        for (Entity entity : targets) {
            if (entity instanceof PlayerEntity) {
                continue;
            }
            SpawnOrigin origin = OriginMarker.resolve(entity);
            boolean tagged = OriginMarker.fromCommandTags(entity) != null;
            boolean marked = OriginAttachment.isMarked(entity);
            CreatureProfile profile = HcmData.CREATURES.get(Registries.ENTITY_TYPE.getId(entity.getType()));
            String eligibility = profile == null ? "  (pas de fiche)"
                    : profile.grantsXpFrom(origin) ? "  éligible" : "  non éligible";
            ctx.getSource().sendFeedback(() -> Text.literal(entity.getDisplayName().getString() + " — ")
                    .formatted(Formatting.GRAY)
                    .append(HuntFeedback.origin(origin).formatted(Formatting.GOLD))
                    .append(Text.literal(tagged ? "  [étiquette]" : marked ? "" : "  [sans marque]").formatted(Formatting.DARK_GRAY))
                    .append(Text.literal(eligibility).formatted(profile != null && profile.grantsXpFrom(origin)
                            ? Formatting.GREEN : Formatting.RED)), false);
            shown++;
        }
        if (shown == 0) {
            ctx.getSource().sendError(Text.literal("Aucune créature visée."));
        }
        return shown;
    }

    private static int set(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        String id = StringArgumentType.getString(ctx, "origine");
        SpawnOrigin origin = OriginMarker.byId(id);
        if (origin == null) {
            ctx.getSource().sendError(Text.literal("Origine inconnue : " + id + " — attendu : "
                    + String.join(", ", Arrays.stream(SpawnOrigin.values()).map(SpawnOrigin::asString).toList())));
            return 0;
        }
        Collection<? extends Entity> targets = EntityArgumentType.getEntities(ctx, "cibles");
        int changed = 0;
        for (Entity entity : targets) {
            if (entity instanceof PlayerEntity) {
                continue;
            }
            // Une étiquette l'emporterait sur la marque : on la retire pour que
            // le réglage prenne.
            for (String tag : List.copyOf(entity.getCommandTags())) {
                if (tag.startsWith(OriginMarker.TAG_PREFIX)) {
                    entity.removeCommandTag(tag);
                }
            }
            OriginAttachment.mark(entity, origin);
            changed++;
        }
        int count = changed;
        ctx.getSource().sendFeedback(() -> Text.literal(count + " créature(s) marquée(s) : ")
                .append(HuntFeedback.origin(origin)).formatted(Formatting.GREEN), true);
        return changed;
    }

    /** Abat les cibles comme si le joueur avait porté le coup — même chemin que le jeu. */
    private static int slay(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        ServerPlayerEntity player = EntityArgumentType.getPlayer(ctx, "joueur");
        Collection<? extends Entity> targets = EntityArgumentType.getEntities(ctx, "cibles");
        int slain = 0;
        for (Entity entity : targets) {
            if (!(entity instanceof LivingEntity living) || entity instanceof PlayerEntity
                    || !(entity.getEntityWorld() instanceof ServerWorld world)) {
                continue;
            }
            if (living.damage(world, world.getDamageSources().playerAttack(player), Float.MAX_VALUE)) {
                slain++;
            }
        }
        int count = slain;
        ctx.getSource().sendFeedback(() -> Text.literal(count + " créature(s) abattue(s) par "
                + player.getName().getString() + "."), false);
        return slain;
    }

    private static int listPrey(ServerCommandSource source) {
        List<Map.Entry<Identifier, CreatureProfile>> prey = new ArrayList<>();
        for (Map.Entry<Identifier, CreatureProfile> entry : HcmData.CREATURES.all().entrySet()) {
            if (entry.getValue().isHuntable()) {
                prey.add(entry);
            }
        }
        prey.sort(Comparator.comparingInt((Map.Entry<Identifier, CreatureProfile> e) -> e.getValue().hunter().orElseThrow().level())
                .thenComparing(e -> e.getKey().toString()));
        source.sendFeedback(() -> Text.literal(prey.size() + " proie(s) déclarée(s) :").formatted(Formatting.GOLD), false);
        for (Map.Entry<Identifier, CreatureProfile> entry : prey) {
            CreatureProfile.HunterEntry hunter = entry.getValue().hunter().orElseThrow();
            boolean present = Registries.ENTITY_TYPE.containsId(entry.getKey());
            String origins = String.join(", ", entry.getValue().xpOrigins().stream().map(SpawnOrigin::asString).toList());
            source.sendFeedback(() -> Text.literal(String.format(Locale.ROOT, "  niv. %2d  %4.0f XP  %s  [%s]%s",
                    hunter.level(), hunter.xp(), entry.getKey(), origins,
                    present ? "" : "  (entité absente de cette installation)"))
                    .formatted(present ? Formatting.WHITE : Formatting.DARK_GRAY), false);
        }
        return prey.size();
    }
}
