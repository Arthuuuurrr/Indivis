package net.hautecapitale.metiers.client;

import net.hautecapitale.metiers.HauteCapitaleMetiers;
import net.hautecapitale.metiers.entity.CarcassEntity;
import net.hautecapitale.metiers.item.HcmItems;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.registry.Registries;
import net.minecraft.storage.NbtReadView;
import net.minecraft.util.ErrorReporter;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.Set;

/**
 * Le rendu d'une carcasse : la créature elle-même, couchée.
 *
 * <p>Le client reconstruit la créature morte à partir de l'instantané que la
 * carcasse transporte — son type et ses données —, la place où est la
 * carcasse, lui donne l'état « morte depuis une seconde », et demande à
 * <em>son</em> rendu de la dessiner. Un cerf mort ressemble donc à un cerf,
 * un loup à un loup, sans un seul modèle à créer, et un renderer GeckoLib
 * dessine sa créature comme d'habitude, avec la rotation de mort qu'il
 * applique lui-même.
 *
 * <p>La créature reconstruite n'est jamais ajoutée au monde ni tickée : c'est
 * un mannequin, gardé sur la carcasse d'une image à l'autre.
 *
 * <p>Si une famille de créatures refuse l'exercice — un renderer qui exige un
 * état que le mannequin n'a pas —, la carcasse passe au rendu de repli, une
 * peau posée à plat, et le journal le dit une fois par type. Rien ne plante.
 */
public final class CarcassRenderer extends EntityRenderer<CarcassEntity, CarcassRenderer.State> {

    /** Une image de carcasse. */
    public static final class State extends EntityRenderState {
        EntityRenderState mob;
        boolean fallback;
        float yaw;
        float mobWidth;
        int entityId;
        final ItemRenderState fallbackItem = new ItemRenderState();
    }

    /** L'angle de la pose de mort de Minecraft. */
    private static final float LYING_DEGREES = 90.0F;

    /** Les types dont le rendu par reconstruction a échoué, signalés une fois. */
    private static final Set<Identifier> REPORTED = new HashSet<>();
    /** Les carcasses dont le rendu a échoué à l'image précédente. */
    private final Set<Integer> broken = new HashSet<>();

    private final ItemModelManager itemModels;

    public CarcassRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.itemModels = context.getItemModelManager();
        this.shadowRadius = 0.5F;
        this.shadowOpacity = 0.6F;
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void updateRenderState(CarcassEntity carcass, State state, float tickDelta) {
        super.updateRenderState(carcass, state, tickDelta);
        state.entityId = carcass.getId();
        state.yaw = carcass.getYaw();
        state.mobWidth = carcass.mobWidth();
        state.mob = null;
        state.fallback = carcass.renderBroken || broken.contains(carcass.getId());

        if (!state.fallback) {
            LivingEntity mob = mannequin(carcass);
            if (mob == null) {
                state.fallback = true;
            } else {
                place(mob, carcass);
                try {
                    state.mob = dispatcher.getAndUpdateRenderState(mob, tickDelta);
                    if (state.mob instanceof LivingEntityRenderState living) {
                        living.hurt = false;
                        living.invisible = false;
                    }
                } catch (RuntimeException e) {
                    giveUp(carcass, e);
                    state.mob = null;
                    state.fallback = true;
                }
            }
        }

        if (state.fallback) {
            itemModels.clearAndUpdate(state.fallbackItem, new ItemStack(HcmItems.FOURRURE_COMMUNE),
                    ItemDisplayContext.GROUND, carcass.getEntityWorld(), null, 0);
        }
    }

