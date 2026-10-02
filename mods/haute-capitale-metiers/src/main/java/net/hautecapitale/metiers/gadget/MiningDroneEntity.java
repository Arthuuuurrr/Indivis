package net.hautecapitale.metiers.gadget;

import net.minecraft.entity.Entity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.FlyingItemEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.UUID;

/**
 * Le drone minier : il suit son maître et ramasse ce qui traîne autour de lui.
 * Pas d'IA, pas de chemin — il vole en ligne droite vers son épaule, et se
 * rapproche d'un coup quand il est distancé. Il n'est jamais sauvegardé : au
 * redémarrage, le gadget le redéploie.
 *
 * <p>Idée reprise de MBK's Useful Items (CC0) ; réécrit.
 */
public class MiningDroneEntity extends Entity implements FlyingItemEntity {

    /** Ce que le client dessine : le sprite du drone en vol, face à la caméra, comme chez MBK. */
    private static final ItemStack SPRITE = net.minecraft.util.Util.make(new ItemStack(GadgetItems.DRONE_MINIER), stack ->
            stack.set(DataComponentTypes.ITEM_MODEL, net.hautecapitale.metiers.HauteCapitaleMetiers.id("drone_en_vol")));

    private UUID owner;
    private int silentTicks;

    public MiningDroneEntity(EntityType<? extends MiningDroneEntity> type, World world) {
        super(type, world);
        this.noClip = true;
    }

    public void setOwner(UUID owner) {
        this.owner = owner;
    }

    public UUID owner() {
        return owner;
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
    }

    @Override
    public void tick() {
        super.tick();
        if (!(getEntityWorld() instanceof ServerWorld world)) {
            return;
        }
        ServerPlayerEntity master = owner == null ? null : world.getServer().getPlayerManager().getPlayer(owner);
        if (master == null || master.isRemoved() || master.getEntityWorld() != world) {
            discard();
            return;
        }
        double range = Gadgets.config().drone_portee;
        Vec3d target = master.getEntityPos().add(-Math.sin(Math.toRadians(master.bodyYaw)) * -0.8D, 1.9D,
                Math.cos(Math.toRadians(master.bodyYaw)) * -0.8D);
        Vec3d delta = target.subtract(getEntityPos());
        double distance = delta.length();
        if (distance > range) {
            silentTicks++;
            if (silentTicks > 60) {
                Gadgets.overlay(master, Gadgets.text("hcm.gadget.drone.perdu", "Le drone a perdu le signal").formatted(Formatting.RED));
                MiningDroneItem.forget(master, this);
                discard();
                return;
            }
        } else {
            silentTicks = 0;
        }
        if (distance > 8.0D) {
            setPosition(target.x, target.y, target.z);
            setVelocity(Vec3d.ZERO);
        } else if (distance > 0.3D) {
            Vec3d velocity = delta.multiply(0.12D);
            setVelocity(velocity);
            move(MovementType.SELF, velocity);
        } else {
            setVelocity(Vec3d.ZERO);
        }
        setYaw(master.headYaw);

        if (world.getTime() % 5 == 0) {
            collect(world, master);
        }
        // Un drone dont plus aucun gadget du maître ne se souvient — gadget perdu,
        // jeté, ou inventaire vidé — n'a plus de raison de voler.
        if (age % 100 == 0 && !MiningDroneItem.isBound(master, this)) {
            discard();
        }
    }

    /** Ce qui traîne à portée entre dans le sac du maître. */
    public int collect(ServerWorld world, ServerPlayerEntity master) {
        double radius = Gadgets.config().drone_ramassage;
        int collected = 0;
        for (ItemEntity item : world.getEntitiesByClass(ItemEntity.class, getBoundingBox().expand(radius),
                item -> item.isAlive() && !item.cannotPickup())) {
            ItemStack stack = item.getStack();
            int before = stack.getCount();
            if (master.getInventory().insertStack(stack)) {
                collected += before - stack.getCount();
                if (stack.isEmpty()) {
                    item.discard();
                }
            } else {
                collected += before - stack.getCount();
            }
        }
        return collected;
    }

    @Override
    public boolean damage(ServerWorld world, DamageSource source, float amount) {
        return false;
    }

    @Override
    public boolean isAttackable() {
        return false;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean isCollidable(Entity entity) {
        return false;
    }

    @Override
    public boolean shouldSave() {
        return false;
    }

    @Override
    protected void readCustomData(ReadView view) {
    }

    @Override
    protected void writeCustomData(WriteView view) {
    }

    /** Un léger balancement, pour le rendu. */
    public float bob(float tickDelta) {
        return MathHelper.sin((age + tickDelta) / 8.0F) * 0.08F;
    }

    @Override
    public ItemStack getStack() {
        return SPRITE;
    }
}
