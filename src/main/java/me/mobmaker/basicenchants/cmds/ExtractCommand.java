package me.mobmaker.basicenchants.cmds;

import me.mobmaker.basicenchants.data.Messages;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

public class ExtractCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!command.getName().equalsIgnoreCase("extract")) { return false; }
        if (args.length != 1) { return false; }
        if (sender instanceof Player) {
            Player p = (Player) sender;
            ItemStack hand = p.getInventory().getItemInMainHand();
            Enchantment extractEnchant = Enchantment.getByKey(NamespacedKey.minecraft(args[0].toLowerCase()));
            if (extractEnchant == null) { return sendStep(p,1); }
            if (!hand.getType().isItem() || hand.getType().equals(Material.ENCHANTED_BOOK)) { return sendStep(p,2); }
            if (!hand.containsEnchantment(extractEnchant)) { return sendStep(p,3); }
            if (p.getInventory().firstEmpty() == -1) { return sendStep(p,4); }
            int enchantLevel = hand.getEnchantmentLevel(extractEnchant);
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

    private boolean sendStep(Player p, int step) {
        switch (step) {
            case 1:
                p.sendMessage(Messages.ENCHANT_INVALID.get());
                return true;
            case 2:
                p.sendMessage(Messages.ENCHANT_BAD_TOOL.get());
                return true;
            case 3:
                p.sendMessage(Messages.ENCHANT_NOT_INSTALLED.get());
                return true;
            case 4:
                p.sendMessage(Messages.ENCHANT_FULLINV.get());
        }
        return true;
    }

    //thing to auto-complete the arg

}
