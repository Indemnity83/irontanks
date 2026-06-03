package com.indemnity83.irontanks.neoforge.client;

import com.indemnity83.irontanks.neoforge.content.TankBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;

/**
 * Draws the fluid level inside a tank: the fluid's own still sprite and tint, at a surface height that
 * tracks the tank's fill ratio. Geometry is submitted as custom quads inset to match the glass model's
 * inner box (2..14 px).
 */
public class TankBlockEntityRenderer implements BlockEntityRenderer<TankBlockEntity, TankRenderState> {

    // The tank model's inner box is 2..14 px; inset the fluid slightly more to avoid z-fighting walls.
    private static final float MIN = 2.5F / 16F;
    private static final float MAX = 13.5F / 16F;
    private static final float FLOOR = 0.5F / 16F;
    private static final float CEIL = 15.5F / 16F;

    public TankBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public TankRenderState createRenderState() {
        return new TankRenderState();
    }

    @Override
    public void extractRenderState(
            TankBlockEntity tank,
            TankRenderState state,
            float tickDelta,
            Vec3 cameraPos,
            ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
        BlockEntityRenderState.extractBase(tank, state, crumblingOverlay);

        long capacity = tank.capacity();
        long amount = tank.amount();
        Level level = tank.getLevel();
        state.hasFluid = !tank.fluidResource().isEmpty() && amount > 0 && capacity > 0 && level != null;
        state.fillRatio = state.hasFluid ? Math.min(1.0F, (float) amount / capacity) : 0.0F;

        if (state.hasFluid) {
            FluidState fluidState = tank.fluidResource().value().defaultFluidState();
            // 26.1 resolves fluid sprites/tint through the baked fluid-model set.
            FluidModel model = Minecraft.getInstance().getModelManager().getFluidStateModelSet().get(fluidState);
            state.sprite = model.stillMaterial().sprite();
            state.tintColor = model.tintSource().color(fluidState.createLegacyBlock());
        } else {
            state.sprite = null;
        }
    }

    @Override
    public void submit(TankRenderState state, PoseStack pose, SubmitNodeCollector queue, CameraRenderState camera) {
        if (!state.hasFluid || state.sprite == null || state.fillRatio <= 0.0F) {
            return;
        }
        TextureAtlasSprite sprite = state.sprite;
        // getTintColor may omit alpha (0x00RRGGBB); force opaque so the surface is visible.
        int color = (state.tintColor & 0xFF000000) == 0 ? state.tintColor | 0xFF000000 : state.tintColor;
        int light = state.lightCoords;
        float surface = FLOOR + state.fillRatio * (CEIL - FLOOR);

        queue.submitCustomGeometry(pose, RenderTypes.translucentMovingBlock(),
                (entry, buffer) -> renderFluid(entry, buffer, sprite, color, light, surface));
    }

    private static void renderFluid(
            PoseStack.Pose entry, VertexConsumer buffer, TextureAtlasSprite sprite, int color, int light, float top) {
        // Top surface (normal +Y), seen from above.
        quad(entry, buffer, sprite, color, light, 0, 1, 0,
                MIN, top, MIN, MIN, top, MAX, MAX, top, MAX, MAX, top, MIN);
        // North (-Z) and South (+Z) walls.
        quad(entry, buffer, sprite, color, light, 0, 0, -1,
                MIN, FLOOR, MIN, MAX, FLOOR, MIN, MAX, top, MIN, MIN, top, MIN);
        quad(entry, buffer, sprite, color, light, 0, 0, 1,
                MAX, FLOOR, MAX, MIN, FLOOR, MAX, MIN, top, MAX, MAX, top, MAX);
        // West (-X) and East (+X) walls.
        quad(entry, buffer, sprite, color, light, -1, 0, 0,
                MIN, FLOOR, MAX, MIN, FLOOR, MIN, MIN, top, MIN, MIN, top, MAX);
        quad(entry, buffer, sprite, color, light, 1, 0, 0,
                MAX, FLOOR, MIN, MAX, FLOOR, MAX, MAX, top, MAX, MAX, top, MIN);
    }

    private static void quad(
            PoseStack.Pose entry, VertexConsumer buffer, TextureAtlasSprite sprite, int color, int light,
            float nx, float ny, float nz,
            float x1, float y1, float z1, float x2, float y2, float z2,
            float x3, float y3, float z3, float x4, float y4, float z4) {
        float u0 = sprite.getU0();
        float u1 = sprite.getU1();
        float v0 = sprite.getV0();
        float v1 = sprite.getV1();
        vertex(entry, buffer, color, light, nx, ny, nz, x1, y1, z1, u0, v1);
        vertex(entry, buffer, color, light, nx, ny, nz, x2, y2, z2, u1, v1);
        vertex(entry, buffer, color, light, nx, ny, nz, x3, y3, z3, u1, v0);
        vertex(entry, buffer, color, light, nx, ny, nz, x4, y4, z4, u0, v0);
    }

    private static void vertex(
            PoseStack.Pose entry, VertexConsumer buffer, int color, int light,
            float nx, float ny, float nz, float x, float y, float z, float u, float v) {
        buffer.addVertex(entry, x, y, z)
                .setColor(color)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(light)
                .setNormal(entry, nx, ny, nz);
    }
}
