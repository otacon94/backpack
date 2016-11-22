package com.otacon94.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class MenuInventoryHolder implements InventoryHolder {
	
	private Player p;
	
	public MenuInventoryHolder(Player player) {
		this.p=player;
	}

	/* 
	 * Never called
	 */
	@Override
	public Inventory getInventory() {
		return Bukkit.createInventory(p, 9);
	}

}
