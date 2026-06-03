package com.indemnity83.irontanks.neoforge.content;

import com.indemnity83.irontanks.core.TankTier;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.jetbrains.annotations.Nullable;

/**
 * A tank block of a given {@link TankTier}. One {@code TankBlock} is registered per tier; they all
 * share a single {@link TankBlockEntity} type. Behavior lives in the block entity and {@code core}.
 */
public class TankBlock extends BaseEntityBlock {

    public static final MapCodec<TankBlock> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.STRING.xmap(TankTier::valueOf, TankTier::name).fieldOf("tier").forGetter(TankBlock::tier),
            propertiesCodec()
    ).apply(instance, TankBlock::new));

    /** True when a connecting tank sits directly below, so the seamless {@code side_stacked} texture is used. */
    public static final BooleanProperty JOINED_BELOW = BooleanProperty.create("joined_below");

    private final TankTier tier;

    public TankBlock(TankTier tier, Properties properties) {
        super(properties);
        this.tier = tier;
        registerDefaultState(defaultBlockState().setValue(JOINED_BELOW, false));
    }

    public TankTier tier() {
        return tier;
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(JOINED_BELOW);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState below = context.getLevel().getBlockState(context.getClickedPos().below());
        return defaultBlockState().setValue(JOINED_BELOW, joinsWithBelow(below));
    }

    @Override
    protected BlockState updateShape(
            BlockState state, LevelReader level, ScheduledTickAccess scheduledTickAccess, BlockPos pos,
            Direction direction, BlockPos neighborPos, BlockState neighborState, RandomSource random) {
        if (direction == Direction.DOWN) {
            state = state.setValue(JOINED_BELOW, joinsWithBelow(neighborState));
        }
        return super.updateShape(state, level, scheduledTickAccess, pos, direction, neighborPos, neighborState, random);
    }

    /** Mirrors the fluid-connection rule: tanks join unless either side is a creative tank. */
    private boolean joinsWithBelow(BlockState below) {
        return tier != TankTier.CREATIVE
                && below.getBlock() instanceof TankBlock belowTank
                && belowTank.tier() != TankTier.CREATIVE;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TankBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide() ? null
                : createTickerHelper(type, IronTanksContent.TANK_BLOCK_ENTITY, TankBlockEntity::serverTick);
    }
}
