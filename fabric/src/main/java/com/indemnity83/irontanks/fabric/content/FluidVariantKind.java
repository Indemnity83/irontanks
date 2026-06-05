package com.indemnity83.irontanks.fabric.content;

import com.indemnity83.irontanks.core.FluidKind;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes;
import net.minecraft.core.component.DataComponents;

/** Adapts Fabric's {@link FluidVariant} to the {@code core} {@link FluidKind} seam. */
public final class FluidVariantKind implements FluidKind<FluidVariant> {

    public static final FluidVariantKind INSTANCE = new FluidVariantKind();

    private FluidVariantKind() {}

    @Override
    public FluidVariant empty() {
        return FluidVariant.blank();
    }

    @Override
    public boolean isEmpty(FluidVariant fluid) {
        return fluid.isBlank();
    }

    @Override
    public boolean isGas(FluidVariant fluid) {
        return !fluid.isBlank() && FluidVariantAttributes.isLighterThanAir(fluid);
    }

    @Override
    public boolean isPotion(FluidVariant fluid) {
        return !fluid.isBlank() && fluid.get(DataComponents.POTION_CONTENTS) != null;
    }
}
