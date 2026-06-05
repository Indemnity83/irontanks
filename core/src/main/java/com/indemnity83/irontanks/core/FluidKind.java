package com.indemnity83.irontanks.core;

/**
 * The handful of facts {@link TankColumn} needs about a loader's fluid type {@code F} without {@code
 * core} touching any Minecraft type. Each loader supplies one implementation over its own fluid stack
 * (NeoForge {@code FluidResource}, Fabric {@code FluidVariant}); equality is left to {@code F}'s own
 * {@code equals}, so there is no {@code equal} method here.
 */
public interface FluidKind<F> {

    /** The "no fluid" value for {@code F} (NeoForge {@code FluidResource.EMPTY} / Fabric blank variant). */
    F empty();

    /** Whether {@code fluid} is the empty value. */
    boolean isEmpty(F fluid);

    /** Whether {@code fluid} is lighter than air, so it settles toward the top of a column. */
    boolean isGas(F fluid);

    /** Whether {@code fluid} is a stored potion (water carrying a {@code potion_contents} component). */
    boolean isPotion(F fluid);
}
