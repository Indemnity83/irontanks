package com.indemnity83.irontanks.fabric.client;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import org.jetbrains.annotations.Nullable;

/**
 * Per-frame render snapshot for a tank: how full it is and which fluid sprite/tint to draw. Populated
 * in {@code extractRenderState} (off the render thread) and consumed in {@code submit}.
 */
public class TankRenderState extends BlockEntityRenderState {
    public boolean hasFluid;
    public float fillRatio;
    /** This tank is completely full (so its fluid fills the block height). */
    public boolean full;
    /** Draw the fluid's top surface here; false when connected fluid continues above, so columns merge. */
    public boolean renderTop;
    @Nullable
    public TextureAtlasSprite sprite;
    public int tintColor = 0xFFFFFFFF;
}