    @Override
    public void render(State state, MatrixStack matrices, OrderedRenderCommandQueue queue, CameraRenderState camera) {
        if (state.mob != null) {
            // La pose de mort de Minecraft, appliquée ici : la créature roule de 90°
            // autour de son propre axe avant-arrière. Le renderer de la créature
            // appliquera ensuite sa rotation de lacet (180 − yaw) ; en la
            // conjuguant, le roulis se fait bien dans le repère du corps.
            boolean drawn = false;
            matrices.push();
            try {
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F - state.yaw));
                matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(LYING_DEGREES));
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(state.yaw - 180.0F));
                dispatcher.render(state.mob, camera, 0.0D, 0.0D, 0.0D, matrices, queue);
                drawn = true;
            } catch (RuntimeException e) {
                broken.add(state.entityId);
                Identifier type = state.mob.entityType == null ? null : Registries.ENTITY_TYPE.getId(state.mob.entityType);
                report(type, e);
                // La peau à plat prend le relais tout de suite, et dès l'image suivante.
            } finally {
                matrices.pop();
            }
            if (drawn) {
                super.render(state, matrices, queue, camera);
                return;
            }
        }
        renderFallback(state, matrices, queue);
        super.render(state, matrices, queue, camera);
    }

    /** Le repli : une peau posée à plat, à la taille de la créature. */
    private void renderFallback(State state, MatrixStack matrices, OrderedRenderCommandQueue queue) {
        matrices.push();
        matrices.translate(0.0D, 0.03D, 0.0D);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-state.yaw));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90.0F));
        float scale = Math.max(1.0F, Math.min(4.0F, state.mobWidth * 1.6F));
        matrices.scale(scale, scale, scale);
        state.fallbackItem.render(matrices, queue, state.light, OverlayTexture.DEFAULT_UV, 0);
        matrices.pop();
    }

    // ------------------------------------------------------------------

    /** La créature reconstruite, gardée sur la carcasse ; {@code null} si c'est impossible. */
    private LivingEntity mannequin(CarcassEntity carcass) {
        if (carcass.renderCache instanceof LivingEntity cached) {
            return cached;
        }
        World world = carcass.getEntityWorld();
        Identifier typeId = carcass.mobType();
        EntityType<?> type = typeId == null ? null : Registries.ENTITY_TYPE.getOptionalValue(typeId).orElse(null);
        if (type == null) {
            return null;
        }
        try {
            Entity created = type.create(world, SpawnReason.LOAD);
            if (!(created instanceof LivingEntity mob)) {
                return null;
            }
            String data = carcass.mobData();
            if (!data.isEmpty()) {
                try {
                    NbtCompound nbt = StringNbtReader.readCompound(data);
                    mob.readData(NbtReadView.create(ErrorReporter.EMPTY, world.getRegistryManager(), nbt));
                } catch (Exception e) {
                    // Un instantané illisible n'empêche pas le mannequin : il sera
                    // simplement dans sa robe par défaut.
                    report(typeId, e);
                }
            }
            // Ni blessée ni « en train de mourir » : un deathTime positif ferait
            // coucher la créature, mais teinterait aussi de rouge tout renderer qui
            // lit ce champ directement (GeckoLib). On la couche nous-mêmes, voir
            // render. La santé à zéro laisse jouer une animation de mort s'il y en a.
            mob.deathTime = 0;
            mob.hurtTime = 0;
            mob.setHealth(0.0F);
            mob.setInvisible(false);
            carcass.renderCache = mob;
            return mob;
        } catch (RuntimeException e) {
            giveUp(carcass, e);
            return null;
        }
    }

    /** Le mannequin suit la carcasse, sans interpolation parasite. */
    private static void place(LivingEntity mob, CarcassEntity carcass) {
        double x = carcass.getX();
        double y = carcass.getY();
        double z = carcass.getZ();
        mob.setPosition(x, y, z);
        mob.lastRenderX = x;
        mob.lastRenderY = y;
        mob.lastRenderZ = z;
        mob.lastX = x;
        float yaw = carcass.getYaw();
        mob.setYaw(yaw);
        mob.lastYaw = yaw;
        mob.bodyYaw = yaw;
        mob.lastBodyYaw = yaw;
        mob.headYaw = yaw;
        mob.lastHeadYaw = yaw;
        mob.setPitch(0.0F);
        mob.lastPitch = 0.0F;
        mob.setOnGround(true);
    }

    private void giveUp(CarcassEntity carcass, Exception e) {
        carcass.renderBroken = true;
        carcass.renderCache = null;
        report(carcass.mobType(), e);
    }

    private static void report(Identifier type, Exception e) {
        if (type != null && REPORTED.add(type)) {
            HauteCapitaleMetiers.LOGGER.warn("Carcasse de {} : rendu par reconstruction impossible, repli sur la peau à plat ({})",
                    type, e.toString());
        }
    }
}
