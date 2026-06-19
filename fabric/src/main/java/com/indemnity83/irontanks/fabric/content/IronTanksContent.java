package com.indemnity83.irontanks.fabric.content;

import com.indemnity83.irontanks.core.TankTier;
import com.indemnity83.irontanks.core.TankUpgrade;
import com.indemnity83.irontanks.fabric.IronTanksFabric;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;

/**
 * Registers all Iron Tanks content: one {@link TankBlock} (plus its block item) per {@link TankTier},
 * a single shared {@link TankBlockEntity} type covering every tank block, and a creative tab. Called
 * once from the Fabric {@code ModInitializer}.
 */
public final class IronTanksContent {

    private IronTanksContent() {}

    public static final Map<TankTier, Block> TANK_BLOCKS = new EnumMap<>(TankTier.class);
    public static BlockEntityType<TankBlockEntity> TANK_BLOCK_ENTITY;

    private static final List<Item> TAB_ITEMS = new ArrayList<>();

    public static void register() {
        for (TankTier tier : TankTier.values()) {
            registerTank(tier);
        }
        for (TankUpgrade upgrade : TankUpgrade.values()) {
            registerUpgrade(upgrade);
        }

        TANK_BLOCK_ENTITY = Registry.register(
                BuiltInRegistries.BLOCK_ENTITY_TYPE,
                id("tank"),
                FabricBlockEntityTypeBuilder.create(
                                TankBlockEntity::new, TANK_BLOCKS.values().toArray(new Block[0]))
                        .build());

        registerCreativeTab();
    }

    private static void registerTank(TankTier tier) {
        String name = tier.name().toLowerCase(Locale.ROOT) + "_tank";

        ResourceKey<Block> blockKey = ResourceKey.create(Registries.BLOCK, id(name));
        BlockBehaviour.Properties properties = BlockBehaviour.Properties.of()
                .setId(blockKey)
                .strength(tier.hardness(), tier.blastResistance())
                .sound(SoundType.METAL)
                .noOcclusion()
                .requiresCorrectToolForDrops();
        TankBlock block = new TankBlock(tier, properties);
        Registry.register(BuiltInRegistries.BLOCK, blockKey, block);
        TANK_BLOCKS.put(tier, block);

        ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, id(name));
        BlockItem item =
                new TankBlockItem(block, new Item.Properties().setId(itemKey).useBlockDescriptionPrefix());
        Registry.register(BuiltInRegistries.ITEM, itemKey, item);
        TAB_ITEMS.add(item);
    }

    private static void registerUpgrade(TankUpgrade upgrade) {
        String name = upgrade.name().toLowerCase(Locale.ROOT) + "_upgrade";
        ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, id(name));
        UpgradeItem item = new UpgradeItem(upgrade, new Item.Properties().setId(itemKey));
        Registry.register(BuiltInRegistries.ITEM, itemKey, item);
        TAB_ITEMS.add(item);
    }

    private static void registerCreativeTab() {
        CreativeModeTab tab = CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
                .title(Component.translatable("itemGroup." + IronTanksFabric.MOD_ID + ".tanks"))
                .icon(() -> new ItemStack(TANK_BLOCKS.get(TankTier.GLASS)))
                .displayItems((parameters, output) -> TAB_ITEMS.forEach(output::accept))
                .build();
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, id("tanks"), tab);
    }

    private static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(IronTanksFabric.MOD_ID, path);
    }
}
