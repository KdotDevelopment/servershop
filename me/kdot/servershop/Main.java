package me.kdot.servershop;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import me.kdot.servershop.commands.CommandSell;
import me.kdot.servershop.commands.CommandServerShop;
import me.kdot.servershop.commands.CommandShop;
import net.milkbowl.vault.economy.Economy;

public class Main extends JavaPlugin implements Listener {

	private File shopDataf;
	private File sellDataf;
	private FileConfiguration shopData; 
	private FileConfiguration sellData;
	
	private NamespacedKey key = new NamespacedKey(this, "ServerShop");
	
	private Economy econ;
	
	public String pluginVersion = "v1.0.2"; //don't forget to change plugin.yml version, too
	
	public void onEnable() {
		if (!setupEconomy()) {
            this.getLogger().severe("Disabled due to no Vault dependency found!");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
		
		getServer().getPluginManager().registerEvents(this, this);
		this.getCommand("servershop").setExecutor(new CommandServerShop(this));
		this.getCommand("shop").setExecutor(new CommandShop(this));
		this.getCommand("sell").setExecutor(new CommandSell(this));
		
		initFiles();
	}
	
	private boolean setupEconomy() {
		if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
		RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
	}
	
	private void initFiles() {
		shopDataf = new File(getDataFolder(), "shop.yml");
		sellDataf = new File(getDataFolder(), "sell.yml");
		
		if(!shopDataf.exists()) {
			shopDataf.getParentFile().mkdirs();
			saveResource("shop.yml", false);
		}
		
		if(!sellDataf.exists()) {
			sellDataf.getParentFile().mkdirs();
			saveResource("sell.yml", false);
		}
		
		shopData = new YamlConfiguration();
		sellData = new YamlConfiguration();
		
		try {
			shopData.load(shopDataf);
			sellData.load(sellDataf);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
		}
	}
	
	public FileConfiguration getShopData() {
		return shopData;
	}
	
	public FileConfiguration getSellData() {
		return sellData;
	}
	
	public void saveItemData() {
		try {
			shopData.save(shopDataf);
			sellData.save(sellDataf);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void reloadItemData() {
		try {
			shopData.load(shopDataf);
			sellData.load(sellDataf);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
		}
	}
	
	public Economy getEcon() {
		return econ;
	}
	
	//Security to make sure players do not have illegal items
	@EventHandler
	public void onInventoryClose(InventoryCloseEvent e) {
		Bukkit.getScheduler().runTask(this, new Runnable() {
		    @Override
		    public void run() {
		    	for(ItemStack item : e.getPlayer().getInventory().getStorageContents()) {
					if(item == null) continue;
					if(item.getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING) != null) {
						e.getPlayer().getInventory().remove(item);
					}
				}
		    }
		});
		
	}
	
	@EventHandler
	public void onInventoryOpen(InventoryOpenEvent e) {
		Bukkit.getScheduler().runTask(this, new Runnable() {
		    @Override
		    public void run() {
		    	for(ItemStack item : e.getPlayer().getInventory().getStorageContents()) {
					if(item == null) continue;
					if(item.getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING) != null) {
						e.getPlayer().getInventory().remove(item);
					}
				}
		    }
		});
		
	}
	
}
