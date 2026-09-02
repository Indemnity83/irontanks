package com.indemnity83.irontanks.neoforge.compat;

import com.indemnity83.irontanks.neoforge.IronTanksNeoForge;
import com.indemnity83.irontanks.neoforge.content.TankBlock;
import com.indemnity83.irontanks.neoforge.content.TankBlockEntity;
import com.indemnity83.irontanks.neoforge.content.TankReadout;
import net.minecraft.resources.Identifier;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;
import snownee.jade.api.config.IPluginConfig;

/**
 * Optional Jade integration: shows a tank's contents on the look-at HUD, the same line the empty-hand
 * readout puts on the action bar. Compile-only against Jade's API — this class is only loaded when Jade
 * is installed, so the mod runs fine without it.
 */
@WailaPlugin(IronTanksNeoForge.MOD_ID)
public class JadeTankPlugin implements IWailaPlugin, IBlockComponentProvider {

    private static final Identifier UID = Identifier.fromNamespaceAndPath(IronTanksNeoForge.MOD_ID, "tank");

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(this, TankBlock.class);
    }

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        // Only what the fluid API hides: Jade's built-in fluid bar already shows plain fluids and their
        // amount, so adding our line there would just duplicate it. A stored potion and a mixed column
        // both report themselves empty to that API (no bar), so our line is all that identifies them.
        if (accessor.getBlockEntity() instanceof TankBlockEntity tank && TankReadout.hiddenFromFluidApi(tank)) {
            tooltip.add(TankReadout.describe(tank));
        }
    }

    @Override
    public Identifier getUid() {
        return UID;
    }
}
