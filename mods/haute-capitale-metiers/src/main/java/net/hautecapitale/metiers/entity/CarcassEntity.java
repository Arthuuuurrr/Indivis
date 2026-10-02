package net.hautecapitale.metiers.entity;

import net.hautecapitale.metiers.HauteCapitaleMetiers;
import net.hautecapitale.metiers.config.MetiersConfig;
import net.hautecapitale.metiers.creature.CreatureProfile;
import net.hautecapitale.metiers.data.HcmData;
import net.hautecapitale.metiers.skin.SkinningEngine;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.NbtWriteView;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ErrorReporter;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

/**
 * Une carcasse : ce qu'une proie laisse au sol, et qu'un seul Dépeceur peut
 * travailler.
 *
 * <p>Elle ne fait rien d'elle-même — pas d'IA, pas de déplacement, juste une
 * chute jusqu'au sol et une heure d'expiration. Elle transporte au client un
 * <em>instantané</em> de la créature morte (son type et ses données), pour que
 * le client la reconstruise et la dessine avec le rendu de la créature
 * elle-même, couchée : un cerf mort ressemble à un cerf, sans un seul modèle
 * à créer. Cette idée vient de Flaying (licence tous droits réservés) ; aucune
 * ligne n'en est reprise.
 *
 * <p>Deux verrous, côté serveur seulement : {@link #channeler} — qui est en
 * train de la dépecer — et {@link #claimedBy} — qui l'a obtenue. Le second est
 * posé <em>avant</em> de distribuer quoi que ce soit.
 */
public class CarcassEntity extends Entity {

    /** L'identifiant du type de la créature morte, ex. {@code cubeanimals:roedeer}. */
    private static final TrackedData<String> MOB_TYPE =
            DataTracker.registerData(CarcassEntity.class, TrackedDataHandlerRegistry.STRING);
    /** Ses données au moment de la mort, en SNBT — ce qu'il faut pour la reconstruire. */
    private static final TrackedData<String> MOB_DATA =
            DataTracker.registerData(CarcassEntity.class, TrackedDataHandlerRegistry.STRING);
    private static final TrackedData<Float> MOB_WIDTH =
            DataTracker.registerData(CarcassEntity.class, TrackedDataHandlerRegistry.FLOAT);
    private static final TrackedData<Float> MOB_HEIGHT =
            DataTracker.registerData(CarcassEntity.class, TrackedDataHandlerRegistry.FLOAT);

    /** Au-delà, l'instantané ne passe pas dans un paquet : on n'envoie que le type. */
    private static final int MAX_SNAPSHOT_CHARS = 30_000;

    /** Serveur : heure d'expiration, en millisecondes d'époque. */
    private long expiresAt;
    /** Serveur : heure de création — la plus ancienne part quand la zone est saturée. */
    private long createdAt;
    /** Serveur : qui la dépèce en ce moment, ou {@code null}. */
    private UUID channeler;
    /** Serveur : qui l'a obtenue, ou {@code null}. Posé avant toute distribution. */
    private UUID claimedBy;

    /** Client : ce que le rendu garde d'un appel à l'autre (la créature reconstruite). */
    public Object renderCache;
    /** Client : le rendu par reconstruction a échoué pour cette carcasse — on dessine le repli. */
    public boolean renderBroken;

    public CarcassEntity(EntityType<? extends CarcassEntity> type, World world) {
        super(type, world);
        this.noClip = false;
    }

    // ------------------------------------------------------------------
    // Création

