package net.hautecapitale.spawns.command;

import net.hautecapitale.spawns.HauteCapitaleSpawns;
import net.hautecapitale.spawns.config.SpawnsConfig;
import net.hautecapitale.spawns.data.PointStatus;
import net.hautecapitale.spawns.data.Resolved;
import net.hautecapitale.spawns.engine.SpawnEngine;
import net.hautecapitale.spawns.state.PointState;
import net.hautecapitale.spawns.store.PointRef;
import net.hautecapitale.spawns.text.Msg;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.EntitiesDestroyS2CPacket;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityTrackerUpdateS2CPacket;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.NbtReadView;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.ErrorReporter;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * L'affichage de debogage des administrateurs : {@code /mmospawn debug on}.
 *
 * <p>Tout est envoye <b>au seul admin</b> concerne : particules ciblees et
 * entites d'affichage factices (paquets d'apparition sans entite serveur). Les
 * joueurs normaux ne voient rien, et rien n'existe dans le monde.
 */
public final class DebugView {

    private static final int REFRESH_TICKS = 10;
    private static final double LABEL_HEIGHT = 2.6D;
    private static final int COLOR_ALIVE = 0x55FF55;
    private static final int COLOR_DEAD = 0xFF5555;
    private static final int COLOR_READY = 0xFFFF55;
    private static final int COLOR_DISABLED = 0x999999;
    private static final int COLOR_INVALID = 0xFF55FF;
    private static final int COLOR_LEASH = 0x55AAFF;

    private static final class Label {
        final int fakeId;
        final UUID uuid = UUID.randomUUID();
        String lastText = "";

        Label(int fakeId) {
            this.fakeId = fakeId;
        }
    }

    private static final Map<UUID, Map<String, Label>> VIEWERS = new HashMap<>();
    private static int nextFakeId = -30_000_000;

    private DebugView() {
    }

    public static boolean isOn(ServerPlayerEntity player) {
        return VIEWERS.containsKey(player.getUuid());
    }

    public static void set(ServerPlayerEntity player, boolean on) {
        if (on) {
            VIEWERS.computeIfAbsent(player.getUuid(), k -> new HashMap<>());
        } else {
            clear(player);
        }
    }

    public static void clear(ServerPlayerEntity player) {
        Map<String, Label> labels = VIEWERS.remove(player.getUuid());
        if (labels != null && !labels.isEmpty() && player.networkHandler != null) {
            int[] ids = labels.values().stream().mapToInt(l -> l.fakeId).toArray();
            player.networkHandler.sendPacket(new EntitiesDestroyS2CPacket(ids));
        }
    }

    public static void clearAll() {
        VIEWERS.clear();
    }

