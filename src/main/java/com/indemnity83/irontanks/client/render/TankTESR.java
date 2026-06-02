package com.indemnity83.irontanks.client.render;

import com.indemnity83.irontanks.common.tiles.TankTile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import org.lwjgl.opengl.GL11;

/**
 * Draws the fluid contained in a tank, scaled to the fill level. Iron Tanks used to inherit this from
 * BuildCraft's tank renderer; now that the tile is self-contained, we render the fluid ourselves.
 */
public class TankTESR extends TileEntitySpecialRenderer<TankTile> {

    // Inset slightly inside the glass shell (which spans 2/16..14/16 horizontally, 0..16/16 vertically).
    private static final double MIN = 2.1 / 16.0;
    private static final double MAX = 13.9 / 16.0;
    private static final double BOTTOM = 0.1 / 16.0;
    private static final double TOP = 15.9 / 16.0;

    @Override
    public void render(TankTile tank, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        FluidStack fluidStack = tank.getFluid();
        if (fluidStack == null || fluidStack.amount <= 0) {
            return;
        }
        int capacity = tank.getCapacity();
        if (capacity <= 0) {
            return;
        }

        Fluid fluid = fluidStack.getFluid();
        TextureAtlasSprite sprite = Minecraft.getMinecraft().getTextureMapBlocks()
                .getAtlasSprite(fluid.getStill(fluidStack).toString());
        if (sprite == null) {
            return;
        }

        int color = fluid.getColor(fluidStack);
        float r = ((color >> 16) & 0xFF) / 255f;
        float g = ((color >> 8) & 0xFF) / 255f;
        float b = (color & 0xFF) / 255f;
        float a = ((color >> 24) & 0xFF) / 255f;
        if (a <= 0f) {
            a = 1f;
        }

        double ratio = Math.min(1.0, (double) fluidStack.amount / capacity);
        double top = BOTTOM + (TOP - BOTTOM) * ratio;

        double u1 = sprite.getMinU();
        double u2 = sprite.getMaxU();
        double v1 = sprite.getMinV();
        double v2 = sprite.getMaxV();

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        GlStateManager.disableLighting();
        GlStateManager.disableCull();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.color(1f, 1f, 1f, 1f);
        Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);

        // Top surface
        buffer.pos(MIN, top, MIN).tex(u1, v1).color(r, g, b, a).endVertex();
        buffer.pos(MIN, top, MAX).tex(u1, v2).color(r, g, b, a).endVertex();
        buffer.pos(MAX, top, MAX).tex(u2, v2).color(r, g, b, a).endVertex();
        buffer.pos(MAX, top, MIN).tex(u2, v1).color(r, g, b, a).endVertex();

        // North face (z = MIN)
        buffer.pos(MIN, BOTTOM, MIN).tex(u1, v2).color(r, g, b, a).endVertex();
        buffer.pos(MAX, BOTTOM, MIN).tex(u2, v2).color(r, g, b, a).endVertex();
        buffer.pos(MAX, top, MIN).tex(u2, v1).color(r, g, b, a).endVertex();
        buffer.pos(MIN, top, MIN).tex(u1, v1).color(r, g, b, a).endVertex();

        // South face (z = MAX)
        buffer.pos(MIN, BOTTOM, MAX).tex(u1, v2).color(r, g, b, a).endVertex();
        buffer.pos(MIN, top, MAX).tex(u1, v1).color(r, g, b, a).endVertex();
        buffer.pos(MAX, top, MAX).tex(u2, v1).color(r, g, b, a).endVertex();
        buffer.pos(MAX, BOTTOM, MAX).tex(u2, v2).color(r, g, b, a).endVertex();

        // West face (x = MIN)
        buffer.pos(MIN, BOTTOM, MIN).tex(u2, v2).color(r, g, b, a).endVertex();
        buffer.pos(MIN, top, MIN).tex(u2, v1).color(r, g, b, a).endVertex();
        buffer.pos(MIN, top, MAX).tex(u1, v1).color(r, g, b, a).endVertex();
        buffer.pos(MIN, BOTTOM, MAX).tex(u1, v2).color(r, g, b, a).endVertex();

        // East face (x = MAX)
        buffer.pos(MAX, BOTTOM, MIN).tex(u1, v2).color(r, g, b, a).endVertex();
        buffer.pos(MAX, BOTTOM, MAX).tex(u2, v2).color(r, g, b, a).endVertex();
        buffer.pos(MAX, top, MAX).tex(u2, v1).color(r, g, b, a).endVertex();
        buffer.pos(MAX, top, MIN).tex(u1, v1).color(r, g, b, a).endVertex();

        tessellator.draw();

        GlStateManager.disableBlend();
        GlStateManager.enableCull();
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
    }
}
