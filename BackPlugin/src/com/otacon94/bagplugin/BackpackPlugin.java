package com.otacon94.bagplugin;

import org.bukkit.plugin.java.JavaPlugin;

import com.otacon94.listeners.BackPackListener;


public class BackpackPlugin extends JavaPlugin {
	
	private static final String COMMAND1 = "backpack";
	private static final String COMMAND2 = "bp";
	
	private BackPackListener listener;
	
	@Override
	public void onEnable() {
		getLogger().info("OtaconBackPackPlugin is now Enabled");
		listener = new BackPackListener(this);
		getCommand(COMMAND1).setExecutor(listener);
		getCommand(COMMAND2).setExecutor(listener);
        getServer().getPluginManager().registerEvents(listener, this);
	}
	
	
	@Override
	public void onDisable() {
		getLogger().info("OtaconBackPackPlugin is now Disabled");
		listener.saveAllBackPacks();
	}
	
	


}