    /**
     * Pose une carcasse là où une créature vient de mourir, ou rien si la zone
     * en est déjà saturée (la plus ancienne cède alors sa place).
     */
    public static CarcassEntity spawnFrom(ServerWorld world, LivingEntity dead, CreatureProfile profile) {
        CreatureProfile.SkinningEntry skinning = profile.skinning().orElse(null);
        if (skinning == null) {
            return null;
        }
        CarcassEntity carcass = new CarcassEntity(HcmEntities.CARCASS, world);
        carcass.setSnapshot(dead);
        carcass.refreshPositionAndAngles(dead.getX(), dead.getY(), dead.getZ(), dead.bodyYaw, 0.0F);
        long now = SkinningEngine.now();
        carcass.createdAt = now;
        carcass.expiresAt = now + skinning.carcassMillis();

        thinOut(world, carcass.getEntityPos());
        world.spawnEntity(carcass);
        return carcass;
    }

    /** Une carcasse d'un type donné, sans créature morte — pour l'administration et le diagnostic. */
    public static CarcassEntity create(ServerWorld world, EntityType<?> mobType, Vec3d pos, int carcassSeconds) {
        Entity template = mobType.create(world, net.minecraft.entity.SpawnReason.LOAD);
        if (!(template instanceof LivingEntity living)) {
            return null;
        }
        living.refreshPositionAndAngles(pos.x, pos.y, pos.z, 0.0F, 0.0F);
        CarcassEntity carcass = new CarcassEntity(HcmEntities.CARCASS, world);
        carcass.setSnapshot(living);
        carcass.refreshPositionAndAngles(pos.x, pos.y, pos.z, 0.0F, 0.0F);
        long now = SkinningEngine.now();
        carcass.createdAt = now;
        carcass.expiresAt = now + carcassSeconds * 1000L;
        thinOut(world, pos);
        world.spawnEntity(carcass);
        living.discard();
        return carcass;
    }

    private static void thinOut(ServerWorld world, Vec3d around) {
        int max = MetiersConfig.get().carcasses_max_par_zone;
        List<CarcassEntity> nearby = world.getEntitiesByClass(CarcassEntity.class,
                Box.of(around, 32.0D, 32.0D, 32.0D), Entity::isAlive);
        if (nearby.size() >= max) {
            nearby.stream().min(Comparator.comparingLong(carcass -> carcass.createdAt)).ifPresent(Entity::discard);
        }
    }

    private void setSnapshot(LivingEntity dead) {
        Identifier type = Registries.ENTITY_TYPE.getId(dead.getType());
        this.dataTracker.set(MOB_TYPE, type.toString());

        String data = "";
        try {
            NbtWriteView view = NbtWriteView.create(ErrorReporter.EMPTY, dead.getRegistryManager());
            dead.writeData(view);
            NbtCompound nbt = view.getNbt();
            // Ce qui n'aide pas à la ressemblance et alourdit : les passagers,
            // la mémoire, l'identité.
            nbt.remove("Passengers");
            nbt.remove("Brain");
            nbt.remove("UUID");
            nbt.remove("leash");
            data = nbt.toString();
            if (data.length() > MAX_SNAPSHOT_CHARS) {
                data = "";
            }
        } catch (RuntimeException e) {
            HauteCapitaleMetiers.LOGGER.warn("Carcasse de {} : instantané impossible, rendu par le type seul ({})",
                    type, e.toString());
        }
        this.dataTracker.set(MOB_DATA, data);

        EntityDimensions dims = dead.getType().getDimensions();
        this.dataTracker.set(MOB_WIDTH, dims.width());
        this.dataTracker.set(MOB_HEIGHT, dims.height());
        calculateDimensions();
    }

    // ------------------------------------------------------------------
    // Ce que le client et le serveur savent

    public Identifier mobType() {
        return Identifier.tryParse(this.dataTracker.get(MOB_TYPE));
    }

    /** L'instantané SNBT, vide si la créature n'a pas pu être capturée. */
    public String mobData() {
        return this.dataTracker.get(MOB_DATA);
    }

    public float mobWidth() {
        return this.dataTracker.get(MOB_WIDTH);
    }

    public float mobHeight() {
        return this.dataTracker.get(MOB_HEIGHT);
    }