    public static void tick(SpawnEngine engine) {
        if (VIEWERS.isEmpty() || engine.server().getTicks() % REFRESH_TICKS != 0) {
            return;
        }
        long now = System.currentTimeMillis();
        int range = SpawnsConfig.get().debugRange;
        Iterator<Map.Entry<UUID, Map<String, Label>>> it = VIEWERS.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<UUID, Map<String, Label>> entry = it.next();
            ServerPlayerEntity player = engine.server().getPlayerManager().getPlayer(entry.getKey());
            if (player == null || player.networkHandler == null) {
                it.remove();
                continue;
            }
            try {
                render(engine, player, entry.getValue(), now, range);
            } catch (Throwable t) {
                HauteCapitaleSpawns.LOGGER.warn("Affichage de debogage interrompu pour {} : {}", player.getName().getString(), t.toString());
            }
        }
    }

    private static void render(SpawnEngine engine, ServerPlayerEntity player, Map<String, Label> labels, long now, int range) {
        ServerWorld world = player.getEntityWorld();
        Identifier dimension = world.getRegistryKey().getValue();
        int chunkRadius = (range >> 4) + 1;
        int pcx = player.getChunkPos().x;
        int pcz = player.getChunkPos().z;
        Set<String> visible = new HashSet<>();
        String selected = Selection.selected(player.getUuid()).orElse("");
        double r2 = (double) range * range;
        for (int dx = -chunkRadius; dx <= chunkRadius; dx++) {
            for (int dz = -chunkRadius; dz <= chunkRadius; dz++) {
                for (PointRef ref : engine.registry().refsInChunk(dimension, PointRef.chunkKey(pcx + dx, pcz + dz))) {
                    if (ref.point().squaredDistanceTo(player.getX(), player.getY(), player.getZ()) > r2) {
                        continue;
                    }
                    visible.add(ref.fullId());
                    PointState state = engine.store().get(ref.fullId()).orElse(null);
                    PointStatus status = state == null ? PointStatus.READY : state.status();
                    int color = colorOf(ref, status);
                    particles(world, player, ref, color, ref.fullId().equals(selected));
                    Text text = labelText(engine, ref, state, status, now, ref.fullId().equals(selected));
                    label(world, player, labels, ref, text);
                }
            }
        }
        List<Integer> gone = new ArrayList<>();
        Iterator<Map.Entry<String, Label>> it = labels.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Label> e = it.next();
            if (!visible.contains(e.getKey())) {
                gone.add(e.getValue().fakeId);
                it.remove();
            }
        }
        if (!gone.isEmpty()) {
            player.networkHandler.sendPacket(new EntitiesDestroyS2CPacket(gone.stream().mapToInt(Integer::intValue).toArray()));
        }
    }

    private static int colorOf(PointRef ref, PointStatus status) {
        if (!ref.isValid()) {
            return COLOR_INVALID;
        }
        return switch (status) {
            case ALIVE -> COLOR_ALIVE;
            case DEAD_WAITING -> COLOR_DEAD;
            case READY -> COLOR_READY;
            case DISABLED -> COLOR_DISABLED;
        };
    }

    private static void particles(ServerWorld world, ServerPlayerEntity player, PointRef ref, int color, boolean selected) {
        double x = ref.point().x();
        double y = ref.point().y();
        double z = ref.point().z();
        ParticleEffect dust = new DustParticleEffect(color, selected ? 1.6F : 1.0F);
        for (int i = 0; i < 6; i++) {
            world.spawnParticles(player, dust, true, false, x, y + 0.2D + i * 0.35D, z, 1, 0, 0, 0, 0);
        }
        // fleche d'orientation : trois points dans la direction du regard
        double yawRad = Math.toRadians(ref.point().yaw());
        double fx = -Math.sin(yawRad);
        double fz = Math.cos(yawRad);
        for (int i = 1; i <= 3; i++) {
            world.spawnParticles(player, dust, true, false, x + fx * 0.4D * i, y + 0.3D, z + fz * 0.4D * i, 1, 0, 0, 0, 0);
        }
        if (selected) {
            ref.resolved().ifPresent(resolved -> {
                if (resolved.leash().isEnabled()) {
                    ring(world, player, x, y + 0.5D, z, resolved.leash().radius(), new DustParticleEffect(COLOR_LEASH, 1.2F), 48);
                }
                if (resolved.wanderRadius() > 0) {
                    ring(world, player, x, y + 0.3D, z, resolved.wanderRadius(), new DustParticleEffect(COLOR_READY, 0.8F), 24);
                }
            });
        }
    }

    private static void ring(ServerWorld world, ServerPlayerEntity player, double cx, double y, double cz, double radius, ParticleEffect effect, int points) {
        for (int i = 0; i < points; i++) {
            double a = (Math.PI * 2 * i) / points;
            world.spawnParticles(player, effect, true, false, cx + Math.cos(a) * radius, y, cz + Math.sin(a) * radius, 1, 0, 0, 0, 0);
        }
    }

    private static Text labelText(SpawnEngine engine, PointRef ref, PointState state, PointStatus status, long now, boolean selected) {
        MutableText text = Text.literal((selected ? "» " : "") + ref.fullId()).formatted(Formatting.GOLD, Formatting.BOLD);
        String entity = ref.resolved().map(r -> r.entity().toString()).orElse("(entite ?)");
        String rank = ref.resolved().map(Resolved::rank).orElse("?");
        String level = ref.resolved().map(r -> r.level() > 1 ? "  Nv " + r.level() : "").orElse("");
        text.append(Text.literal("\n" + entity).formatted(Formatting.WHITE))
                .append(Text.literal("  [" + rank + "]").formatted(Formatting.LIGHT_PURPLE))
                .append(Text.literal(level).formatted(Formatting.GOLD));
        Formatting statusColor = switch (status) {
            case ALIVE -> Formatting.GREEN;
            case DEAD_WAITING -> Formatting.RED;
            case READY -> Formatting.YELLOW;
            case DISABLED -> Formatting.GRAY;
        };
        String statusText = ref.isValid() ? status.asString().toUpperCase() : "INVALIDE";
        String respawn = status == PointStatus.DEAD_WAITING && state != null ? Msg.mmss(state.remainingSeconds(now)) : "--";
        String leash = ref.resolved().map(r -> r.leash().isEnabled() ? String.valueOf(r.leash().radius()) : "aucune").orElse("?");
        text.append(Text.literal("\n" + statusText).formatted(statusColor))
                .append(Text.literal("  Respawn: " + respawn).formatted(Formatting.AQUA))
                .append(Text.literal("  Leash: " + leash).formatted(Formatting.BLUE));
        String mobInfo = "";
        if (state != null && status == PointStatus.ALIVE) {
            LivingEntity living = engine.loadedEntity(state).orElse(null);
            if (living != null) {
                double d = Math.sqrt(ref.point().squaredDistanceTo(living.getX(), living.getY(), living.getZ()));
                mobInfo = String.format("  mob a %.0f m, %.0f/%.0f PV", d, living.getHealth(), living.getMaxHealth());
                if (engine.leash().isReturning(living.getUuid())) {
                    mobInfo += " (retour)";
                }
            } else {
                mobInfo = "  mob non charge";
            }
        }
        text.append(Text.literal("\nzone: " + ref.zone().id()).formatted(Formatting.GRAY))
                .append(Text.literal(mobInfo).formatted(Formatting.DARK_AQUA));
        if (!ref.isValid()) {
            text.append(Text.literal("\n" + String.join(" ; ", ref.problems())).formatted(Formatting.RED));
        }
        return text;
    }

    private static void label(ServerWorld world, ServerPlayerEntity player, Map<String, Label> labels, PointRef ref, Text text) {
        Label label = labels.get(ref.fullId());
        boolean fresh = label == null;
        if (fresh) {
            label = new Label(nextFakeId--);
            labels.put(ref.fullId(), label);
        }
        String serialized = text.toString();
        if (!fresh && serialized.equals(label.lastText)) {
            return;
        }
        label.lastText = serialized;
        DisplayEntity.TextDisplayEntity display = new DisplayEntity.TextDisplayEntity(EntityType.TEXT_DISPLAY, world);
        NbtCompound nbt = new NbtCompound();
        NbtElement encoded = TextCodecs.CODEC.encodeStart(NbtOps.INSTANCE, text).result().orElse(null);
        if (encoded != null) {
            nbt.put("text", encoded);
        }
        nbt.putString("billboard", "center");
        nbt.putInt("background", 0xA0000000);
        nbt.putInt("line_width", 260);
        nbt.putBoolean("see_through", true);
        display.readData(NbtReadView.create(ErrorReporter.EMPTY, world.getRegistryManager(), nbt));
        double x = ref.point().x();
        double y = ref.point().y() + LABEL_HEIGHT;
        double z = ref.point().z();
        List<Packet<?>> packets = new ArrayList<>(2);
        if (fresh) {
            packets.add(new EntitySpawnS2CPacket(label.fakeId, label.uuid, x, y, z, 0.0F, 0.0F, EntityType.TEXT_DISPLAY, 0, Vec3d.ZERO, 0.0D));
        }
        var entries = display.getDataTracker().getChangedEntries();
        if (entries != null && !entries.isEmpty()) {
            packets.add(new EntityTrackerUpdateS2CPacket(label.fakeId, entries));
        }
        for (Packet<?> packet : packets) {
            player.networkHandler.sendPacket(packet);
        }
    }
}
