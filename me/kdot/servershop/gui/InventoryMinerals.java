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

public class InventoryMinerals implements Listener {

	private Inventory inv;
	private InventoryShop prevInv;
	private Player player;
	private Main plugin;
	
	private String guiName = "Minerals";
	private String sectionName = "minerals";
	
	private NamespacedKey key = new NamespacedKey(Bukkit.getPluginManager().getPlugin("ServerShop"), "ServerShop");
	
	public InventoryMinerals(Main plugin, Player player, InventoryShop prevInv) {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		this.plugin = plugin;
		this.player = player;
		this.prevInv = prevInv;
	}
	
	public void openInv() {
		inv = Bukkit.createInventory(player, 45, guiName);
		
		//Top Row
		for(int i = 0; i <= 8; i++) {
			inv.setItem(i, guiItem(Material.BLACK_STAINED_GLASS_PANE, " ", "illegal-item", ""));
		}
		
		//Bottom Row
		for(int i = 36; i <= 44; i++) {
			if(i==40) continue;
			inv.setItem(i, guiItem(Material.BLACK_STAINED_GLASS_PANE, " ", "illegal-item", ""));
		}
		
		//Middle item section
		int iteration = 9; //starting slot
		for(String key : plugin.getShopData().getConfigurationSection(sectionName).getKeys(false)) {
			if(Material.getMaterial(key.toUpperCase()) == null && !player.hasPermission("servershop.admin")) {
				continue;
			}
			String loreString = ChatColor.GREEN + "Buy: " + ChatColor.GRAY + "$" + String.format("%,.2f", Double.parseDouble(plugin.getShopData().getString(sectionName + "." + key)));
			inv.setItem(iteration, guiItem(Material.getMaterial(key.toUpperCase()), "", key, loreString));
			iteration++;
			if(iteration > 43) break;
		}
		
		//fill any extra holes with gray glass
		for(int i = 9; i < 36; i++) {
			if(inv.getItem(i) == null) {
				inv.setItem(i, guiItem(Material.LIGHT_GRAY_STAINED_GLASS_PANE, " ", "illegal-item", ""));
			}
		}
		
		inv.setItem(40, guiItem(Material.BARRIER, "&c&lBack", "back", ""));
		
		player.openInventory(inv);
	}
	
	private ItemStack guiItem(Material material, String name, String basicName, String lore) {
		if(material == null) {
			material = Material.BEDROCK;
			ItemStack invalidItem = new ItemStack(material, 1);
			ItemMeta invalidMeta = invalidItem.getItemMeta();
			invalidMeta.getPersistentDataContainer().set(key, PersistentDataType.STRING, "invalid-item"); //So the item can be securely identified later
			invalidMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&4&lInvalid Item"));
			invalidMeta.setLore(Arrays.asList(ChatColor.translateAlternateColorCodes('&', "&cThe config contains an invalid item, &7" + basicName), 
					ChatColor.translateAlternateColorCodes('&', "&cPlease use standard minecraft item names"), 
					ChatColor.translateAlternateColorCodes('&', "&cThis item is invisible to normal players")));
			invalidItem.setItemMeta(invalidMeta);
			return invalidItem;
		}
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
		if(basicName == "back") {
			prevInv.openInv();
			return;
		}else if(basicName == "invalid-item") {
			return;
		}else if(basicName != "") {
			InventorySelector invselector = new InventorySelector(plugin, player, prevInv, basicName, sectionName);
			invselector.openInv();
		}
	}
	
}
