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
import org.bukkit.inventory.meta.ItemMeta;

public class AnvilEvent implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getWhoClicked() instanceof Player) {
            Player p = (Player) e.getWhoClicked();
            if (e.getView().getType() == InventoryType.ANVIL) {
                AnvilInventory anvilInventory = (AnvilInventory) e.getInventory();
                ItemStack slotOne = anvilInventory.getFirstItem();
                ItemStack slotTwo = anvilInventory.getSecondItem();
                if (slotOne == null) { return; }
                if (slotTwo == null) { return; }
                if (slotOne.getType().equals(Material.ENCHANTED_BOOK)) {
                    EnchantmentStorageMeta metaOne = (EnchantmentStorageMeta) slotOne.getItemMeta();
                    if (!slotTwo.getType().equals(Material.ENCHANTED_BOOK)) { return; }
                    ItemStack finalItem = slotOne;
                    EnchantmentStorageMeta finalMeta = (EnchantmentStorageMeta) finalItem.getItemMeta();
                    EnchantmentStorageMeta metaTwo = (EnchantmentStorageMeta) slotTwo.getItemMeta();
                    for (Enchantment ench : metaOne.getStoredEnchants().keySet()) {
                        int levelOne = metaOne.getStoredEnchantLevel(ench);
                        int levelTwo = metaTwo.getStoredEnchantLevel(ench);
                        if (!metaTwo.hasStoredEnchant(ench)) { return; }
                        if (levelOne == levelTwo) {
                            finalMeta.addStoredEnchant(ench, levelOne + 1, true);
                        } else if (levelTwo > levelOne) {
                            finalMeta.addStoredEnchant(ench, levelTwo, true);
                        } else return;
                        finalItem.setItemMeta(finalMeta);
                        anvilInventory.setResult(finalItem);
                    }
                } else if (slotOne.getType().isItem()) {
                    ItemStack finalItem = slotOne;
                    ItemMeta finalMeta = finalItem.getItemMeta();
                    ItemMeta metaOne = slotOne.getItemMeta();
                    //if combining with a book
                    if (slotTwo.getType().equals(Material.ENCHANTED_BOOK)) {
                        EnchantmentStorageMeta metaTwo = (EnchantmentStorageMeta) slotTwo.getItemMeta();
                        for (Enchantment ench : metaTwo.getStoredEnchants().keySet()) {
                            int levelOne = metaOne.getEnchantLevel(ench);
                            int levelTwo = metaTwo.getStoredEnchantLevel(ench);
                            if (levelOne == levelTwo) {
                                finalMeta.addEnchant(ench, levelOne + 1, true);
                            } else if (levelTwo > levelOne) {
                                finalMeta.addEnchant(ench, levelTwo, true);
                            } else return;
                            finalItem.setItemMeta(finalMeta);
                            anvilInventory.setResult(finalItem);
                        }
                    //if using anything else
                    } else {
                        ItemMeta metaTwo = slotTwo.getItemMeta();
                        for (Enchantment ench : metaTwo.getEnchants().keySet()) {
                            int levelOne = metaOne.getEnchantLevel(ench);
                            int levelTwo = metaTwo.getEnchantLevel(ench);
                            if (levelOne == levelTwo) {
                                finalMeta.addEnchant(ench, levelOne + 1, true);
                            } else if (levelTwo > levelOne) {
                                finalMeta.addEnchant(ench, levelTwo, true);
                            } else return;
                            finalItem.setItemMeta(finalMeta);
                            anvilInventory.setResult(finalItem);
                        }
                    }
                }
            }
        }
    }
}
