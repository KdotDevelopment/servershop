package me.kdot.servershop.gui;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import me.kdot.servershop.Main;
import me.kdot.servershop.util.ItemManager;
import net.md_5.bungee.api.ChatColor;

public class InventorySell implements Listener {

	private Inventory inv;
	private Player player;
	private Main plugin;
	
	private String guiName = "Sell Items";
	private double totalCost = 0; //total cost of items
	private int amount = 0; //total amount of items
	
	private NamespacedKey key = new NamespacedKey(Bukkit.getPluginManager().getPlugin("ServerShop"), "ServerShop");
	
	public InventorySell(Main plugin, Player player) {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		this.plugin = plugin;
		this.player = player;
	}
	
	public void openInv() {
		inv = Bukkit.createInventory(player, 45, guiName);
		
		for(int i = 36; i < 45; i++) {
			if(i==39 || i==41) continue;
			inv.setItem(i, guiItem(Material.BLACK_STAINED_GLASS_PANE, " ", "illegal-item", ""));
		}
		
		inv.setItem(39, guiItem(Material.BARRIER, "&c&lCancel", "cancel", ""));
		inv.setItem(41, guiItem(Material.EMERALD_BLOCK, "&a&lConfirm Sell", "sell", ChatColor.GRAY + "" + amount + " items: " + "$" + String.format("%,.2f",totalCost)));
		
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
		if(!(e.getInventory() == inv)) return;
		if(!e.getView().getTitle().equals(guiName)) return;
		if(e.getSlot() < 36) { //When player uses open portion of inventory
			Bukkit.getScheduler().runTask(plugin, new Runnable() {
			    @Override
			    public void run() {
			    	updateAmounts();
			    }
			});
			
			return;
		}
		if(e.getClickedInventory() == null) return;
		if(e.getClickedInventory().getType() == InventoryType.PLAYER) return;
		if(e.getCurrentItem() == null) return;
		handleItemClick(e.getCurrentItem().getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING));
		e.setCancelled(true);
		return;
	}
	
	@EventHandler
	public void onInventoryMoveItem(InventoryMoveItemEvent e) {
		if(e.getDestination() == null) return;
		if(!(e.getDestination() == inv || e.getSource() == inv)) return;
		//if(!e.getView().getTitle().equals(guiName)) return;
		Bukkit.getScheduler().runTask(plugin, new Runnable() {
		    @Override
		    public void run() {
		    	updateAmounts();
		    }
		});
		return;
	}
	
	
	@EventHandler
	public void onInventoryClose(InventoryCloseEvent e) { //Put items back in players inventory after closing
		if(e.getInventory().getType() == InventoryType.PLAYER) return;
		if(!(e.getInventory() == inv)) return;
		if(!e.getView().getTitle().equals(guiName)) return;
		
		ItemStack[] itemList = e.getInventory().getContents();
		
		for(ItemStack item : itemList) {
			if(item == null) continue;
			if(item.getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING) == null) { //make sure to only use items that you should give back to player (not barrier or glass)
				player.getInventory().addItem(item);
			}
		}
		
	}
	
	private void handleItemClick(String basicName) {
		if(basicName == "cancel") {
			player.closeInventory();
		}
		if(basicName == "sell") {
			ItemManager manager = new ItemManager(plugin);
			manager.sellInvItems(player, inv);
		}
	}
	
	private void updateAmounts() {
		amount = 0;
		totalCost = 0.0;
		for(int i = 0; i < 36; i++) {
			if(inv.getItem(i) == null) continue; //Make sure there is item in the slot
			ItemStack item = inv.getItem(i);
			if(plugin.getSellData().getDouble(item.getType().toString().toLowerCase()) != 0.0) { //Make sure item is a sellable item
				double itemCost = plugin.getSellData().getDouble(item.getType().toString().toLowerCase());
				totalCost += itemCost * item.getAmount();
				amount += item.getAmount();
			}
		}
		inv.setItem(41, guiItem(Material.EMERALD_BLOCK, "&a&lConfirm Sell", "sell", ChatColor.GRAY + "" + amount + " items: " + "$" + String.format("%,.2f",totalCost)));
	}
	
}
