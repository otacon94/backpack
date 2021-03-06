package com.otacon94.backplugin;

import org.apache.logging.log4j.core.net.Priority;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.otacon94.listeners.BackpackListener;


public class BackpackPlugin extends JavaPlugin {
	
	private static final String COMMAND1 = "backpack";
	private static final String COMMAND2 = "bp";
	
	private BackpackListener listener;
	
	@Override
	public void onEnable() {
		getLogger().info("OtaconBackPackPlugin is now Enabled");
		listener = new BackpackListener(this);
		listener.reloadInventories();
		getCommand(COMMAND1).setExecutor(listener);
		getCommand(COMMAND2).setExecutor(listener);
        getServer().getPluginManager().registerEvents(listener, this);
	}
	
	
	@Override
	public void onDisable() {
		getLogger().info("OtaconBackPackPlugin is now Disabled");
		listener.closeAllInventories();
	}
	
	
}
