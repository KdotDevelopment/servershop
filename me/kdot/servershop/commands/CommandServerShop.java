package me.kdot.servershop.commands;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import me.kdot.servershop.Main;
import net.md_5.bungee.api.ChatColor;

public class CommandServerShop implements CommandExecutor {
	
	Main plugin;

	public CommandServerShop(Main plugin) {
		this.plugin = plugin;
	}
	
	// /servershop setprice <building-blocks/farming-foods/minerals/mobs/misc> <item> <new price>
	// /servershop additem <building-blocks/farming-foods/minerals/mobs/misc> <item> <price>
	// /servershop removeitem <building-blocks/farming-foods/minerals/mobs/misc> <item>
	// /servershop check - checks both config files for errors
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(label.equalsIgnoreCase("servershop") || label.equalsIgnoreCase("servershop:servershop")) {
			if(args.length == 0) {
				sender.sendMessage(ChatColor.GREEN + "ServerShop " + plugin.pluginVersion);
				sender.sendMessage(ChatColor.GREEN + "Made by KdotDevelopment");
				sender.sendMessage(ChatColor.RED + "Usage: /servershop check");
				return true;
			}
			if(args.length == 1 && args[0].equalsIgnoreCase("check")) {
				checkConfig(sender);
			}
			/*if(args.length == 1) {
				if(args[0].equalsIgnoreCase("setprice")) {
					sender.sendMessage(ChatColor.RED + "Usage: /servershop setprice <building-blocks/farming-foods/minerals/mobs/misc> <item> <new price>");
					return true;
				}
				if(args[0].equalsIgnoreCase("additem")) {
					sender.sendMessage(ChatColor.RED + "Usage: /servershop additem <building-blocks/farming-foods/minerals/mobs/misc> <item> <price>");
					return true;
				}
				if(args[0].equalsIgnoreCase("removeitem")) {
					sender.sendMessage(ChatColor.RED + "Usage: /servershop removeitem <building-blocks/farming-foods/minerals/mobs/misc> <item>");
					return true;
				}
				sender.sendMessage(ChatColor.GREEN + "ServerShop " + plugin.pluginVersion);
				sender.sendMessage(ChatColor.GREEN + "Made by KdotDevelopment");
				sender.sendMessage(ChatColor.RED + "Usage: /servershop <setprice/additem/removeitem>");
				return false;
			}
			if(args.length == 2) {
				if(args[0].equalsIgnoreCase("setprice") || args[0].equalsIgnoreCase("additem") || args[0].equalsIgnoreCase("removeitem")) {
					if(args[1].equalsIgnoreCase("building-blocks") || args[1].equalsIgnoreCase("farming-foods") || args[1].equalsIgnoreCase("minerals") || args[1].equalsIgnoreCase("mobs") || args[1].equalsIgnoreCase("misc")) {
						
					}else {
						sender.sendMessage(ChatColor.RED + "Available Categories: building-blocks farming-foods minerals mobs misc");
						return true;
					}
				}
				sender.sendMessage(ChatColor.GREEN + "ServerShop " + plugin.pluginVersion);
				sender.sendMessage(ChatColor.GREEN + "Made by KdotDevelopment");
				sender.sendMessage(ChatColor.RED + "Usage: /servershop <setprice/additem/removeitem>");
				return false;
			}*/
		}
		return false;
	}
	
	private void checkConfig(CommandSender sender) {
		boolean hasError = false;
		String sectionNames[] = {"building-blocks", "farming-foods", "minerals", "mobs", "misc"};
		plugin.reloadItemData();
		for(String sectionName : sectionNames) {
			for(String key : plugin.getShopData().getConfigurationSection(sectionName).getKeys(false)) {
				if(Material.getMaterial(key.toUpperCase()) == null) {
					sender.sendMessage(ChatColor.DARK_RED + "(shop.yml) Error Found: " + ChatColor.RED + sectionName + " " + key);
					hasError = true;
				}
			}
		}
		for(String key : plugin.getSellData().getKeys(false)) {
			if(Material.getMaterial(key.toUpperCase()) == null) {
				sender.sendMessage(ChatColor.DARK_RED + "(sell.yml)  " + ChatColor.BLACK + "." + ChatColor.DARK_RED + "Error Found: " + ChatColor.RED + key);
				hasError = true;
			}
		}
		if(hasError == false) {
			sender.sendMessage(ChatColor.GREEN + "No errors found.");
		}
		
	}
	
	
	
}
