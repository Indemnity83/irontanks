package com.indemnity83.irontank.handler;

import java.io.File;

import com.indemnity83.irontank.reference.Reference;

import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

import net.minecraftforge.common.config.Configuration;

public class ConfigurationHandler 
{
	public static Configuration configuration;
	public static boolean testValue = false;
	
	public static void init(File configFile) 
	{
		if( configuration == null ) 
		{
			configuration = new Configuration(configFile);
			loadConfiguration();
		}
	}
	
	@SubscribeEvent
	public void onConfigurationChangedEvent(ConfigChangedEvent.OnConfigChangedEvent event)
	{
		if (event.modID.equalsIgnoreCase(Reference.MODID)) 
		{
			 loadConfiguration();
		}
	}
	
	public static void loadConfiguration()
	{
		testValue = configuration.getBoolean(Configuration.CATEGORY_GENERAL, "testConfig", true, "This is an example config value");
		
		if (configuration.hasChanged())
		{
			configuration.save();
		}
	}
}
