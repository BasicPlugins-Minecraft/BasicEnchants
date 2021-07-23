package me.mobmaker.basicenchants.data;

import org.bukkit.Registry;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.enchantments.Enchantment;

import java.util.ArrayList;
import java.util.List;

public class Utilities implements TabCompleter {

    private static final ArrayList<String> enchants = new ArrayList<>();

    public static void init() {
        for (Enchantment enchantment : Registry.ENCHANTMENT) {
            enchants.add(enchantment.getKey().getKey());
        }
    }

    public static int round(int num, int precision) {
        return Math.round(num * (10 * precision)) / (10 * precision);
    }

    public static double round(double num, int precision) {
        return (double) Math.round(num * (10 * precision)) / (10 * precision);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (command.getName().equalsIgnoreCase("extract")) {
            return enchants;
        }
        if ( command.getName().equalsIgnoreCase("enchant")) {
            return enchants;
        }
        return null;
    }

    /* public static void updateConfig() {
        try {
            File configFile = new File(BasicEnchants.instance().getDataFolder(), "config.yml");
            FileConfiguration conf = new YamlConfiguration();
                conf.load(configFile);
            if (conf.getDouble("version") < BasicEnchants.version()) {
                Map<String, Object> values = conf.getValues(true);
                values.remove("version");
                boolean deleted = configFile.delete();
                BasicEnchants.instance().saveDefaultConfig();
                conf.load(configFile);
                for (String s : values.keySet()) {
                    conf.set(s, values.get(s));
                }
                conf.save(configFile);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/
}
