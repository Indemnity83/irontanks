package com.indemnity83.irontanks.fabric.client;

import com.indemnity83.irontanks.fabric.content.TankBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;

/**
 * Draws the fluid level inside a tank: the fluid's own still sprite and tint, at a surface height that
 * tracks the tank's fill ratio. Geometry is submitted as custom quads inset to match the glass model's
 * inner box (2..14 px).
 */
public class TankBlockEntityRenderer implements BlockEntityRenderer<TankBlockEntity, TankRenderState> {

    // The tank model's inner box is 2..14 px; inset the fluid horizontally to avoid z-fighting the walls.
    private static final float MIN = 2.5F / 16F;
    private static final float MAX = 13.5F / 16F;
    // Fluid sits on the block floor (0) and a full tank's visible surface stops just shy of the lid.
    private static final float FLOOR = 0.0F;
    private static final float FULL_SURFACE = 15.5F / 16F;

    public TankBlockEntityRenderer(BlockEntityRendererProvider.Context context) {}

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
        state.hasFluid = !tank.fluidVariant().isBlank() && amount > 0 && capacity > 0 && level != null;
        state.fillRatio = state.hasFluid ? Math.min(1.0F, (float) amount / capacity) : 0.0F;
        state.full = state.hasFluid && amount >= capacity;
        // If connected fluid continues above, hide this tank's surface and let its fluid reach the top so
        // the two tanks read as one continuous column.
        state.renderTop = state.hasFluid && !tank.hasFluidAbove();

        if (state.hasFluid) {
            FluidState fluidState = tank.fluidVariant().getFluid().defaultFluidState();
            // 26.1 resolves fluid sprites/tint through the baked fluid-model set.
            FluidModel model = Minecraft.getInstance()
                    .getModelManager()
                    .getFluidStateModelSet()
                    .get(fluidState);
            state.sprite = model.stillMaterial().sprite();
            // Biome-tinted fluids (water) need world context via colorInWorld; tintSource is null for
            // untinted fluids (lava) — fall back to no tint (white) in both the null and non-client cases.
            var tintSource = model.tintSource();
            state.tintColor = tintSource != null && level instanceof BlockAndTintGetter tintGetter
                    ? tintSource.colorInWorld(fluidState.createLegacyBlock(), tintGetter, tank.getBlockPos())
                    : 0xFFFFFFFF;
            // A potion is stored as water carrying a potion_contents component; tint it by the potion's
            // own color so each potion reads distinctly instead of as plain water.
            var potion = tank.fluidVariant().get(DataComponents.POTION_CONTENTS);
            if (potion != null) {
                state.tintColor = potion.getColor();
            }
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
        // Visible surface height; a full tank stops just shy of the lid.
        float surface = state.full ? FULL_SURFACE : state.fillRatio;
        // Walls reach the surface where it's visible, or the full block height where fluid continues above.
        float wallTop = state.renderTop ? surface : 1.0F;
        boolean renderTop = state.renderTop;

        queue.submitCustomGeometry(
                pose,
                RenderTypes.translucentMovingBlock(),
                (entry, buffer) -> renderFluid(entry, buffer, sprite, color, light, surface, wallTop, renderTop));
    }

    private static void renderFluid(
            PoseStack.Pose entry,
            VertexConsumer buffer,
            TextureAtlasSprite sprite,
            int color,
            int light,
            float surface,
            float wallTop,
            boolean renderTop) {
        if (renderTop) {
            // Top surface (normal +Y), seen from above.
            quad(
                    entry, buffer, sprite, color, light, 0, 1, 0, MIN, surface, MIN, MIN, surface, MAX, MAX, surface,
                    MAX, MAX, surface, MIN);
        }
        // North (-Z) and South (+Z) walls.
        quad(
                entry, buffer, sprite, color, light, 0, 0, -1, MIN, FLOOR, MIN, MAX, FLOOR, MIN, MAX, wallTop, MIN, MIN,
                wallTop, MIN);
        quad(
                entry, buffer, sprite, color, light, 0, 0, 1, MAX, FLOOR, MAX, MIN, FLOOR, MAX, MIN, wallTop, MAX, MAX,
                wallTop, MAX);
        // West (-X) and East (+X) walls.
        quad(
                entry, buffer, sprite, color, light, -1, 0, 0, MIN, FLOOR, MAX, MIN, FLOOR, MIN, MIN, wallTop, MIN, MIN,
                wallTop, MAX);
        quad(
                entry, buffer, sprite, color, light, 1, 0, 0, MAX, FLOOR, MIN, MAX, FLOOR, MAX, MAX, wallTop, MAX, MAX,
                wallTop, MIN);
    }

    private static void quad(
            PoseStack.Pose entry,
            VertexConsumer buffer,
            TextureAtlasSprite sprite,
            int color,
            int light,
            float nx,
            float ny,
            float nz,
            float x1,
            float y1,
            float z1,
            float x2,
            float y2,
            float z2,
            float x3,
            float y3,
            float z3,
            float x4,
            float y4,
            float z4) {
        float u0 = sprite.getU0();
        float u1 = sprite.getU1();
        float v0 = sprite.getV0();
        float v1 = sprite.getV1();
        // Front face.
        vertex(entry, buffer, color, light, nx, ny, nz, x1, y1, z1, u0, v1);
        vertex(entry, buffer, color, light, nx, ny, nz, x2, y2, z2, u1, v1);
        vertex(entry, buffer, color, light, nx, ny, nz, x3, y3, z3, u1, v0);
        vertex(entry, buffer, color, light, nx, ny, nz, x4, y4, z4, u0, v0);
        // Back face (reverse winding, flipped normal) so it shows from both sides regardless of culling.
        vertex(entry, buffer, color, light, -nx, -ny, -nz, x4, y4, z4, u0, v0);
        vertex(entry, buffer, color, light, -nx, -ny, -nz, x3, y3, z3, u1, v0);
        vertex(entry, buffer, color, light, -nx, -ny, -nz, x2, y2, z2, u1, v1);
        vertex(entry, buffer, color, light, -nx, -ny, -nz, x1, y1, z1, u0, v1);
    }

    private static void vertex(
            PoseStack.Pose entry,
            VertexConsumer buffer,
            int color,
            int light,
            float nx,
            float ny,
            float nz,
            float x,
            float y,
            float z,
            float u,
            float v) {
        buffer.addVertex(entry, x, y, z)
                .setColor(color)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(light)
                .setNormal(entry, nx, ny, nz);
    }
}
