package com.indemnity83.irontanks.fabric.content;

import com.indemnity83.irontanks.core.TankTier;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

/** Block item for tanks that shows the tier's capacity (and a note for void/creative tanks) on hover. */
public class TankBlockItem extends BlockItem {

    public TankBlockItem(TankBlock block, Properties properties) {
        super(block, properties);
    }

    @Override
    public void appendHoverText(
            ItemStack stack,
            Item.TooltipContext context,
            TooltipDisplay tooltipDisplay,
            Consumer<Component> consumer,
            TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltipDisplay, consumer, flag);
        TankTier tier = ((TankBlock) getBlock()).tier();
        consumer.accept(Component.translatable("irontanks.tooltip.capacity", tier.buckets())
                .withStyle(ChatFormatting.GRAY));
        if (tier == TankTier.VOID) {
            consumer.accept(
                    Component.translatable("irontanks.tooltip.void_tank").withStyle(ChatFormatting.GRAY));
        } else if (tier == TankTier.CREATIVE) {
            consumer.accept(
                    Component.translatable("irontanks.tooltip.creative_tank").withStyle(ChatFormatting.GRAY));
        }
    }
}
