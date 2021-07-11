package me.mobmaker.basicenchants.cmds;

import me.mobmaker.basicenchants.data.Messages;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class EnchantCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!command.getName().equalsIgnoreCase("enchant")) { return false; }
        if (args.length != 2) { return false; }
        if (sender instanceof Player) {
            Player p = (Player) sender;
            ItemStack hand = p.getInventory().getItemInMainHand();
            int enchantLevel;
            Enchantment enchant = Enchantment.getByKey(NamespacedKey.minecraft(args[0].toLowerCase()));
            if (enchant == null) { return sendStep(p,1); }
            if (!hand.getType().isItem()) { return sendStep(p,2); }
            try { enchantLevel = Integer.parseInt(args[1]);
            } catch (Exception e) { return sendStep(p,3); }
            hand.addUnsafeEnchantment(enchant, enchantLevel);
            p.sendMessage(Messages.ENCHANT_ENCHANTED.get(hand.displayName().color(NamedTextColor.AQUA),
                    enchant.displayName(enchantLevel).color(NamedTextColor.GREEN)));
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
                p.sendMessage(Messages.ENCHANT_NOT_NUMBER.get());
                return true;
        }
        return true;
    }
}
