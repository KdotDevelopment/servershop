package me.kdot.servershop.gui;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import me.kdot.servershop.Main;
import net.md_5.bungee.api.ChatColor;

public class InventoryShop implements Listener {
	
	private Inventory inv;
	private Player player;
	private Main plugin;
	
	private String guiName = "Server Item Shop";
	
	private NamespacedKey key = new NamespacedKey(Bukkit.getPluginManager().getPlugin("ServerShop"), "ServerShop");
	
	public InventoryShop(Main plugin, Player player) {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		this.plugin = plugin;
		this.player = player;
	}
	
	//Building Blocks ; Farming Items ; Minerals ; Mobs ; Misc.
	
	public void openInv() {
		inv = Bukkit.createInventory(player, 9, guiName);
		
		inv.setItem(2, guiItem(Material.BRICKS, "&eBuilding Blocks", "building-blocks", ""));
		inv.setItem(3, guiItem(Material.CARROT, "&eFarming/Foods", "farming-foods", ""));
		inv.setItem(4, guiItem(Material.GOLD_INGOT, "&eMinerals", "minerals", ""));
		inv.setItem(5, guiItem(Material.BONE, "&eMob Items", "mobs", ""));
		inv.setItem(6, guiItem(Material.BOOK, "&eMisc. Items", "misc", ""));
		
		//fill any extra holes with gray glass
		for(int i = 0; i < 9; i++) {
			if(inv.getItem(i) == null) {
				inv.setItem(i, guiItem(Material.LIGHT_GRAY_STAINED_GLASS_PANE, " ", "illegal-item", ""));
			}
		}
		
		player.openInventory(inv);
	}
	
	private ItemStack guiItem(Material material, String name, String basicName, String lore) {
		ItemStack item = new ItemStack(material, 1);
	    ItemMeta meta = item.getItemMeta();
	    meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, basicName); //So the item can be securely identified later
	    meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
	    if(lore != "") { //So it doesnt have that annoying little space under the title
	    	meta.setLore(Arrays.asList(ChatColor.translateAlternateColorCodes('&', lore)));
	    }
	    item.setItemMeta(meta);
	    return item;
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		if(e.getCurrentItem() == null) return;
		if(e.getClickedInventory().getType() == InventoryType.PLAYER) return;
		if(!(e.getInventory() == inv)) return;
		if(!e.getView().getTitle().equals(guiName)) return;
		e.setCancelled(true);
		handleItemClick(e.getCurrentItem().getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING));
	}
	
	private void handleItemClick(String basicName) {
		if(basicName.equals("building-blocks")) {
			InventoryBuildingBlocks gui = new InventoryBuildingBlocks(plugin, player, this);
			gui.openInv();
		}
		if(basicName.equals("farming-foods")) {
			InventoryFarmingFoods gui = new InventoryFarmingFoods(plugin, player, this);
			gui.openInv();
		}
		if(basicName.equals("minerals")) {
			InventoryMinerals gui = new InventoryMinerals(plugin, player, this);
			gui.openInv();
		}
		if(basicName.equals("mobs")) {
			InventoryMobs gui = new InventoryMobs(plugin, player, this);
			gui.openInv();
		}
		if(basicName.equals("misc")) {
			InventoryMisc gui = new InventoryMisc(plugin, player, this);
			gui.openInv();
		}
		
	}
	
}
