package com.indemnity83.irontanks.neoforge.client;

import com.indemnity83.irontanks.core.FluidBody;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import org.jetbrains.annotations.Nullable;

/**
 * Per-frame render snapshot for a tank: how full it is and which fluid sprite/tint to draw. Populated
 * in {@code extractRenderState} (off the render thread) and consumed in {@code submit}.
 */
public class TankRenderState extends BlockEntityRenderState {
    public boolean hasFluid;
    /** Where the fluid sits in the block and which of its faces to draw (see {@link FluidBody}). */
    public FluidBody body = FluidBody.EMPTY;

    @Nullable
    public TextureAtlasSprite sprite;

    public int tintColor = 0xFFFFFFFF;
}
