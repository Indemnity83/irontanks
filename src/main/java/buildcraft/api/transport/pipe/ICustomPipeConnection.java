/** Copyright (c) 2011-2017, SpaceToad and the BuildCraft Team http://www.mod-buildcraft.com
 * <p/>
 * BuildCraft is distributed under the terms of the Minecraft Mod Public License 1.0, or MMPL. Please check the contents
 * of the license located in http://www.mod-buildcraft.com/MMPL-1.0.txt */
package buildcraft.api.transport.pipe;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface ICustomPipeConnection {
    /** @return How long the connecting pipe should extend for, in addition to its normal 4/16f connection. Values less
     *         than or equal to <code>-4 / 16.0f</code> indicate that the pipe will not connect at all, and will render
     *         as it it was not connected. */
    float getExtension(World world, BlockPos pos, EnumFacing face, IBlockState state);
}
