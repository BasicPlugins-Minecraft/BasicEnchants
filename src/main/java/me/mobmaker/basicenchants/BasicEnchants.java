package me.mobmaker.basicenchants;

import me.mobmaker.basicenchants.cmds.EnchantCommand;
import me.mobmaker.basicenchants.cmds.ExtractCommand;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class BasicEnchants extends JavaPlugin {

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(new AnvilEvent(), this);
        this.getCommand("extract").setExecutor(new ExtractCommand());
        this.getCommand("enchant").setExecutor(new EnchantCommand());

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
