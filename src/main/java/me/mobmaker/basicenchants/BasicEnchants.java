package me.mobmaker.basicenchants;

import me.mobmaker.basicenchants.cmds.EnchantCommand;
import me.mobmaker.basicenchants.cmds.ExtractCommand;
import me.mobmaker.basicenchants.data.Utilities;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public final class BasicEnchants extends JavaPlugin {

    public static boolean isEcon;
    private static Economy econ;
    private static BasicEnchants instance;

    @Override
    public void onEnable() {
        if (!setupEconomy()) {
            this.getLogger().severe("Economy not found. Economy integration disabled!");
            isEcon = false;
        } else isEcon = true;
        instance = this;
        Utilities.init();
        this.saveDefaultConfig();
        //Utilities.updateConfig();
        Bukkit.getPluginManager().registerEvents(new AnvilEvent(), this);
        this.getCommand("extract").setExecutor(new ExtractCommand());
        this.getCommand("enchant").setExecutor(new EnchantCommand());
    }

    public static BasicEnchants instance() {
        return instance;
    }

    public static FileConfiguration config() {
        return instance().getConfig();
    }

    public static double version() {
        return Double.parseDouble(instance.getDescription().getVersion());
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
        return true;
    }

    public static Economy economy() {
        return econ;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
