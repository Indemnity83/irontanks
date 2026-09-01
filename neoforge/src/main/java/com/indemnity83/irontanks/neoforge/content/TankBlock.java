package com.indemnity83.irontanks.neoforge.content;

import com.indemnity83.irontanks.core.TankTier;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
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
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.fluid.FluidUtil;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jetbrains.annotations.Nullable;

/**
 * A tank block of a given {@link TankTier}. One {@code TankBlock} is registered per tier; they all
 * share a single {@link TankBlockEntity} type. Behavior lives in the block entity and {@code core}.
 */
public class TankBlock extends BaseEntityBlock {

    public static final MapCodec<TankBlock> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                    Codec.STRING
                            .xmap(TankTier::valueOf, TankTier::name)
                            .fieldOf("tier")
                            .forGetter(TankBlock::tier),
                    propertiesCodec())
            .apply(instance, TankBlock::new));

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

    /**
     * Right-click to move fluid between the tank and the held item: a bucket (or any fluid container)
     * fills/drains a full bucket; a potion bottle deposits its contents and an empty glass bottle draws
     * a bottle (1/3 bucket) back out. Potions are stored as water carrying a {@code potion_contents}
     * component, so water bottles merge with real water and only water can be bottled back.
     */
    @Override
    protected InteractionResult useItemOn(
            ItemStack stack,
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            BlockHitResult hit) {
        if (level.getBlockEntity(pos) instanceof TankBlockEntity tank) {
            if (stack.is(Items.POTION) || stack.is(Items.GLASS_BOTTLE)) {
                return bottleInteraction(stack, level, pos, player, hand, tank)
                        ? InteractionResult.SUCCESS
                        : InteractionResult.PASS;
            }
            if (FluidUtil.interactWithFluidHandler(player, hand, level, pos, hit.getDirection())) {
                return InteractionResult.SUCCESS;
            }
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hit);
    }

    /** Empty-hand right-click reads the tank's contents out to the action bar. */
    @Override
    protected InteractionResult useWithoutItem(
            BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!level.isClientSide() && level.getBlockEntity(pos) instanceof TankBlockEntity tank) {
            player.sendOverlayMessage(TankReadout.describe(tank));
        }
        return InteractionResult.SUCCESS;
    }

    /**
     * Deposits a potion (held {@link Items#POTION}) into the tank or draws one out into a held
     * {@link Items#GLASS_BOTTLE}. Returns whether a full bottle's worth actually transferred — on the
     * client this is a non-committing dry run so the result matches the server without mutating state.
     */
    private static boolean bottleInteraction(
            ItemStack stack, Level level, BlockPos pos, Player player, InteractionHand hand, TankBlockEntity tank) {
        boolean commit = !level.isClientSide();
        TankFluidHandler handler = new TankFluidHandler(tank);

        if (stack.is(Items.POTION)) {
            PotionContents contents = stack.get(DataComponents.POTION_CONTENTS);
            if (contents == null) {
                return false; // not a real potion; let vanilla handle it (e.g. drinking)
            }
            FluidResource resource = isPlainWater(contents)
                    ? FluidResource.of(Fluids.WATER)
                    : FluidResource.of(Fluids.WATER).with(DataComponents.POTION_CONTENTS, contents);
            try (Transaction tx = Transaction.openRoot()) {
                if (!handler.depositBottle(resource, tx)) {
                    return false;
                }
                if (!commit) {
                    return true;
                }
                tx.commit();
            }
            level.playSound(null, pos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
            consumeAndReturn(player, hand, stack, new ItemStack(Items.GLASS_BOTTLE));
            return true;
        }

        // Empty glass bottle: only water can be bottled (a non-water fluid leaves the bottle empty).
        // currentFluid() (not the pipe-facing getResource) so a sealed potion is still bottle-drawable.
        FluidResource current = handler.currentFluid();
        if (current.isEmpty() || current.getFluid() != Fluids.WATER) {
            return false;
        }
        try (Transaction tx = Transaction.openRoot()) {
            if (!handler.extractBottle(current, tx)) {
                return false;
            }
            if (!commit) {
                return true;
            }
            tx.commit();
        }
        level.playSound(null, pos, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
        consumeAndReturn(player, hand, stack, bottleFor(current));
        return true;
    }

    /** A water bottle with no effects, custom color, or name — stored as plain water so it stacks with it. */
    private static boolean isPlainWater(PotionContents contents) {
        return contents.is(Potions.WATER)
                && contents.customEffects().isEmpty()
                && contents.customColor().isEmpty()
                && contents.customName().isEmpty();
    }

    /** The filled bottle a water column hands back: the stored potion, or a plain water bottle. */
    private static ItemStack bottleFor(FluidResource water) {
        PotionContents contents = water.get(DataComponents.POTION_CONTENTS);
        if (contents == null) {
            return PotionContents.createItemStack(Items.POTION, Potions.WATER);
        }
        ItemStack bottle = new ItemStack(Items.POTION);
        bottle.set(DataComponents.POTION_CONTENTS, contents);
        return bottle;
    }

    /** Consume one of the held item and give back the result, mirroring vanilla bottle handling. */
    private static void consumeAndReturn(Player player, InteractionHand hand, ItemStack held, ItemStack result) {
        if (player.getAbilities().instabuild) {
            return; // creative: don't consume the held item or spawn duplicates
        }
        held.shrink(1);
        if (held.isEmpty()) {
            player.setItemInHand(hand, result);
        } else if (!player.getInventory().add(result)) {
            player.drop(result, false);
        }
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState below =
                context.getLevel().getBlockState(context.getClickedPos().below());
        return defaultBlockState().setValue(JOINED_BELOW, joinsWithBelow(below));
    }

    @Override
    protected BlockState updateShape(
            BlockState state,
            LevelReader level,
            ScheduledTickAccess scheduledTickAccess,
            BlockPos pos,
            Direction direction,
            BlockPos neighborPos,
            BlockState neighborState,
            RandomSource random) {
        if (direction == Direction.DOWN) {
            state = state.setValue(JOINED_BELOW, joinsWithBelow(neighborState));
        }
        return super.updateShape(state, level, scheduledTickAccess, pos, direction, neighborPos, neighborState, random);
    }

    /** Mirrors the fluid-connection rule ({@link TankTier#joinsColumn()}): only column members join. */
    private boolean joinsWithBelow(BlockState below) {
        return tier.joinsColumn()
                && below.getBlock() instanceof TankBlock belowTank
                && belowTank.tier().joinsColumn();
    }

    /** Hide the shared top/bottom face between two connecting tanks so a vertical stack looks seamless. */
    @Override
    protected boolean skipRendering(BlockState state, BlockState neighborState, Direction direction) {
        if (direction.getAxis() == Direction.Axis.Y
                && tier.joinsColumn()
                && neighborState.getBlock() instanceof TankBlock neighbor
                && neighbor.tier().joinsColumn()) {
            return true;
        }
        return super.skipRendering(state, neighborState, direction);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TankBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide()
                ? null
                : createTickerHelper(type, IronTanksContent.TANK_BLOCK_ENTITY, TankBlockEntity::serverTick);
    }
}
