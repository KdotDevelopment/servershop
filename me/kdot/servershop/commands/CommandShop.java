package me.kdot.servershop.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.kdot.servershop.Main;
import me.kdot.servershop.gui.InventoryShop;
import net.md_5.bungee.api.ChatColor;

public class CommandShop implements CommandExecutor {

	private Main plugin;
	
	public CommandShop(Main plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(label.equalsIgnoreCase("shop") || label.equalsIgnoreCase("servershop:shop")) {
			if(sender instanceof Player) {
				if(sender.hasPermission("servershop.shop")) {
					plugin.reloadItemData();
					InventoryShop gui = new InventoryShop(plugin, (Player)sender);
					gui.openInv();
				}
			}else {
				sender.sendMessage(ChatColor.RED + "[Server Shop] You must be a player!");
			}
		}
		
		return false;
	}

}
