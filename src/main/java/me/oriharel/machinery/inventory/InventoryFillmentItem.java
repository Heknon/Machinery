package me.oriharel.machinery.inventory;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class InventoryFillmentItem extends InventoryItem {
    public InventoryFillmentItem(Material material, int amount, String displayName, String... lore) {
        super(-1, material, amount, displayName, lore);
    }

    public InventoryFillmentItem(ItemStack itemStack) {
        super(-1, itemStack);
    }

    public InventoryFillmentItem(Material material, int amount, String displayName) {
        super(-1, material, amount, displayName);
    }
}
