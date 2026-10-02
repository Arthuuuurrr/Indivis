package net.hautecapitale.metiers.client;

import net.hautecapitale.metiers.gadget.MiningDroneEntity;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import net.minecraft.client.render.entity.state.FlyingItemEntityRenderState;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;

/**
 * Le drone minier à l'écran, comme chez MBK : son sprite, toujours face à la
 * caméra, à 0,8, pleinement éclairé — le rendu des boules de neige et des
 * perles de l'Ender. On y ajoute seulement un léger balancement vertical.
 */
public final class MiningDroneRenderer extends FlyingItemEntityRenderer<MiningDroneEntity> {

    /** L'état du sprite, plus le balancement. */
    public static final class State extends FlyingItemEntityRenderState {
        float bob;
    }

    public MiningDroneRenderer(EntityRendererFactory.Context context) {
        super(context, 0.8F, true);
        this.shadowRadius = 0.0F;
    }

    @Override
    public FlyingItemEntityRenderState createRenderState() {
        return new State();
    }

    @Override
    public void updateRenderState(MiningDroneEntity drone, FlyingItemEntityRenderState state, float tickDelta) {
        super.updateRenderState(drone, state, tickDelta);
        if (state instanceof State own) {
            own.bob = drone.bob(tickDelta);
        }
    }

    @Override
    public void render(FlyingItemEntityRenderState state, MatrixStack matrices, OrderedRenderCommandQueue queue, CameraRenderState camera) {
        matrices.push();
        matrices.translate(0.0D, 0.15D + (state instanceof State own ? own.bob : 0.0F), 0.0D);
        super.render(state, matrices, queue, camera);
        matrices.pop();
    }
}
