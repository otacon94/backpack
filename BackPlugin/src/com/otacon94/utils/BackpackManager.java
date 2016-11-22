package com.otacon94.utils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class BackpackManager {

	private static final String INVENTORYPATH = "/inventories/";
	private static final String INVENTORYKEY = "inventory";

	private static final String PACKSIZEPATH = "size";
	
	private static final String BACKNAMEKEY = "BackPack name";
	private static final String BACKPACKSIZEKEY = "BackPack size";

	private static final String DEFAULTBACKPACKNAME = "Backpack";
	private static final int DEFAULTBACKPACKSIZE = 9;
	

	private String backpackName = DEFAULTBACKPACKNAME;
	private int backpackSize = DEFAULTBACKPACKSIZE;
	
	private HashMap<Player, Inventory> backpacks = new HashMap<Player, Inventory>();
	
	private JavaPlugin plugin;
	
	public BackpackManager(JavaPlugin p) {
		plugin=p;
		checkConfigFile();
	}
	
	/**
	 * Creates the inventory file for the given player
	 * @param p - the player you want to create the inventory file
	 */
	public void createInventoryFile(Player p){
		File f = getPlayerInventoryFile(p);
		try {
			f.getParentFile().mkdirs();
			f.createNewFile();
		} catch (Exception e) {
			p.sendMessage("Error creating your inventory file");
			e.printStackTrace();
		}
	}
	
	/**
	 * It checks if the config file exists or not. In case of negative response it provides to create a new config file,
	 * otherwise it provides to get the specified backpack's name and size.
	 */
	private void checkConfigFile(){
		try {
			if (!plugin.getDataFolder().exists()) {
				plugin.getDataFolder().mkdirs();
			}
			File file = getConfigFile();
			if (!file.exists()) {
				file.createNewFile();
				createConfigFile();
			} else {
				backpackName = (String)plugin.getConfig().get(BACKNAMEKEY);
				backpackSize = (Integer)plugin.getConfig().get(BACKPACKSIZEKEY);
			}
		} catch (Exception e) {
			e.printStackTrace();

		}
	}

	/**
	 * Opens the backpack of the given player. If the player doesn't have a backpack loaded tries to load it.
	 * @param p - the player you want to open the backpack
	 */
	public void openBackpack(Player p){
		Inventory inventory = backpacks.get(p);
		if(inventory==null)
			loadBackpack(p);
		p.openInventory(inventory);
	}
	
	
	/**
	 * Saves the backpack of the given player only is the currentversion is different from the saved one
	 * @param p - the holder of the backpack
	 */
	public void saveBackpackIfDifferent(Player p){
		Inventory database = getPlayerSavedBackpack(p);
		Inventory inventory = backpacks.get(p);
		if(database!=null && !database.equals(inventory)){
			saveBackpack(p);
		}
	}
	
	/**
	 * Saves all backpacks into respectives files
	 */
	public void saveAllBackpacks(){
		for( Player p: plugin.getServer().getOnlinePlayers() ){
			saveBackpack(p);
		}
	}
	
	/**
	 * It creates a new inventory for the given player with the config specified parameter.
	 * @param p - the holder of the backpack
	 * @return a new inventory with the size and the title specified in config gile
	 */
	public Inventory createPlayerInventory(Player p){
		Inventory inventory = Bukkit.createInventory(new MenuInventoryHolder(p), backpackSize, backpackName);
		return inventory;
	}
	
	/**
	 * Loads in the backpacks collection the given player's backpack. It checks first for the 
	 * inventory file existence and in case of positive answer, the procedure will load the backpack 
	 * and this one is put into the backpacks collection. It also makes check for the size of the loaded inventory:
	 * if it's bigger than the size specified in the config file, the backpack's contente will be dropped in the
	 * player location to avoid to lose something.
	 * @param p - the holder of the backpack you want to load
	 */
	public void loadBackpack(Player p){
		try {
			File inventoryFile = getPlayerInventoryFile(p);
			if( !inventoryFile.exists() ){
				createInventoryFile(p);
				backpacks.put(p, createInventory(p));
				return;
			}
			FileConfiguration ymlConfig = YamlConfiguration.loadConfiguration(inventoryFile);
			int size = ymlConfig.getInt(PACKSIZEPATH);
			if( size>backpackSize ){
				dropBackpack(p);
			}
			Inventory inventory = getInventory(backpackSize,p,ymlConfig);
			backpacks.put(p, inventory);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Saves the holder's backpack. It translates the Inventory to a HashMap<String,Object> with the key representing
	 * the value's index.
	 * @param p - the holder of the backpack you want to save
	 */
	public void saveBackpack(Player p){
		try {
			File inventoryFile = getPlayerInventoryFile(p);
			Inventory inventory = backpacks.get(p);
			if( inventory==null ){
				inventory = createPlayerInventory(p);
			}
			FileConfiguration inventoryConfig = YamlConfiguration.loadConfiguration(inventoryFile);
			inventoryConfig.set(PACKSIZEPATH, inventory.getContents().length);
			saveInventory(inventory,inventoryConfig);
			inventoryConfig.save(inventoryFile);
		} catch (Exception e) {
			p.sendMessage("Error while saving your Backpack");
			e.printStackTrace();
		}
	}
	
	/**
	 * Drops all the backpack in the location of the player. It's used to avoid losing items after a downgrade of
	 * of the size of the backpack
	 * @param p - the holder's backpack
	 */
	private void dropBackpack(Player p){
		Inventory inventory = getPlayerSavedBackpack(p);
		for(int i=0;i<inventory.getContents().length;i++){
			if( inventory.getItem(i)!=null ){
				p.getWorld().dropItem(p.getLocation(), inventory.getItem(i));
			}
		}
	}
	
	/**
	 * Returns an inventory with the given size, with the given player as holder and built from the given file.
	 * @param size - the size of the inventory
	 * @param p - the holder of the backpack
	 * @param ymlConfig - the file from which to load the backpack
	 * @return the inventory with specified size and holder from the file
	 */
	private Inventory getInventory(int size,Player p,FileConfiguration ymlConfig){
		Inventory inv = createInventory(p,size);
		for(int i=0;i<size;i++){
			inv.setItem(i, ymlConfig.getItemStack(i+""));
		}
		return inv;
	}
	
	/**
	 * Saved the inventory in the given file. The inventory is saved in this format: ItemIndex, ItemStack
	 * @param inv - the inventory to save
	 * @param inventoryConfig - the file in which to save 
	 */
	private void saveInventory(Inventory inv, FileConfiguration inventoryConfig){
		for(int i=0;i<inv.getSize();i++){
			inventoryConfig.set(i+"", inv.getItem(i));
		}
	}
	
	/**
	 * Returns the saved backpack of the given player. First the method get the inventory size, then
	 * loads an inventory with the given size calling {@link #getInventory(int,Player,FileConfiguration)}
	 * and returns it.
	 * @param p - the holder of the backpack
	 * @return the saved backpack of the player
	 */
	public Inventory getPlayerSavedBackpack(Player p){
		try {
			File inventoryFile = getPlayerInventoryFile(p);
			YamlConfiguration ymlConfig = new YamlConfiguration();
			ymlConfig.load(inventoryFile);
			int size = ymlConfig.getInt(PACKSIZEPATH);
			Inventory inventory = getInventory(size,p,ymlConfig);
			return inventory;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	/**
	 * Return the inventory file relative to the player.
	 * @param p - the Inventory's holder
	 * @return the inventory file for the player
	 */
	private File getPlayerInventoryFile(Player p){
		return new File(plugin.getDataFolder()+INVENTORYPATH, p.getName()+".yml");
	}
	
	/**
	 * Returns the config file
	 * @return the config file
	 */
	private File getConfigFile(){
		File f = new File(plugin.getDataFolder()+"/config.yml");
		return f;
	}
	
	/**
	 * Creates a canonical configfile
	 */
	private void createConfigFile(){
		plugin.getConfig().set(BACKNAMEKEY, DEFAULTBACKPACKNAME);
		plugin.getConfig().set(BACKPACKSIZEKEY, DEFAULTBACKPACKSIZE);
		plugin.saveConfig();
	}
	
	/**
	 * Creates a new inventory for the player with the size specified in the config file.
	 * @param player - the inventory holder
	 * @return a new Inventory for the given Holder with size and title specified by config
	 */
	private Inventory createInventory(Player player){
		return createInventory(player, backpackSize);
	}
	
	/**
	 * Creates an inventory for the player with the given size
	 * @param player - the inventory holder
	 * @param dim - the dimension of the inventory
	 * @return a new Inventory for the given Holder and size. The title is specified in config file
	 */
	private Inventory createInventory(Player player, int dim){
		return Bukkit.createInventory(new MenuInventoryHolder(player), dim, backpackName);
	}

}
