package com.indemnity83.irontanks.common.core;

import com.indemnity83.irontanks.IronTanks;
import com.indemnity83.irontanks.common.blocks.CreativeTankBlock;
import com.indemnity83.irontanks.common.blocks.StackableTankBlock;
import com.indemnity83.irontanks.common.blocks.VoidTankBlock;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

public class Blocks {
    // BuildCraft's tank module — when present its tank is the canonical base, so we soft-suppress ours.
    public static final String BUILDCRAFT_FACTORY = "buildcraftfactory";
    // Shared OreDictionary tag so recipes accept either our glass tank or BuildCraft's tank.
    public static final String TANK_GLASS_ORE = "tankGlass";

    @GameRegistry.ObjectHolder(IronTanks.MODID + ":glass_tank")
    public static StackableTankBlock glassTank;

    @GameRegistry.ObjectHolder(IronTanks.MODID + ":iron_tank")
    public static StackableTankBlock ironTank;

    @GameRegistry.ObjectHolder(IronTanks.MODID + ":gold_tank")
    public static StackableTankBlock goldTank;

    @GameRegistry.ObjectHolder(IronTanks.MODID + ":diamond_tank")
    public static StackableTankBlock diamondTank;

    @GameRegistry.ObjectHolder(IronTanks.MODID + ":obsidian_tank")
    public static StackableTankBlock obsidianTank;

    @GameRegistry.ObjectHolder(IronTanks.MODID + ":silver_tank")
    public static StackableTankBlock silverTank;

    @GameRegistry.ObjectHolder(IronTanks.MODID + ":copper_tank")
    public static StackableTankBlock copperTank;

    @GameRegistry.ObjectHolder(IronTanks.MODID + ":emerald_tank")
    public static StackableTankBlock emeraldTank;

    @GameRegistry.ObjectHolder(IronTanks.MODID + ":void_tank")
    public static VoidTankBlock voidTank;

    @GameRegistry.ObjectHolder(IronTanks.MODID + ":creative_tank")
    public static CreativeTankBlock creativeTank;

    public static void init() {
        obsidianTank.setResistance(6000);

        if (!IronTanksConfig.creativeTankBreakable) {
            creativeTank.setBlockUnbreakable();
        }

        registerOreDictionary();

        // When BuildCraft is installed, its tank is the canonical base tier. Hide ours from the creative
        // tab (its crafting recipe is suppressed via a recipe condition) so the two don't visibly duplicate.
        if (Loader.isModLoaded(BUILDCRAFT_FACTORY)) {
            glassTank.setCreativeTab(null);
        }
    }

    /**
     * Unify the base "glass" tank across mods so recipes accept either tank. Registered in init (the
     * block-item is resolvable by then); Forge's ore ingredients late-bind, so recipes still pick it up.
     */
    private static void registerOreDictionary() {
        OreDictionary.registerOre(TANK_GLASS_ORE, new ItemStack(glassTank));

        // BuildCraft doesn't OreDict its own tank; add it by registry name (no hard dependency).
        Block buildCraftTank = Block.getBlockFromName("buildcraftfactory:tank");
        if (buildCraftTank != null) {
            OreDictionary.registerOre(TANK_GLASS_ORE, new ItemStack(buildCraftTank));
        }
    }

    @SideOnly(Side.CLIENT)
    public static void initModels() {
        glassTank.initModel();
        ironTank.initModel();
        goldTank.initModel();
        diamondTank.initModel();
        obsidianTank.initModel();
        copperTank.initModel();
        emeraldTank.initModel();
        silverTank.initModel();
        voidTank.initModel();
        creativeTank.initModel();
    }
}
