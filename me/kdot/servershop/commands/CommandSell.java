package me.kdot.servershop.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.kdot.servershop.Main;
import me.kdot.servershop.gui.InventorySell;
import net.md_5.bungee.api.ChatColor;

public class CommandSell implements CommandExecutor {

	private Main plugin;
	
	public CommandSell(Main plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(label.equalsIgnoreCase("sell") || label.equalsIgnoreCase("servershop:sell")) {
			if(sender instanceof Player) {
				if(sender.hasPermission("servershop.sell")) {
					plugin.reloadItemData();
					InventorySell gui = new InventorySell(plugin, (Player)sender);
					gui.openInv();
				}
			}else {
				sender.sendMessage(ChatColor.RED + "[Server Shop] You must be a player!");
			}
		}
		
		return false;
	}

}
