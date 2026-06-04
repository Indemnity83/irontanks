package com.indemnity83.irontanks.fabric.content;

import com.indemnity83.irontanks.core.TankTier;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.Level;

/**
 * Builds a one-line, human-readable description of a tank column's contents — the placed tank shows
 * nothing on its own, so this powers both the empty-hand readout and the Jade HUD. Reports the whole
 * vertical column (the logical tank), and for a stored potion shows the effect line (amplifier +
 * duration) so otherwise-identical sub-variants — Strength I vs II vs extended — read distinctly.
 */
public final class TankReadout {

    private TankReadout() {}

    public static Component describe(TankBlockEntity tank) {
        long total = 0;
        long capacity = 0;
        FluidVariant fluid = FluidVariant.blank();
        for (TankBlockEntity t : tank.columnTanks()) {
            total += t.amount();
            capacity += t.capacity();
            if (fluid.isBlank() && !t.fluidVariant().isBlank()) {
                fluid = t.fluidVariant();
            }
        }
        if (fluid.isBlank() || total <= 0) {
            return Component.translatable("irontanks.readout.empty");
        }

        return Component.translatable(
                "irontanks.readout.line",
                labelFor(fluid, tank.getLevel()),
                Component.translatable(
                        "irontanks.readout.amount",
                        total / TankTier.DROPLETS_PER_MB,
                        capacity / TankTier.DROPLETS_PER_MB));
    }

    /**
     * Whether the tank column holds a potion (water carrying a {@code potion_contents} component). Jade's
     * native fluid bar already shows plain fluids, so the HUD adds our line only for potions — which are
     * sealed from the fluid API and so would otherwise be invisible there.
     */
    public static boolean isPotion(TankBlockEntity tank) {
        for (TankBlockEntity t : tank.columnTanks()) {
            FluidVariant fluid = t.fluidVariant();
            if (!fluid.isBlank() && fluid.get(DataComponents.POTION_CONTENTS) != null) {
                return true;
            }
        }
        return false;
    }

    /** The fluid's display name, or a potion's effect line when it carries potion contents. */
    private static Component labelFor(FluidVariant fluid, Level level) {
        PotionContents potion = fluid.get(DataComponents.POTION_CONTENTS);
        return potion != null ? potionLabel(potion, level) : FluidVariantAttributes.getName(fluid);
    }

    /** The potion's effect line ("Strength II (3:00)"), or its base name when it has no effects. */
    private static Component potionLabel(PotionContents potion, Level level) {
        if (potion.hasEffects()) {
            List<Component> lines = new ArrayList<>();
            float tickRate = level != null ? level.tickRateManager().tickrate() : 20.0F;
            PotionContents.addPotionTooltip(potion.getAllEffects(), lines::add, 1.0F, tickRate);
            if (!lines.isEmpty()) {
                return lines.getFirst();
            }
        }
        return potion.getName("item.minecraft.potion.effect.");
    }
}
