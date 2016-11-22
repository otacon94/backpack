package com.otacon94.listeners;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.otacon94.utils.BackpackManager;
import com.otacon94.utils.MenuInventoryHolder;

public class BackpackListener implements CommandExecutor,Listener  {
	
	private JavaPlugin plugin ;
	
	private BackpackManager backpackManager ;
	
	public BackpackListener(JavaPlugin plugin) {
		this.plugin=plugin;
		backpackManager = new BackpackManager(plugin);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if( !(sender instanceof Player) ){
			sender.sendMessage("You must be a player to perform this command");
			return false;
		}
		Player player = (Player)sender;
		if( player.hasPermission("backplugin.use") ){
			backpackManager.openBackpack(player);
		}else{
			player.sendMessage("You don' have enough permission to perform this command");
		}
		return false;
	}
	
	public void reloadInventories(){
		for( Player p: plugin.getServer().getOnlinePlayers() ){
			backpackManager.loadBackpack(p);
		}
	}
	
	public void closeAllInventories(){
		for( Player p: plugin.getServer().getOnlinePlayers() ){
			p.updateInventory();
			p.closeInventory();
		}
	}
	

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerJoinEvent(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		backpackManager.loadBackpack(player);
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerQuit(PlayerJoinEvent event) {
		Player player = event.getPlayer();	
		backpackManager.saveBackpack(player);
	}
	
	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event) {
		if( event.getInventory().getHolder() instanceof MenuInventoryHolder ) {
			Player player = (Player) event.getPlayer();
			backpackManager.saveBackpackIfDifferent(player);
		}
	}
	
	
	
}
