package net.hautecapitale.spawns.engine;

import net.hautecapitale.spawns.HauteCapitaleSpawns;
import net.hautecapitale.spawns.api.KillReport;
import net.hautecapitale.spawns.bridge.PartyBridge;
import net.hautecapitale.spawns.config.SpawnsConfig;
import net.hautecapitale.spawns.data.CreditMode;
import net.hautecapitale.spawns.data.QuestDrop;
import net.hautecapitale.spawns.data.Resolved;
import net.hautecapitale.spawns.text.Msg;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

/**
 * Le credit d'un kill et ce qu'il declenche : compteurs de scoreboard et objets de quete.
 *
 * <p>Les compteurs sont le pont sans code vers les quetes en datapack / Easy NPC :
 * un point qui declare {@code counters: ["orc_kills"]} incremente l'objectif
 * {@code orc_kills} de chaque joueur credite. Les objets de quete personnels vont
 * directement dans l'inventaire de chaque credite, avec un tirage independant.
 */
public final class Rewards {

    private static final Random RANDOM = new Random();
    private static final Set<String> WARNED_ITEMS = new HashSet<>();

    private Rewards() {
    }

    /** Les joueurs credites, selon le mode. Tueur d'abord, sans doublon, en ligne seulement. */
    public static List<UUID> credited(MinecraftServer server, CreditMode mode, Optional<ServerPlayerEntity> killer,
                                      List<UUID> participants, Vec3d position, int radius) {
        LinkedHashSet<UUID> out = new LinkedHashSet<>();
        killer.ifPresent(k -> out.add(k.getUuid()));
        if (mode == CreditMode.KILLER) {
            return new ArrayList<>(out);
        }
        out.addAll(participants);
        if (mode == CreditMode.PARTY_NEARBY && PartyBridge.available()) {
            List<UUID> seeds = new ArrayList<>(out);
            double r2 = (double) radius * radius;
            for (UUID seed : seeds) {
                ServerPlayerEntity player = server.getPlayerManager().getPlayer(seed);
                if (player == null) {
                    continue;
                }
                for (ServerPlayerEntity member : PartyBridge.onlineMembers(server, player)) {
                    if (member.getEntityWorld() == player.getEntityWorld()
                            && member.squaredDistanceTo(position.x, position.y, position.z) <= r2) {
                        out.add(member.getUuid());
                    }
                }
            }
        }
        return new ArrayList<>(out);
    }

    public static void apply(MinecraftServer server, KillReport report, Resolved resolved, SpawnsConfig config) {
        if (!resolved.counters().isEmpty()) {
            counters(server, report, resolved.counters(), config);
        }
        if (config.builtInQuestDrops && !resolved.drops().isEmpty()) {
            drops(server, report, resolved.drops());
        }
    }

    private static void counters(MinecraftServer server, KillReport report, List<String> counters, SpawnsConfig config) {
        ServerScoreboard scoreboard = server.getScoreboard();
        for (String name : counters) {
            ScoreboardObjective objective = scoreboard.getNullableObjective(name);
            if (objective == null) {
                if (!config.createMissingObjectives) {
                    continue;
                }
                try {
                    objective = scoreboard.addObjective(name, ScoreboardCriterion.DUMMY, Text.literal(name),
                            ScoreboardCriterion.RenderType.INTEGER, false, null);
                } catch (Throwable t) {
                    HauteCapitaleSpawns.LOGGER.warn("Objectif de scoreboard « {} » impossible a creer : {}", name, t.toString());
                    continue;
                }
            }
            for (UUID uuid : report.credited()) {
                ServerPlayerEntity player = server.getPlayerManager().getPlayer(uuid);
                if (player != null) {
                    scoreboard.getOrCreateScore(player, objective).incrementScore(1);
                }
            }
        }
    }

    private static void drops(MinecraftServer server, KillReport report, List<QuestDrop> drops) {
        ServerWorld world = server.getWorld(report.mob().dimension());
        for (QuestDrop drop : drops) {
            if (!Registries.ITEM.containsId(drop.item())) {
                if (WARNED_ITEMS.add(drop.item().toString())) {
                    HauteCapitaleSpawns.LOGGER.warn("{} : objet de quete inconnu « {} », ignore", report.mob().fullId(), drop.item());
                }
                continue;
            }
            Item item = Registries.ITEM.get(drop.item());
            if (drop.personal()) {
                for (UUID uuid : report.credited()) {
                    ServerPlayerEntity player = server.getPlayerManager().getPlayer(uuid);
                    if (player != null && roll(drop)) {
                        give(player, new ItemStack(item, Math.max(1, drop.count())));
                    }
                }
            } else if (world != null && roll(drop)) {
                Vec3d p = report.position();
                world.spawnEntity(new ItemEntity(world, p.x, p.y + 0.5D, p.z, new ItemStack(item, Math.max(1, drop.count()))));
            }
        }
    }

    private static boolean roll(QuestDrop drop) {
        return RANDOM.nextDouble() * 100.0D < drop.chancePercent();
    }

    private static void give(ServerPlayerEntity player, ItemStack stack) {
        String name = stack.getName().getString();
        int count = stack.getCount();
        player.giveItemStack(stack);
        Msg.actionBar(player, Text.literal("+" + count + " " + name).formatted(Formatting.AQUA)
                .append(Text.literal(" (objet de quete)").formatted(Formatting.GRAY)));
    }
}
