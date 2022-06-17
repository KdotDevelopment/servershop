package me.kdot.servershop.util;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.kdot.servershop.Main;
import net.md_5.bungee.api.ChatColor;

public class ItemManager {
	
	private Main plugin;
	
	public ItemManager(Main plugin) {
		this.plugin = plugin;
	}
	
	public void giveShopItem(Player player, int amount, String basicName, String sectionName) {
		double price = Double.parseDouble(plugin.getShopData().getString(sectionName + "." + basicName));
		if(price != 0.0) {
			if(!isInvFull(player)) {
				if(amount <= 0) return;
				if(playerHasBalance(player, price*amount)) {
					ItemStack item = new ItemStack(Material.getMaterial(basicName.toUpperCase()));
					item.setAmount(amount);
					plugin.getEcon().withdrawPlayer(player, price*amount);
					player.closeInventory();
					player.getInventory().addItem(item);
					shopReceipt(player, price*amount, amount, basicName);
				}else {
					player.closeInventory();
					player.sendMessage(ChatColor.RED + "[Server Shop] Not enough balance!");
				}
			}else {
				player.closeInventory();
				player.sendMessage(ChatColor.RED + "[Server Shop] Error! Not enough inventory space!");
			}
		}
	}
	
	//Only do slots 0-35
	public void sellInvItems(Player player, Inventory inv) {
		double totalCost = 0.0;
		int amount = 0;
		for(int i = 0; i < 36; i++) {
			if(inv.getItem(i) == null) continue; //Make sure there is item in the slot
			ItemStack item = inv.getItem(i);
			if(plugin.getSellData().getDouble(item.getType().toString().toLowerCase()) != 0.0) { //Make sure item is a sellable item
				double itemCost = plugin.getSellData().getDouble(item.getType().toString().toLowerCase());
				totalCost += itemCost * item.getAmount();
				amount += item.getAmount();
				inv.setItem(i, null);
			}
		}
		if(amount > 0) {
			plugin.getEcon().depositPlayer(player, totalCost);
			sellReceipt(player, totalCost, amount);
			player.closeInventory();
		}
	}
	
	private boolean isInvFull(Player player) {
		if(player.getInventory().firstEmpty() == -1) {
			return true;
		}else {
			return false;
		}
	}
	
	private boolean playerHasBalance(Player player, double price) {
		if(plugin.getEcon().getBalance(player) >= price) {
			return true;
		}else {
			return false;
		}
	}
	
	private void shopReceipt(Player player, double price, int amount, String itemName) {
		itemName = itemName.replace('_', ' ');
		if(amount > 1) {
			itemName = itemName + "s";
		}
		player.sendMessage(" ");
		player.sendMessage(ChatColor.GREEN + "You have successfully purchased " + ChatColor.GRAY + amount + " " + ChatColor.GREEN + itemName + " for " + ChatColor.GRAY + "$" +  String.format("%,.2f",price) + ChatColor.GREEN + " from the server.");
		player.sendMessage(" ");
	}
	
	private void sellReceipt(Player player, double price, int amount) {
		String noun = "item";
		if(amount > 1) {
			noun = "items";
		}
		
		player.sendMessage(" ");
		player.sendMessage(ChatColor.GREEN + "You have successfully sold " + ChatColor.GRAY + amount + " " + ChatColor.GREEN + noun + " for " + ChatColor.GRAY + "$" +  String.format("%,.2f",price) + ChatColor.GREEN + " to the server.");
		player.sendMessage(" ");
	}
	
}
