package com.indemnity83.irontank.item;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

import com.indemnity83.irontank.block.BlockExtendedTank;

/**
 * ItemBlock for the tanks that shows the tank's fluid capacity in its tooltip.
 */
public class ItemBlockExtendedTank extends ItemBlock {

	public ItemBlockExtendedTank(Block block) {
		super(block);
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean advanced) {
		if (this.field_150939_a instanceof BlockExtendedTank) {
			BlockExtendedTank tank = (BlockExtendedTank) this.field_150939_a;
			list.add(StatCollector.translateToLocalFormatted("irontank.tooltip.capacity", tank.type.capacity));
		}
	}

}
