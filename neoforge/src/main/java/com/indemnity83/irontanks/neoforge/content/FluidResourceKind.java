package com.indemnity83.irontanks.neoforge.content;

import com.indemnity83.irontanks.core.FluidKind;
import net.minecraft.core.component.DataComponents;
import net.neoforged.neoforge.transfer.fluid.FluidResource;

/** Adapts NeoForge's {@link FluidResource} to the {@code core} {@link FluidKind} seam. */
public final class FluidResourceKind implements FluidKind<FluidResource> {

    public static final FluidResourceKind INSTANCE = new FluidResourceKind();

    private FluidResourceKind() {}

    @Override
    public FluidResource empty() {
        return FluidResource.EMPTY;
    }

    @Override
    public boolean isEmpty(FluidResource fluid) {
        return fluid.isEmpty();
    }

    @Override
    public boolean isGas(FluidResource fluid) {
        return !fluid.isEmpty() && fluid.value().getFluidType().isLighterThanAir();
    }

    @Override
    public boolean isPotion(FluidResource fluid) {
        return !fluid.isEmpty() && fluid.get(DataComponents.POTION_CONTENTS) != null;
    }
}
