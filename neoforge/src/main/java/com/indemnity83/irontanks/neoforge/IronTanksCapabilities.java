package com.indemnity83.irontanks.neoforge;

import com.indemnity83.irontanks.neoforge.content.IronTanksContent;
import com.indemnity83.irontanks.neoforge.content.TankFluidHandler;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

/**
 * Exposes every tank's fluid storage to NeoForge's transfer API so pipes and pumps can fill and drain
 * it. The handler delegates capacity/clamp math to {@code core} (see {@link TankFluidHandler}).
 */
public final class IronTanksCapabilities {

    private IronTanksCapabilities() {
    }

    public static void register(IEventBus modBus) {
        modBus.addListener(IronTanksCapabilities::registerCapabilities);
    }

    private static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.Fluid.BLOCK,
                IronTanksContent.TANK_BLOCK_ENTITY,
                (tank, side) -> new TankFluidHandler(tank));
    }
}
