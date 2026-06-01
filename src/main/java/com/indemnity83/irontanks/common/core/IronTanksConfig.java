package com.indemnity83.irontanks.common.core;

import net.minecraftforge.common.config.Configuration;

import java.io.File;

public final class IronTanksConfig {
    public static boolean creativeTankBreakable = false;

    private IronTanksConfig() {
    }

    public static void load(File configFile) {
        Configuration config = new Configuration(configFile);

        creativeTankBreakable = config.getBoolean(
                "creativeTankBreakable",
                Configuration.CATEGORY_GENERAL,
                false,
                "Allow the Creative Tank to be broken and harvested like any other tank.\n"
                        + "Leave this false (the default) to keep Creative Tanks unbreakable so they can\n"
                        + "be used for fixed, admin-built setups on servers and in modpacks."
        );

        if (config.hasChanged()) {
            config.save();
        }
    }
}
