package com.indemnity83.irontanks.core;

import java.util.List;

/**
 * The in-place tank upgrade paths, as loader-agnostic data. Each value maps a source tier to the tier
 * it promotes to; the loader builds an upgrade item per entry and swaps the placed block while
 * preserving contents. Mirrors the 1.12 upgrade-item set.
 *
 * <p>Note there are two glass upgrades (to copper and to iron) and two routes into gold (from iron and
 * from silver), so this is a graph rather than a single chain.
 */
public enum TankUpgrade {
    GLASS_COPPER(TankTier.GLASS, TankTier.COPPER),
    GLASS_IRON(TankTier.GLASS, TankTier.IRON),
    COPPER_IRON(TankTier.COPPER, TankTier.IRON),
    COPPER_SILVER(TankTier.COPPER, TankTier.SILVER),
    IRON_GOLD(TankTier.IRON, TankTier.GOLD),
    SILVER_GOLD(TankTier.SILVER, TankTier.GOLD),
    GOLD_DIAMOND(TankTier.GOLD, TankTier.DIAMOND),
    DIAMOND_OBSIDIAN(TankTier.DIAMOND, TankTier.OBSIDIAN),
    DIAMOND_EMERALD(TankTier.DIAMOND, TankTier.EMERALD),
    DIAMOND_ALUMINIUM(TankTier.DIAMOND, TankTier.ALUMINIUM),
    EMERALD_STAINLESSSTEEL(TankTier.EMERALD, TankTier.STAINLESSSTEEL),
    ALUMINIUM_STAINLESSSTEEL(TankTier.ALUMINIUM, TankTier.STAINLESSSTEEL),
    STAINLESSSTEEL_TITANIUM(TankTier.STAINLESSSTEEL, TankTier.TITANIUM),
    TITANIUM_TUNGSTENSTEEL(TankTier.TITANIUM, TankTier.TUNGSTENSTEEL);

    private final TankTier from;
    private final TankTier to;

    TankUpgrade(TankTier from, TankTier to) {
        this.from = from;
        this.to = to;
    }

    public TankTier from() {
        return from;
    }

    public TankTier to() {
        return to;
    }

    /** All upgrade paths starting from the given tier. */
    public static List<TankUpgrade> from(TankTier tier) {
        return java.util.Arrays.stream(values()).filter(u -> u.from == tier).toList();
    }
}
