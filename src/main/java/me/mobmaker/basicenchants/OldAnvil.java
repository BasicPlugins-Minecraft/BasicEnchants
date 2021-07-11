package me.mobmaker.basicenchants;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

public class OldAnvil implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getWhoClicked() instanceof Player) {
            Player p = (Player) e.getWhoClicked();
            if (e.getView().getType() == InventoryType.ANVIL) {
                AnvilInventory anvilInventory = (AnvilInventory) e.getInventory();
                ItemStack slotOne = anvilInventory.getFirstItem();
                ItemStack slotTwo = anvilInventory.getSecondItem();
                if (slotTwo == null) { return; }
                if (slotOne == null) { return; }
                if (slotOne.getType().equals(Material.ENCHANTED_BOOK)) {
                    if (!slotTwo.getType().equals(Material.ENCHANTED_BOOK)) { return; }
                    //create the final item
                    ItemStack finalItem = new ItemStack(Material.ENCHANTED_BOOK);
                    EnchantmentStorageMeta finalMeta = (EnchantmentStorageMeta) finalItem.getItemMeta();
                    //get the storage for the books
                    EnchantmentStorageMeta meta = (EnchantmentStorageMeta) slotOne.getItemMeta();
                    EnchantmentStorageMeta metaTwo = (EnchantmentStorageMeta) slotTwo.getItemMeta();

                    for (Enchantment ench : meta.getStoredEnchants().keySet()) {
                        int enchLevel = meta.getEnchantLevel(ench);
                        int secondLevel = metaTwo.getEnchantLevel(ench);
                        if (!metaTwo.hasStoredEnchant(ench)) { return; }
                        if (enchLevel == secondLevel) {
                            finalMeta.addStoredEnchant(ench, enchLevel + 1, true);
                        }
                        if (enchLevel < secondLevel) {
                            finalMeta.addStoredEnchant(ench, secondLevel, true);
                        }
                    }
                    finalItem.setItemMeta(finalMeta);
                    anvilInventory.setResult(finalItem);
                } else {
                    ItemStack finalItem = new ItemStack(anvilInventory.getItem(0).getType());
                    for (Enchantment ench : slotOne.getEnchantments().keySet()) {
                        int enchLevel = slotOne.getEnchantmentLevel(ench);
                        int secondLevel = slotTwo.getEnchantmentLevel(ench);
                        if (!slotTwo.containsEnchantment(ench)) { return; }
                        if (slotTwo.getType().equals(Material.ENCHANTED_BOOK)) {
                            EnchantmentStorageMeta metaTwo = (EnchantmentStorageMeta) slotTwo.getItemMeta();
                            secondLevel = metaTwo.getStoredEnchantLevel(ench);
                        }
                        if (enchLevel == secondLevel) {
                            finalItem.addUnsafeEnchantment(ench, enchLevel + 1);
                        }
                        if (enchLevel < secondLevel) {
                            finalItem.addUnsafeEnchantment(ench, secondLevel);
                        }
                    }
                    anvilInventory.setResult(finalItem);
                }

            }
        }
    }
}