    /** La fiche de la créature dont c'est la carcasse, ou {@code null}. */
    public CreatureProfile profile() {
        Identifier type = mobType();
        return type == null ? null : HcmData.CREATURES.get(type);
    }

    // ------------------------------------------------------------------
    // Verrous — serveur

    public UUID channeler() {
        return channeler;
    }

    public void setChanneler(UUID player) {
        this.channeler = player;
    }

    public boolean isClaimed() {
        return claimedBy != null;
    }

    public UUID claimedBy() {
        return claimedBy;
    }

    /**
     * Le verrou : réussit une seule fois. Sur le fil du serveur, deux appels
     * ne peuvent pas se croiser ; le second trouve la carcasse prise.
     */
    public boolean claim(UUID player) {
        if (claimedBy != null) {
            return false;
        }
        claimedBy = player;
        return true;
    }

    public long expiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(long expiresAt) {
        this.expiresAt = expiresAt;
    }

    public long createdAt() {
        return createdAt;
    }

    // ------------------------------------------------------------------
    // Comportement

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        builder.add(MOB_TYPE, "minecraft:pig");
        builder.add(MOB_DATA, "");
        builder.add(MOB_WIDTH, 0.9F);
        builder.add(MOB_HEIGHT, 0.9F);
    }

    @Override
    public void onTrackedDataSet(TrackedData<?> data) {
        super.onTrackedDataSet(data);
        if (MOB_WIDTH.equals(data) || MOB_HEIGHT.equals(data)) {
            calculateDimensions();
        }
        if (MOB_TYPE.equals(data) || MOB_DATA.equals(data)) {
            renderCache = null;
            renderBroken = false;
        }
    }

    /** Couchée : sa boîte a la longueur de la créature et la hauteur de son flanc. */
    @Override
    public EntityDimensions getDimensions(EntityPose pose) {
        float w = Math.max(0.5F, mobWidth());
        float h = Math.max(0.3F, mobHeight());
        return EntityDimensions.changing(Math.max(w, Math.min(h, 3.0F)), Math.min(h, Math.max(0.3F, w * 0.6F)));
    }

    @Override
    public void tick() {
        super.tick();
        if (getEntityWorld() instanceof ServerWorld) {
            if (SkinningEngine.now() >= expiresAt) {
                discard();
                return;
            }
            if (!isOnGround()) {
                setVelocity(getVelocity().add(0.0D, -0.04D, 0.0D));
            }
            if (!getVelocity().equals(Vec3d.ZERO)) {
                move(MovementType.SELF, getVelocity());
                setVelocity(isOnGround() ? Vec3d.ZERO : getVelocity().multiply(0.98D));
            }
        }
    }

    /** Clic droit : le Dépeceur commence à travailler. */
    @Override
    public ActionResult interact(PlayerEntity player, Hand hand) {
        if (hand != Hand.MAIN_HAND) {
            return ActionResult.PASS;
        }
        if (!(player instanceof ServerPlayerEntity serverPlayer)) {
            return ActionResult.SUCCESS;
        }
        return SkinningEngine.start(serverPlayer, this) ? ActionResult.CONSUME : ActionResult.FAIL;
    }

    @Override
    public boolean canHit() {
        return true;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean isCollidable(Entity entity) {
        return false;
    }

    /** Une carcasse ne se casse pas à coups d'épée : elle se dépèce, ou elle expire. */
    @Override
    public boolean damage(ServerWorld world, DamageSource source, float amount) {
        return false;
    }

    @Override
    public boolean isAttackable() {
        return false;
    }

    /** Jamais écrite sur le disque : au redémarrage, il n'y a plus de carcasses, et rien n'est dupliqué. */
    @Override
    public boolean shouldSave() {
        return false;
    }

    @Override
    protected void readCustomData(ReadView view) {
        // Rien : une carcasse ne se relit pas.
    }

    @Override
    protected void writeCustomData(WriteView view) {
        // Rien : une carcasse ne s'écrit pas.
    }
}
