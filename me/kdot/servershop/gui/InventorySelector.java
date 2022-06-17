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
import me.kdot.servershop.util.ItemManager;
import net.md_5.bungee.api.ChatColor;

public class InventorySelector implements Listener {
	private Inventory inv;
	private InventoryShop prevInv;
	private Player player;
	private Main plugin;
	
	private String guiName = "Amount Selector";
	
	//Variables needed to identify the item and its info
	private String basicName;
	private String sectionName;
	
	private int amount = 0;
	
	private NamespacedKey key = new NamespacedKey(Bukkit.getPluginManager().getPlugin("ServerShop"), "ServerShop");
	
	public InventorySelector(Main plugin, Player player, InventoryShop prevInv, String basicName, String sectionName) {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		this.plugin = plugin;
		this.prevInv = prevInv;
		this.player = player;
		this.basicName = basicName;
		this.sectionName = sectionName;
	}
	
	public void openInv() {
		inv = Bukkit.createInventory(player, 54, guiName);
		
		//Top Row
		for(int i = 0; i <= 8; i++) {
			if(i==4) continue;
			inv.setItem(i, guiItem(Material.BLACK_STAINED_GLASS_PANE, " ", "illegal-item", ""));
		}
		
		//Bottom Row
		for(int i = 45; i <= 53; i++) {
			if(i==48 || i==50) continue;
			inv.setItem(i, guiItem(Material.BLACK_STAINED_GLASS_PANE, " ", "illegal-item", ""));
		}
		
		inv.setItem(11, guiItem(Material.LIME_STAINED_GLASS_PANE, "&aAdd 1", "+1", ""));
		inv.setItem(12, guiItem(Material.LIME_STAINED_GLASS_PANE, "&aAdd 8", "+8", ""));
		inv.setItem(13, guiItem(Material.LIME_STAINED_GLASS_PANE, "&aAdd 32", "+32", ""));
		inv.setItem(14, guiItem(Material.LIME_STAINED_GLASS_PANE, "&aAdd 64", "+64", ""));
		inv.setItem(15, guiItem(Material.LIME_STAINED_GLASS_PANE, "&aAdd 256", "+256", ""));
		
		inv.setItem(29, guiItem(Material.RED_STAINED_GLASS_PANE, "&cRemove 1", "-1", ""));
		inv.setItem(30, guiItem(Material.RED_STAINED_GLASS_PANE, "&cRemove 8", "-8", ""));
		inv.setItem(31, guiItem(Material.RED_STAINED_GLASS_PANE, "&cRemove 32", "-32", ""));
		inv.setItem(32, guiItem(Material.RED_STAINED_GLASS_PANE, "&cRemove 64", "-64", ""));
		inv.setItem(33, guiItem(Material.RED_STAINED_GLASS_PANE, "&cRemove 256", "-256", ""));
		
		inv.setItem(4, guiItem(Material.getMaterial(basicName.toUpperCase()), "", "illegal-item", ChatColor.GRAY + "$" + String.format("%,.2f",Double.parseDouble(plugin.getShopData().getString(sectionName + "." + basicName)))));
		inv.setItem(48, guiItem(Material.BARRIER, "&c&lBack", "back", ""));
		inv.setItem(50, guiItem(Material.EMERALD_BLOCK, "&a&lConfirm Purchase", "purchase", ChatColor.GRAY + "" + amount + " items: " + "$" + String.format("%,.2f",amount*Double.parseDouble(plugin.getShopData().getString(sectionName + "." + basicName)))));
		
		//fill any extra holes with gray glass
		for(int i = 9; i <= 44; i++) {
			if(inv.getItem(i) == null) {
				inv.setItem(i, guiItem(Material.LIGHT_GRAY_STAINED_GLASS_PANE, " ", "illegal-item", ""));
			}
		}
		
		player.openInventory(inv);
	}
	
	private ItemStack guiItem(Material material, String name, String basicName, String lore) {
		if(material == null) material = Material.BEDROCK;
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
		if((e.getAction().toString().contains("PICKUP"))) {
			handleItemClick(e.getCurrentItem().getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING));
		}
		e.setCancelled(true);
		return;
	}
	
	private void handleItemClick(String basicName) {
		if(basicName == "back") {
			prevInv.openInv();
		}else if(basicName.contains("+")) {
			if(basicName == "+1") {
				amount += 1;
			}
			if(basicName == "+8") {
				amount += 8;
			}
			if(basicName == "+32") {
				amount += 32;
			}
			if(basicName == "+64") {
				amount += 64;
			}
			if(basicName == "+256") {
				amount += 256;
			}
			if(amount > 1024) {
				amount = 1024;
			}
			inv.setItem(50, guiItem(Material.EMERALD_BLOCK, "&a&lConfirm Purchase", "purchase", ChatColor.GRAY + "" + amount + " items: " + "$" + String.format("%,.2f",amount*Double.parseDouble(plugin.getShopData().getString(sectionName + "." + this.basicName)))));
			player.updateInventory();
		}else if(basicName.contains("-")) {
			if(basicName == "-1") {
				amount -= 1;
			}
			if(basicName == "-8") {
				amount -= 8;
			}
			if(basicName == "-32") {
				amount -= 32;
			}
			if(basicName == "-64") {
				amount -= 64;
			}
			if(basicName == "-256") {
				amount -= 256;
			}
			if(amount < 0) {
				amount = 0;
			}
			inv.setItem(50, guiItem(Material.EMERALD_BLOCK, "&a&lConfirm Purchase", "purchase", ChatColor.GRAY + "" + amount + " items: " + "$" + String.format("%,.2f",amount*Double.parseDouble(plugin.getShopData().getString(sectionName + "." + this.basicName)))));
			player.updateInventory();
		}else if(basicName == "purchase") {
			ItemManager manager = new ItemManager(plugin);
			manager.giveShopItem(player, amount, this.basicName, sectionName);
		}
	}
}
