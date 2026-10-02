package net.hautecapitale.metiers.skin;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.hautecapitale.metiers.api.Metiers;
import net.hautecapitale.metiers.config.MetiersConfig;
import net.hautecapitale.metiers.craft.XpFalloff;
import net.hautecapitale.metiers.creature.CreatureProfile;
import net.hautecapitale.metiers.creature.DropRoll;
import net.hautecapitale.metiers.entity.CarcassEntity;
import net.hautecapitale.metiers.profession.Profession;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.LongSupplier;

/**
 * Le Dépeceur : commencer, tenir, finir.
 *
 * <p>Dépecer n'est pas un clic, c'est une <em>canalisation</em> : le joueur
 * commence, reste à portée, garde son outil en main, et au bout de la durée
 * de la fiche reçoit les matières. S'éloigner, lâcher l'outil, perdre la
 * carcasse : tout s'annule, sans rien donner.
 *
 * <p>Deux verrous font qu'un seul Dépeceur obtient tout : la carcasse se
 * réserve au premier qui commence — le second lit « déjà en cours » — et se
 * <em>réclame</em> avant toute distribution, sur le fil du serveur. Même si
 * deux canalisations se terminaient au même tick, une seule passerait.
 */
public final class SkinningEngine {

    /** Horloge murale, remplaçable par le diagnostic. */
    private static LongSupplier clock = System::currentTimeMillis;

    /** Une canalisation en cours. */
    public static final class Channel {
        public final ServerPlayerEntity player;
        public final CarcassEntity carcass;
        public final CreatureProfile.SkinningEntry entry;
        public final long startedAt;
        public final long endsAt;
        public final Vec3d startPos;
        public final ItemStack tool;
        int lastReportedSecond = -1;

        Channel(ServerPlayerEntity player, CarcassEntity carcass, CreatureProfile.SkinningEntry entry, long now) {
            this.player = player;
            this.carcass = carcass;
            this.entry = entry;
            this.startedAt = now;
            this.endsAt = now + entry.channelMillis();
            this.startPos = player.getEntityPos();
            this.tool = player.getMainHandStack().copy();
        }

        public double progress(long now) {
            long total = endsAt - startedAt;
            return total <= 0L ? 1.0D : Math.min(1.0D, (now - startedAt) / (double) total);
        }
    }

    public enum Refusal {
        PAS_UNE_CARCASSE,
        FICHE_INCONNUE,
        METIER_NON_APPRIS,
        NIVEAU_INSUFFISANT,
        MAUVAIS_OUTIL,
        TROP_LOIN,
        DEJA_EN_COURS,
        DEJA_PRISE
    }

    public enum Cancel {
        TROP_LOIN,
        A_BOUGE,
        OUTIL_LACHE,
        CARCASSE_DISPARUE,
        PRISE_PAR_UN_AUTRE,
        JOUEUR_PARTI
    }

    public record Outcome(CreatureProfile.SkinningEntry entry, double xpGained, int levelsGained,
                          List<ItemStack> materials) {
    }

    private static final Map<UUID, Channel> CHANNELS = new LinkedHashMap<>();

    private SkinningEngine() {
    }

    public static void init() {
        ServerTickEvents.END_SERVER_TICK.register(SkinningEngine::tick);
    }

    public static long now() {
        return clock.getAsLong();
    }

    /** Pour le diagnostic : avancer ou remettre l'horloge. {@code null} = heure réelle. */
    public static void setClock(LongSupplier supplier) {
        clock = supplier == null ? System::currentTimeMillis : supplier;
    }

    // ------------------------------------------------------------------
    // Commencer

    /** Peut-on commencer ? {@code null} = oui. */
    public static Refusal check(ServerPlayerEntity player, CarcassEntity carcass) {
        if (carcass == null || carcass.isRemoved()) {
            return Refusal.PAS_UNE_CARCASSE;
        }
        CreatureProfile profile = carcass.profile();
        if (profile == null || profile.skinning().isEmpty()) {
            return Refusal.FICHE_INCONNUE;
        }
        CreatureProfile.SkinningEntry entry = profile.skinning().get();
        if (carcass.isClaimed()) {
            return Refusal.DEJA_PRISE;
        }
        if (carcass.channeler() != null && !carcass.channeler().equals(player.getUuid())) {
            return Refusal.DEJA_EN_COURS;
        }
        if (!Metiers.hasProfession(player, Profession.DEPECEUR)) {
            return Refusal.METIER_NON_APPRIS;
        }
        if (Metiers.getLevel(player, Profession.DEPECEUR) < entry.level()) {
            return Refusal.NIVEAU_INSUFFISANT;
        }
        if (!holdsTool(player, entry)) {
            return Refusal.MAUVAIS_OUTIL;
        }
        if (!withinReach(player, carcass)) {
            return Refusal.TROP_LOIN;
        }
        return null;
    }

    /** Commence la canalisation ; en cas de refus, le joueur lit pourquoi. */
    public static boolean start(ServerPlayerEntity player, CarcassEntity carcass) {
        Refusal refusal = check(player, carcass);
        if (refusal != null) {
            SkinningFeedback.refused(player, refusal, carcass);
            return false;
        }
        Channel previous = CHANNELS.get(player.getUuid());
        if (previous != null) {
            if (previous.carcass == carcass) {
                return true; // déjà en train : un second clic ne relance rien
            }
            cancel(previous, null);
        }
        CreatureProfile.SkinningEntry entry = carcass.profile().skinning().orElseThrow();
        Channel channel = new Channel(player, carcass, entry, now());
        carcass.setChanneler(player.getUuid());
        CHANNELS.put(player.getUuid(), channel);
        SkinningFeedback.started(player, channel);
        if (entry.channelMillis() <= 0L) {
            complete(channel);
        }
        return true;
    }

