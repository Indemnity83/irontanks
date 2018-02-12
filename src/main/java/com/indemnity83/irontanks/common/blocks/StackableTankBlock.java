package com.indemnity83.irontanks.common.blocks;

import buildcraft.factory.block.ITankBlockConnector;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class StackableTankBlock extends TankBlock implements ITankBlockConnector {

    private static final IProperty<Boolean> JOINED_BELOW = PropertyBool.create("joined_below");

    public StackableTankBlock(String tankName, int tankCapacity) {
        super(tankName, tankCapacity);

        setDefaultState(this.blockState.getBaseState().withProperty(JOINED_BELOW, false));
    }

    public IBlockState getActualState(IBlockState blockState, IBlockAccess world, BlockPos pos) {
        boolean tankBelow = world.getBlockState(pos.down()).getBlock() instanceof ITankBlockConnector;
        return blockState.withProperty(JOINED_BELOW, tankBelow);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, JOINED_BELOW);
    }

    public int getMetaFromState(IBlockState blockState) {
        return 0;
    }

    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState();
    }
}
