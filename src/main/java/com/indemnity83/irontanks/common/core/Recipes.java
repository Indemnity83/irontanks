package com.indemnity83.irontanks.common.core;

import buildcraft.api.BCBlocks;
import buildcraft.lib.recipe.RecipeBuilderShaped;
import net.minecraft.item.ItemStack;

public class Recipes {
    public static void init() {
        if (Blocks.copperTank != null) {
            RecipeBuilderShaped builder = new RecipeBuilderShaped();
            builder.add(" g ");
            builder.add("gtg");
            builder.add("igi");
            builder.map('i', "ingotCopper");
            builder.map('g', "blockGlassColorless");
            builder.map('t', BCBlocks.FACTORY_TANK);
            builder.setResult(new ItemStack(Blocks.copperTank));
            builder.register();
            builder.registerRotated();
        }

        if (Blocks.ironTank != null) {
            RecipeBuilderShaped builder = new RecipeBuilderShaped();
            builder.add("igi");
            builder.add("gtg");
            builder.add("igi");
            builder.map('i', "ingotIron");
            builder.map('g', "blockGlassColorless");
            builder.map('t', BCBlocks.FACTORY_TANK);
            builder.setResult(new ItemStack(Blocks.ironTank));
            builder.register();
            builder.registerRotated();
        }

        if (Blocks.ironTank != null) {
            RecipeBuilderShaped builder = new RecipeBuilderShaped();
            builder.add(" g ");
            builder.add("gtg");
            builder.add("igi");
            builder.map('i', "ingotIron");
            builder.map('g', "blockGlassColorless");
            builder.map('t', Blocks.copperTank);
            builder.setResult(new ItemStack(Blocks.ironTank));
            builder.register();
            builder.registerRotated();
        }

        if (Blocks.silverTank != null) {
            RecipeBuilderShaped builder = new RecipeBuilderShaped();
            builder.add("igi");
            builder.add("gtg");
            builder.add("igi");
            builder.map('i', "ingotSilver");
            builder.map('g', "blockGlassColorless");
            builder.map('t', Blocks.copperTank);
            builder.setResult(new ItemStack(Blocks.silverTank));
            builder.register();
            builder.registerRotated();
        }

        if (Blocks.silverTank != null) {
            RecipeBuilderShaped builder = new RecipeBuilderShaped();
            builder.add(" g ");
            builder.add("gtg");
            builder.add("igi");
            builder.map('i', "ingotSilver");
            builder.map('g', "blockGlassColorless");
            builder.map('t', Blocks.ironTank);
            builder.setResult(new ItemStack(Blocks.silverTank));
            builder.register();
            builder.registerRotated();
        }

        if (Blocks.goldTank != null) {
            RecipeBuilderShaped builder = new RecipeBuilderShaped();
            builder.add("igi");
            builder.add("gtg");
            builder.add("igi");
            builder.map('i', "ingotGold");
            builder.map('g', "blockGlassColorless");
            builder.map('t', Blocks.ironTank);
            builder.setResult(new ItemStack(Blocks.goldTank));
            builder.register();
            builder.registerRotated();
        }

        if (Blocks.goldTank != null) {
            RecipeBuilderShaped builder = new RecipeBuilderShaped();
            builder.add(" g ");
            builder.add("gtg");
            builder.add("igi");
            builder.map('i', "ingotGold");
            builder.map('g', "blockGlassColorless");
            builder.map('t', Blocks.silverTank);
            builder.setResult(new ItemStack(Blocks.goldTank));
            builder.register();
            builder.registerRotated();
        }

        if (Blocks.diamondTank != null) {
            RecipeBuilderShaped builder = new RecipeBuilderShaped();
            builder.add(" g ");
            builder.add("gtg");
            builder.add("igi");
            builder.map('i', "gemDiamond");
            builder.map('g', "blockGlassColorless");
            builder.map('t', Blocks.goldTank);
            builder.setResult(new ItemStack(Blocks.diamondTank));
            builder.register();
            builder.registerRotated();
        }

        if (Blocks.emeraldTank != null) {
            RecipeBuilderShaped builder = new RecipeBuilderShaped();
            builder.add(" g ");
            builder.add("gtg");
            builder.add("igi");
            builder.map('i', "gemEmerald");
            builder.map('g', "blockGlassColorless");
            builder.map('t', Blocks.diamondTank);
            builder.setResult(new ItemStack(Blocks.emeraldTank));
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

        if (Blocks.voidTank != null) {
            RecipeBuilderShaped builder = new RecipeBuilderShaped();
            builder.add(" g ");
            builder.add("gtg");
            builder.add("bgr");
            builder.map('g', "blockGlassColorless");
            builder.map('t', BCBlocks.FACTORY_TANK);
            builder.map('b', "dyeBlack");
            builder.map('r', "dustRedstone");
            builder.setResult(new ItemStack(Blocks.voidTank));
            builder.register();
            builder.registerRotated();
        }

        if (Items.copperIronUpgrade != null) {
            RecipeBuilderShaped builder = new RecipeBuilderShaped();
            builder.add(" g ");
            builder.add("gsg");
            builder.add("tgt");
            builder.map('g', "paneGlassColorless");
            builder.map('s', "ingotCopper");
            builder.map('t', "ingotIron");
            builder.setResult(new ItemStack(Items.copperIronUpgrade));
            builder.register();
            builder.registerRotated();
        }

        if (Items.copperSilverUpgrade != null) {
            RecipeBuilderShaped builder = new RecipeBuilderShaped();
            builder.add("tgt");
            builder.add("gsg");
            builder.add("tgt");
            builder.map('g', "paneGlassColorless");
            builder.map('s', "ingotCopper");
            builder.map('t', "ingotSilver");
            builder.setResult(new ItemStack(Items.copperSilverUpgrade));
            builder.register();
            builder.registerRotated();
        }

        if (Items.diamondObsidianUpgrade != null) {
            RecipeBuilderShaped builder = new RecipeBuilderShaped();
            builder.add("ttt");
            builder.add("tst");
            builder.add("ttt");
            builder.map('s', "gemDiamond");
            builder.map('t', "blockObsidian");
            builder.setResult(new ItemStack(Items.diamondObsidianUpgrade));
            builder.register();
            builder.registerRotated();
        }

        if (Items.glassCopperUpgrade != null) {
            RecipeBuilderShaped builder = new RecipeBuilderShaped();
            builder.add("tgt");
            builder.add("g g");
            builder.add("tgt");
            builder.map('g', "paneGlassColorless");
            builder.map('t', "ingotCopper");
            builder.setResult(new ItemStack(Items.glassCopperUpgrade));
            builder.register();
            builder.registerRotated();
        }

        if (Items.glassIronUpgrade != null) {
            RecipeBuilderShaped builder = new RecipeBuilderShaped();
            builder.add("tgt");
            builder.add("g g");
            builder.add("tgt");
            builder.map('g', "paneGlassColorless");
            builder.map('t', "ingotIron");
            builder.setResult(new ItemStack(Items.glassIronUpgrade));
            builder.register();
            builder.registerRotated();
        }

        if (Items.goldDiamondUpgrade != null) {
            RecipeBuilderShaped builder = new RecipeBuilderShaped();
            builder.add(" g ");
            builder.add("gsg");
            builder.add("tgt");
            builder.map('g', "paneGlassColorless");
            builder.map('s', "ingotGold");
            builder.map('t', "gemDiamond");
            builder.setResult(new ItemStack(Items.goldDiamondUpgrade));
            builder.register();
            builder.registerRotated();
        }

        if (Items.diamondEmeraldUpgrade != null) {
            RecipeBuilderShaped builder = new RecipeBuilderShaped();
            builder.add(" g ");
            builder.add("gsg");
            builder.add("tgt");
            builder.map('g', "paneGlassColorless");
            builder.map('s', "gemDiamond");
            builder.map('t', "gemEmerald");
            builder.setResult(new ItemStack(Items.diamondEmeraldUpgrade));
            builder.register();
            builder.registerRotated();
        }

        if (Items.ironGoldUpgrade != null) {
            RecipeBuilderShaped builder = new RecipeBuilderShaped();
            builder.add("tgt");
            builder.add("gsg");
            builder.add("tgt");
            builder.map('g', "paneGlassColorless");
            builder.map('s', "ingotIron");
            builder.map('t', "ingotGold");
            builder.setResult(new ItemStack(Items.ironGoldUpgrade));
            builder.register();
            builder.registerRotated();
        }

        if (Items.silverGoldUpgrade != null) {
            RecipeBuilderShaped builder = new RecipeBuilderShaped();
            builder.add("tgt");
            builder.add("gsg");
            builder.add("tgt");
            builder.map('g', "paneGlassColorless");
            builder.map('s', "ingotSilver");
            builder.map('t', "ingotGold");
            builder.setResult(new ItemStack(Items.silverGoldUpgrade));
            builder.register();
            builder.registerRotated();
        }

        // --- Extended tiers: aluminium, stainless steel, titanium, tungsten steel ---
        // Tanks craft from the prior tank surrounded by glass and the new metal; the
        // metals are ore-dictionary gated so the tiers only craft in packs that add them.

        for (String aluminium : new String[] {"ingotAluminium", "ingotAluminum"}) {
            if (Blocks.aluminiumTank != null) {
                RecipeBuilderShaped builder = new RecipeBuilderShaped();
                builder.add(" g ");
                builder.add("gtg");
                builder.add("igi");
                builder.map('i', aluminium);
                builder.map('g', "blockGlassColorless");
                builder.map('t', Blocks.diamondTank);
                builder.setResult(new ItemStack(Blocks.aluminiumTank));
                builder.register();
                builder.registerRotated();
            }
        }

        if (Blocks.stainlessSteelTank != null) {
            RecipeBuilderShaped builder = new RecipeBuilderShaped();
            builder.add(" g ");
            builder.add("gtg");
            builder.add("igi");
            builder.map('i', "ingotStainlessSteel");
            builder.map('g', "blockGlassColorless");
            builder.map('t', Blocks.emeraldTank);
            builder.setResult(new ItemStack(Blocks.stainlessSteelTank));
            builder.register();
            builder.registerRotated();
        }

        if (Blocks.stainlessSteelTank != null) {
            RecipeBuilderShaped builder = new RecipeBuilderShaped();
            builder.add(" g ");
            builder.add("gtg");
            builder.add("igi");
            builder.map('i', "ingotStainlessSteel");
            builder.map('g', "blockGlassColorless");
            builder.map('t', Blocks.aluminiumTank);
            builder.setResult(new ItemStack(Blocks.stainlessSteelTank));
            builder.register();
            builder.registerRotated();
        }

        if (Blocks.titaniumTank != null) {
            RecipeBuilderShaped builder = new RecipeBuilderShaped();
            builder.add(" g ");
            builder.add("gtg");
            builder.add("igi");
            builder.map('i', "ingotTitanium");
            builder.map('g', "blockGlassColorless");
            builder.map('t', Blocks.stainlessSteelTank);
            builder.setResult(new ItemStack(Blocks.titaniumTank));
            builder.register();
            builder.registerRotated();
        }

        if (Blocks.tungstenSteelTank != null) {
            RecipeBuilderShaped builder = new RecipeBuilderShaped();
            builder.add(" g ");
            builder.add("gtg");
            builder.add("igi");
            builder.map('i', "ingotTungstenSteel");
            builder.map('g', "blockGlassColorless");
            builder.map('t', Blocks.titaniumTank);
            builder.setResult(new ItemStack(Blocks.tungstenSteelTank));
            builder.register();
            builder.registerRotated();
        }

        for (String aluminium : new String[] {"ingotAluminium", "ingotAluminum"}) {
            if (Items.diamondAluminiumUpgrade != null) {
                RecipeBuilderShaped builder = new RecipeBuilderShaped();
                builder.add(" g ");
                builder.add("gsg");
                builder.add("tgt");
                builder.map('g', "paneGlassColorless");
                builder.map('s', "gemDiamond");
                builder.map('t', aluminium);
                builder.setResult(new ItemStack(Items.diamondAluminiumUpgrade));
                builder.register();
                builder.registerRotated();
            }
        }

        if (Items.emeraldStainlessSteelUpgrade != null) {
            RecipeBuilderShaped builder = new RecipeBuilderShaped();
            builder.add(" g ");
            builder.add("gsg");
            builder.add("tgt");
            builder.map('g', "paneGlassColorless");
            builder.map('s', "gemEmerald");
            builder.map('t', "ingotStainlessSteel");
            builder.setResult(new ItemStack(Items.emeraldStainlessSteelUpgrade));
            builder.register();
            builder.registerRotated();
        }

        for (String aluminium : new String[] {"ingotAluminium", "ingotAluminum"}) {
            if (Items.aluminiumStainlessSteelUpgrade != null) {
                RecipeBuilderShaped builder = new RecipeBuilderShaped();
                builder.add(" g ");
                builder.add("gsg");
                builder.add("tgt");
                builder.map('g', "paneGlassColorless");
                builder.map('s', aluminium);
                builder.map('t', "ingotStainlessSteel");
                builder.setResult(new ItemStack(Items.aluminiumStainlessSteelUpgrade));
                builder.register();
                builder.registerRotated();
            }
        }

        if (Items.stainlessSteelTitaniumUpgrade != null) {
            RecipeBuilderShaped builder = new RecipeBuilderShaped();
            builder.add(" g ");
            builder.add("gsg");
            builder.add("tgt");
            builder.map('g', "paneGlassColorless");
            builder.map('s', "ingotStainlessSteel");
            builder.map('t', "ingotTitanium");
            builder.setResult(new ItemStack(Items.stainlessSteelTitaniumUpgrade));
            builder.register();
            builder.registerRotated();
        }

        if (Items.titaniumTungstenSteelUpgrade != null) {
            RecipeBuilderShaped builder = new RecipeBuilderShaped();
            builder.add(" g ");
            builder.add("gsg");
            builder.add("tgt");
            builder.map('g', "paneGlassColorless");
            builder.map('s', "ingotTitanium");
            builder.map('t', "ingotTungstenSteel");
            builder.setResult(new ItemStack(Items.titaniumTungstenSteelUpgrade));
            builder.register();
            builder.registerRotated();
        }
    }
}