    // ------------------------------------------------------------------
    // Tenir

    /** Chaque tick serveur : vérifier chaque canalisation, finir celles dont l'heure est venue. */
    public static void tick(MinecraftServer server) {
        if (CHANNELS.isEmpty()) {
            return;
        }
        long now = now();
        for (Channel channel : new ArrayList<>(CHANNELS.values())) {
            Cancel cancel = why(channel);
            if (cancel != null) {
                cancel(channel, cancel);
                continue;
            }
            if (now >= channel.endsAt) {
                complete(channel);
            } else {
                SkinningFeedback.progress(channel, now);
            }
        }
    }

    private static Cancel why(Channel channel) {
        ServerPlayerEntity player = channel.player;
        CarcassEntity carcass = channel.carcass;
        if (player.isRemoved() || player.isDead()) {
            return Cancel.JOUEUR_PARTI;
        }
        if (carcass.isRemoved()) {
            return Cancel.CARCASSE_DISPARUE;
        }
        if (carcass.isClaimed() && !player.getUuid().equals(carcass.claimedBy())) {
            return Cancel.PRISE_PAR_UN_AUTRE;
        }
        if (!withinReach(player, carcass)) {
            return Cancel.TROP_LOIN;
        }
        if (player.getEntityPos().squaredDistanceTo(channel.startPos) > 0.6D * 0.6D) {
            return Cancel.A_BOUGE;
        }
        if (!holdsTool(player, channel.entry) || !ItemStack.areItemsEqual(player.getMainHandStack(), channel.tool)) {
            return Cancel.OUTIL_LACHE;
        }
        return null;
    }

    public static void cancel(Channel channel, Cancel reason) {
        CHANNELS.remove(channel.player.getUuid(), channel);
        if (channel.player.getUuid().equals(channel.carcass.channeler())) {
            channel.carcass.setChanneler(null);
        }
        if (reason != null) {
            SkinningFeedback.cancelled(channel.player, reason);
        }
    }

    // ------------------------------------------------------------------
    // Finir

    /**
     * La distribution. Le verrou d'abord : si la carcasse est déjà à quelqu'un,
     * on s'arrête là. Ensuite seulement les matières, l'XP, et la carcasse
     * disparaît.
     */
    public static Outcome complete(Channel channel) {
        CHANNELS.remove(channel.player.getUuid(), channel);
        ServerPlayerEntity player = channel.player;
        CarcassEntity carcass = channel.carcass;
        if (carcass.isRemoved() || !carcass.claim(player.getUuid())) {
            SkinningFeedback.cancelled(player, Cancel.PRISE_PAR_UN_AUTRE);
            return null;
        }

        CreatureProfile.SkinningEntry entry = channel.entry;
        ServerWorld world = (ServerWorld) carcass.getEntityWorld();
        List<ItemStack> materials = new ArrayList<>(DropRoll.roll(List.of(entry.material()), world.getRandom()));
        materials.addAll(DropRoll.roll(entry.secondary(), world.getRandom()));
        for (ItemStack stack : materials) {
            give(player, stack);
        }

        int level = Metiers.getLevel(player, Profession.DEPECEUR);
        double xp = XpFalloff.apply(entry.xp(), level, entry.level());
        int levels = xp > 0.0D ? Metiers.addXp(player, Profession.DEPECEUR, xp) : 0;

        carcass.discard();
        Outcome outcome = new Outcome(entry, xp, levels, materials);
        SkinningFeedback.completed(player, outcome);
        return outcome;
    }

    // ------------------------------------------------------------------

    /** La famille d'outils exigée : celle de la fiche, sinon celle de la configuration, sinon aucune. */
    public static TagKey<net.minecraft.item.Item> requiredTool(CreatureProfile.SkinningEntry entry) {
        Identifier id = entry.tool().orElse(MetiersConfig.get().skinningTool());
        return id == null ? null : TagKey.of(RegistryKeys.ITEM, id);
    }

    public static boolean holdsTool(ServerPlayerEntity player, CreatureProfile.SkinningEntry entry) {
        TagKey<net.minecraft.item.Item> tool = requiredTool(entry);
        return tool == null || player.getMainHandStack().isIn(tool);
    }

    /** À portée : la distance des yeux au point le plus proche de la carcasse. */
    public static boolean withinReach(ServerPlayerEntity player, CarcassEntity carcass) {
        double reach = MetiersConfig.get().depecage_portee;
        Vec3d eyes = player.getEyePos();
        Box box = carcass.getBoundingBox();
        double dx = MathHelper.clamp(eyes.x, box.minX, box.maxX) - eyes.x;
        double dy = MathHelper.clamp(eyes.y, box.minY, box.maxY) - eyes.y;
        double dz = MathHelper.clamp(eyes.z, box.minZ, box.maxZ) - eyes.z;
        return dx * dx + dy * dy + dz * dz <= reach * reach;
    }

    private static void give(ServerPlayerEntity player, ItemStack stack) {
        ItemStack copy = stack.copy();
        if (!player.getInventory().insertStack(copy) || !copy.isEmpty()) {
            player.dropItem(copy, false);
        }
    }

    /** Le diagnostic : la canalisation d'un joueur, ou {@code null}. */
    public static Channel channelOf(ServerPlayerEntity player) {
        return CHANNELS.get(player.getUuid());
    }

    /** Le diagnostic : tout oublier. */
    public static void clearChannels() {
        for (Channel channel : new ArrayList<>(CHANNELS.values())) {
            cancel(channel, null);
        }
    }
}
