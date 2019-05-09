package com.indemnity83.irontank.utility;

import java.util.HashMap;

import com.indemnity83.irontank.init.ModBlocks;
import com.indemnity83.irontank.init.ModItems;
import net.minecraft.item.Item;
import net.minecraft.block.Block;

import cpw.mods.fml.common.event.FMLMissingMappingsEvent;

public class ItemMapHelper {
    private HashMap<String, Item> remapList = new HashMap();

    public ItemMapHelper() {
        remapList.put("irontank:ironGoldUpgrade", ModItems.ironGoldUpgrade);
        remapList.put("irontank:goldDiamondUpgrade", ModItems.goldDiamondUpgrade);
        remapList.put("irontank:silverDiamondUpgrade", ModItems.silverDiamondUpgrade);
        remapList.put("irontank:copperSilverUpgrade", ModItems.copperSilverUpgrade);
        remapList.put("irontank:silverGoldUpgrade", ModItems.silverGoldUpgrade);
        remapList.put("irontank:copperIronUpgrade", ModItems.copperIronUpgrade);
        remapList.put("irontank:glassIronUpgrade", ModItems.glassIronUpgrade);
        remapList.put("irontank:glassCopperUpgrade", ModItems.glassCopperUpgrade);
        remapList.put("irontank:diamondObsidianUpgrade", ModItems.diamondObsidianUpgrade);

        remapList.put("irontank:copperTank", Item.getItemFromBlock(ModBlocks.copperTank));
        remapList.put("irontank:ironTank", Item.getItemFromBlock(ModBlocks.ironTank));
        remapList.put("irontank:silverTank", Item.getItemFromBlock(ModBlocks.silverTank));
        remapList.put("irontank:goldTank", Item.getItemFromBlock(ModBlocks.goldTank));
        remapList.put("irontank:obsidianTank", Item.getItemFromBlock(ModBlocks.obsidianTank));
        remapList.put("irontank:diamondTank", Item.getItemFromBlock(ModBlocks.diamondTank));
    }

    public void handleMissingMaps(FMLMissingMappingsEvent event) {
        for (FMLMissingMappingsEvent.MissingMapping map : event.get()) {
            if (this.remapList.containsKey(map.name)) {
                switch (map.type) {
                    case BLOCK:
                        LogHelper.info("Remapping missing mapping for block " + map.name);
                        map.remap(Block.getBlockFromItem(remapList.get(map.name)));
                        break;
                    default:
                        LogHelper.info("Remapping missing mapping for item " + map.name);
                        map.remap(remapList.get(map.name));
                }
            }
        }
    }
}
