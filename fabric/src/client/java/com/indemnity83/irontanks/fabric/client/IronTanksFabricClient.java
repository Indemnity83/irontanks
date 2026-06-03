package com.indemnity83.irontanks.fabric.client;

import com.indemnity83.irontanks.fabric.content.IronTanksContent;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;

/**
 * Client-only setup: registers the tank block-entity renderer so tanks show their fluid level.
 */
public final class IronTanksFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        BlockEntityRenderers.register(IronTanksContent.TANK_BLOCK_ENTITY, TankBlockEntityRenderer::new);
    }
}
