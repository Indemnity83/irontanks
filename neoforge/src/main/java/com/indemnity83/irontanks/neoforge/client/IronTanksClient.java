package com.indemnity83.irontanks.neoforge.client;

import com.indemnity83.irontanks.neoforge.content.IronTanksContent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

/**
 * Client-only setup: registers the {@link TankBlockEntityRenderer} so tanks show their fluid level.
 * Loaded only on the client (guarded in the mod entry point).
 */
public final class IronTanksClient {

    private IronTanksClient() {
    }

    public static void register(IEventBus modBus) {
        modBus.addListener(IronTanksClient::registerRenderers);
    }

    private static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(IronTanksContent.TANK_BLOCK_ENTITY, TankBlockEntityRenderer::new);
    }
}
