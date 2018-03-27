package com.indemnity83.irontanks.common.core;

import buildcraft.api.BCBlocks;
import buildcraft.lib.recipe.RecipeBuilderShaped;
import net.minecraft.item.ItemStack;

public class Recipes {
    public static void init() {
        if (Blocks.copperTank != null) {
            RecipeBuilderShaped builder = new RecipeBuilderShaped();
            builder.add("ggg");
            builder.add("ctc");
            builder.add("ggg");
            builder.map('c', "ingotCopper");
            builder.map('g', "blockGlassColorless");
            builder.map('t', BCBlocks.FACTORY_TANK);
            builder.setResult(new ItemStack(Blocks.copperTank));
            builder.register();
            builder.registerRotated();
        }

        if (Blocks.copperTank != null) {
            RecipeBuilderShaped builder = new RecipeBuilderShaped();
            builder.add("ggg");
            builder.add("iti");
            builder.add("ggg");
            builder.map('i', "ingotCopper");
            builder.map('g', "blockGlassColorless");
            builder.map('t', BCBlocks.FACTORY_TANK);
            builder.setResult(new ItemStack(Blocks.copperTank));
            builder.register();
            builder.registerRotated();
        }

        if (Blocks.ironTank != null) {
            RecipeBuilderShaped builder = new RecipeBuilderShaped();
            builder.add("gig");
            builder.add("iti");
            builder.add("gig");
            builder.map('i', "ingotIron");
            builder.map('g', "blockGlassColorless");
            builder.map('t', BCBlocks.FACTORY_TANK);
            builder.setResult(new ItemStack(Blocks.ironTank));
            builder.register();
            builder.registerRotated();
        }

        if (Blocks.silverTank != null) {
            RecipeBuilderShaped builder = new RecipeBuilderShaped();
            builder.add("gsg");
            builder.add("sts");
            builder.add("gsg");
            builder.map('s', "ingotSilver");
            builder.map('g', "blockGlassColorless");
            builder.map('t', Blocks.silverTank);
            builder.setResult(new ItemStack(Blocks.silverTank));
            builder.register();
            builder.registerRotated();
        }

        if (Blocks.silverTank != null) {
            RecipeBuilderShaped builder = new RecipeBuilderShaped();
            builder.add("ggg");
            builder.add("tgt");
            builder.add("ggg");
            builder.map('s', "ingotSilver");
            builder.map('g', "blockGlassColorless");
            builder.map('t', Blocks.ironTank);
            builder.setResult(new ItemStack(Blocks.silverTank));
            builder.register();
            builder.registerRotated();
        }

        if (Blocks.goldTank != null) {
            RecipeBuilderShaped builder = new RecipeBuilderShaped();
            builder.add("ggg");
            builder.add("iti");
            builder.add("ggg");
            builder.map('i', "ingotGold");
            builder.map('g', "blockGlassColorless");
            builder.map('t', Blocks.ironTank);
            builder.setResult(new ItemStack(Blocks.goldTank));
            builder.register();
            builder.registerRotated();
        }

        if (Blocks.goldTank != null) {
            RecipeBuilderShaped builder = new RecipeBuilderShaped();
            builder.add("gig");
            builder.add("iti");
            builder.add("gig");
            builder.map('i', "ingotGold");
            builder.map('g', "blockGlassColorless");
            builder.map('t', Blocks.silverTank);
            builder.setResult(new ItemStack(Blocks.goldTank));
            builder.register();
            builder.registerRotated();
        }

        if (Blocks.diamondTank != null) {
            RecipeBuilderShaped builder = new RecipeBuilderShaped();
            builder.add("ggg");
            builder.add("gtg");
            builder.add("ddd");
            builder.map('g', "blockGlassColorless");
            builder.map('d', "gemDiamond");
            builder.map('t', Blocks.ironTank);
            builder.setResult(new ItemStack(Blocks.diamondTank));
            builder.register();
            builder.registerRotated();
        }

        if (Blocks.diamondTank != null) {
            RecipeBuilderShaped builder = new RecipeBuilderShaped();
            builder.add("ggg");
            builder.add("dtd");
            builder.add("ggg");
            builder.map('g', "blockGlassColorless");
            builder.map('d', "gemDiamond");
            builder.map('t', Blocks.goldTank);
            builder.setResult(new ItemStack(Blocks.diamondTank));
            builder.register();
            builder.registerRotated();
        }

        if (Blocks.obsidianTank != null) {
            RecipeBuilderShaped builder = new RecipeBuilderShaped();
            builder.add("ooo");
            builder.add("oto");
            builder.add("ooo");
            builder.map('o', "blockObsidian");
            builder.map('t', Blocks.diamondTank);
            builder.setResult(new ItemStack(Blocks.obsidianTank));
            builder.register();
            builder.registerRotated();
        }

        if (Items.copperIronUpgrade != null) {
            RecipeBuilderShaped builder = new RecipeBuilderShaped();
            builder.add("ggg");
            builder.add("tst");
            builder.add("ggg");
            builder.map('g', "blockGlassColorless");
            builder.map('s', "ingotCopper");
            builder.map('t', "ingotIron");
            builder.setResult(new ItemStack(Items.copperIronUpgrade));
            builder.register();
            builder.registerRotated();
        }

        if (Items.copperSilverUpgrade != null) {
            RecipeBuilderShaped builder = new RecipeBuilderShaped();
            builder.add("ggg");
            builder.add("tst");
            builder.add("ggg");
            builder.map('g', "blockGlassColorless");
            builder.map('s', "ingotCopper");
            builder.map('t', "ingotSilver");
            builder.setResult(new ItemStack(Items.copperSilverUpgrade));
            builder.register();
            builder.registerRotated();
        }

        if (Items.diamondObsidianUpgrade != null) {
            RecipeBuilderShaped builder = new RecipeBuilderShaped();
            builder.add("ggg");
            builder.add("tst");
            builder.add("ggg");
            builder.map('g', "blockGlassColorless");
            builder.map('s', "gemDiamond");
            builder.map('t', "blockObsidian");
            builder.setResult(new ItemStack(Items.diamondObsidianUpgrade));
            builder.register();
            builder.registerRotated();
        }

        if (Items.glassCopperUpgrade != null) {
            RecipeBuilderShaped builder = new RecipeBuilderShaped();
            builder.add("ggg");
            builder.add("tgt");
            builder.add("ggg");
            builder.map('g', "blockGlassColorless");
            builder.map('t', "ingotCopper");
            builder.setResult(new ItemStack(Items.glassCopperUpgrade));
            builder.register();
            builder.registerRotated();
        }

        if (Items.glassIronUpgrade != null) {
            RecipeBuilderShaped builder = new RecipeBuilderShaped();
            builder.add("ggg");
            builder.add("tgt");
            builder.add("ggg");
            builder.map('g', "blockGlassColorless");
            builder.map('t', "ingotIron");
            builder.setResult(new ItemStack(Items.glassIronUpgrade));
            builder.register();
            builder.registerRotated();
        }

        if (Items.goldDiamondUpgrade != null) {
            RecipeBuilderShaped builder = new RecipeBuilderShaped();
            builder.add("ggg");
            builder.add("tst");
            builder.add("ggg");
            builder.map('g', "blockGlassColorless");
            builder.map('s', "ingotGold");
            builder.map('t', "gemDiamond");
            builder.setResult(new ItemStack(Items.glassIronUpgrade));
            builder.register();
            builder.registerRotated();
        }

        if (Items.ironGoldUpgrade != null) {
            RecipeBuilderShaped builder = new RecipeBuilderShaped();
            builder.add("ggg");
            builder.add("tst");
            builder.add("ggg");
            builder.map('g', "blockGlassColorless");
            builder.map('s', "ingotIron");
            builder.map('t', "ingotGold");
            builder.setResult(new ItemStack(Items.ironGoldUpgrade));
            builder.register();
            builder.registerRotated();
        }

        if (Items.silverDiamondUpgrade != null) {
            RecipeBuilderShaped builder = new RecipeBuilderShaped();
            builder.add("ggg");
            builder.add("tst");
            builder.add("ggg");
            builder.map('g', "blockGlassColorless");
            builder.map('s', "ingotSilver");
            builder.map('t', "gemDiamond");
            builder.setResult(new ItemStack(Items.silverDiamondUpgrade));
            builder.register();
            builder.registerRotated();
        }

        if (Items.silverGoldUpgrade != null) {
            RecipeBuilderShaped builder = new RecipeBuilderShaped();
            builder.add("ggg");
            builder.add("tst");
            builder.add("ggg");
            builder.map('g', "blockGlassColorless");
            builder.map('s', "ingotSilver");
            builder.map('t', "ingotGold");
            builder.setResult(new ItemStack(Items.silverGoldUpgrade));
            builder.register();
            builder.registerRotated();
        }
    }
}
