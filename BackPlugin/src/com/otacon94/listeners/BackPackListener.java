package com.otacon94.listeners;

import java.io.File;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;

import com.otacon94.backplugin.BackpackPlugin;

public class BackPackListener implements CommandExecutor,Listener  {
	
	private HashMap<Player,Inventory> backpacks = new HashMap<Player,Inventory>();
	
	private BackpackPlugin plugin;
	
	public BackPackListener(BackpackPlugin pack){
		this.plugin=pack;
		createConfig();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("You must be a  Player to use this command!");
			return false;
		}
		Player player = (Player) sender;
		if( player.hasPermission("backplugin.use") ){
			Inventory inv = backpacks.get(player);
			player.openInventory(inv);
			return true;
		}
		return false;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerJoinEvent(PlayerJoinEvent event) {
		Player player = event.getPlayer();
//		plugin.getLogger().info(player.getName()+" has connected!");
		File config = new File(plugin.getDataFolder(), "config.yml");
		if (!config.exists()) {
			plugin.saveDefaultConfig();
		} else {
			if(!event.getPlayer().hasPlayedBefore()) {
				Inventory inv = createInventory(player);
				plugin.getConfig().addDefault(player.getName(), inv);
//				plugin.getLogger().info(player.getName()+" has no inventory, creating one!");
			}else{
				Inventory inv = (Inventory)plugin.getConfig().get(player.getName());
				if( inv==null ){
					inv = createInventory(player);
				}
				backpacks.put(player, inv);
//				plugin.getLogger().info(player.getName()+"'s inventory loading");
			}
		}
		
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerQuit(PlayerJoinEvent event) {
		Player player = event.getPlayer();	
//		plugin.getLogger().info(player.getName()+" is leaving, saving back");
		plugin.getConfig().set(player.getName(), backpacks.get(player));
		plugin.saveConfig();
	}
	
	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event) {
		if(event.getInventory().getType() == InventoryType.CHEST) {
			Player player = (Player) event.getPlayer();
			Inventory database = (Inventory)plugin.getConfig().get(player.getName());
			Inventory inventory = backpacks.get(player);
			if(database!=null && !database.equals(inventory)){
				plugin.getConfig().set(player.getName(), inventory);
			}else{
				plugin.getConfig().set(player.getName(), inventory);
			}
			plugin.saveConfig();
		}
	}
	
	private void createConfig() {
	    try {
	        if (!plugin.getDataFolder().exists()) {
	        	plugin.getDataFolder().mkdirs();
	        }
	        File file = new File(plugin.getDataFolder(), "config.yml");
	        if (!file.exists()) {
//	        	plugin. getLogger().info("Config.yml not found, creating!");
	        	file.createNewFile();
	        } else {
//	        	plugin.getLogger().info("Config.yml found, loading!");
	        }
	    } catch (Exception e) {
	        e.printStackTrace();

	    }

	}
	
	private Inventory createInventory(Player p){
		return Bukkit.createInventory(p, 9, "Backpack");
	}
	
	public void saveAllBackPacks(){
		for(Player p: backpacks.keySet()){
			plugin.getConfig().set(p.getName(), backpacks.get(p));
		}
		plugin.saveConfig();
	}

}
