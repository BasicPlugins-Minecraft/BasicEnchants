package me.mobmaker.basicenchants.cmds;

import me.mobmaker.basicenchants.BasicEnchants;
import me.mobmaker.basicenchants.data.Messages;
import me.mobmaker.basicenchants.data.Utilities;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

public class ExtractCommand implements CommandExecutor {

    private final HashMap<UUID, XPLevel> xpPersist = new HashMap<>();
    private final HashMap<UUID, CashAmount> cashPersist = new HashMap<>();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, Command command, @NotNull String label, String[] args) {
        if (!command.getName().equalsIgnoreCase("extract")) return false;
        if (args.length != 1) return false;
        if (sender instanceof Player p) {
            ItemStack hand = p.getInventory().getItemInMainHand();
            Enchantment extractEnchant = Enchantment.getByKey(NamespacedKey.minecraft(args[0].toLowerCase()));
            if (extractEnchant == null) return sendStep(p,1);
            if (!hand.getType().isItem() || hand.getType().equals(Material.ENCHANTED_BOOK)) return sendStep(p,2);
            if (!hand.containsEnchantment(extractEnchant)) return sendStep(p,3);
            if (p.getInventory().firstEmpty() == -1) return sendStep(p,4);
            int enchantLevel = hand.getEnchantmentLevel(extractEnchant);
            //the penalization system handles the config value
            try { if (!penalty(p, hand, extractEnchant, enchantLevel)) return true; } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            hand.removeEnchantment(extractEnchant);
            ItemStack book = new ItemStack(Material.ENCHANTED_BOOK);
            EnchantmentStorageMeta meta = (EnchantmentStorageMeta) book.getItemMeta();
            meta.addStoredEnchant(extractEnchant, enchantLevel, true);
            book.setItemMeta(meta);
            p.getInventory().addItem(book);
            p.sendMessage(Messages.ENCHANT_EXTRACTED.get(
                    extractEnchant.displayName(enchantLevel).color(NamedTextColor.GREEN),
                    hand.displayName().color(NamedTextColor.AQUA)));
            return true;
        }
        return false;
    }

    private record XPLevel (Material mat, Enchantment enc, int levels) { }

    private record CashAmount (Material mat, Enchantment enc, double cash) { }

    private boolean sendStep(Player p, int step) {
        switch (step) {
            case 1 -> p.sendMessage(Messages.ENCHANT_INVALID.get());
            case 2 -> p.sendMessage(Messages.ENCHANT_BAD_TOOL.get());
            case 3 -> p.sendMessage(Messages.ENCHANT_NOT_INSTALLED.get());
            case 4 -> p.sendMessage(Messages.ENCHANT_FULLINV.get());
        }
        return true;
    }

    private int xpRandomizer(int encLevel, Enchantment enchantment) {
        if (encLevel <= enchantment.getMaxLevel()) {
            return new Random().nextInt((2 * encLevel) - 1 ) + 2;
        } else if (encLevel <= (2 * enchantment.getMaxLevel())) {
            return new Random().nextInt((2 * encLevel) - 7 ) + 8;
        }
        return new Random().nextInt((3 * encLevel) - 7 ) + 8;
    }

    private int cashRandomizer(int encLevel, Enchantment enchantment) {
        int min = BasicEnchants.config().getInt("extract.min");
        int max = BasicEnchants.config().getInt("extract.max");
        if (encLevel <= enchantment.getMaxLevel()) {
            return new Random().nextInt((max/3) - 1 ) + min;
        } else if (encLevel <= (2 * enchantment.getMaxLevel())) {
            return new Random().nextInt((max/2) - 7 ) + min*2;
        }
        return new Random().nextInt((max) - 7 ) + min*2;
    }

    private boolean penalty(Player p, ItemStack hand, Enchantment enc, int level) throws Exception {
        switch (Objects.requireNonNull(BasicEnchants.config().getString("penalty"))) {
            case "xp":
                UUID pid = p.getUniqueId();
                int expNeeded;
                if (xpPersist.containsKey(pid)) {
                    if (xpPersist.get(pid).mat == hand.getType() && xpPersist.get(pid).enc == enc) {
                        expNeeded = xpPersist.get(pid).levels();
                    } else expNeeded = xpRandomizer(level, enc);
                } else expNeeded = xpRandomizer(level, enc);
                if (p.getLevel() < expNeeded) {
                    xpPersist.put(p.getUniqueId(), new XPLevel(hand.getType(), enc, expNeeded));
                    p.sendMessage(Messages.ENCHANT_MORE_EXP.get(Component.text(Utilities.round(expNeeded - p.getLevel(),1))));
                    return false;
                }
                xpPersist.remove(p.getUniqueId());
                p.setLevel(p.getLevel() - expNeeded);
                return true;
            case "econ" :
                if (!BasicEnchants.isEcon) {
                    throw new Exception("Configuration value 'penalty' set to 'econ' even though there is no economy!");
                }
                Economy econ = BasicEnchants.economy();
                UUID id = p.getUniqueId();
                double cashNeeded;
                if (cashPersist.containsKey(id)) {
                    if (cashPersist.get(id).mat == hand.getType() && cashPersist.get(id).enc == enc) {
                        cashNeeded = cashPersist.get(id).cash;
                    } else cashNeeded = cashRandomizer(level, enc);
                } else cashNeeded = cashRandomizer(level, enc);
                if (econ.getBalance(p) < cashNeeded) {
                    cashPersist.put(p.getUniqueId(), new CashAmount(hand.getType(), enc, cashNeeded));
                    p.sendMessage(Messages.ENCHANT_MORE_CASH.get(
                            Component.text(Utilities.round(cashNeeded - econ.getBalance(p),1)),
                            Component.text(econ.currencyNameSingular())));
                    return false;
                }
                xpPersist.remove(p.getUniqueId());
                econ.withdrawPlayer(p, cashNeeded);
                return true;
            case "none":
                return true;
        }
        throw new Exception("Configuration value 'penalty' not properly set");
    }
}
