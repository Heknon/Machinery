package me.oriharel.machinery.inventory;

import me.oriharel.machinery.CallbackR;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.Objects;

public class InventoryItem {
    protected final ItemStack itemStack;
    protected int indexInInventory;
    protected CallbackR<Boolean> onClick;

    public InventoryItem(int indexInInventory, Material material, int amount, String displayName, String ...lore) {
        this.itemStack = new ItemStack(material, amount);
        ItemMeta meta = this.itemStack.getItemMeta();
        meta.setDisplayName(displayName);
        meta.setLore(Arrays.asList(lore));
        this.itemStack.setItemMeta(meta);
        this.indexInInventory = indexInInventory;
    }

    public InventoryItem(Material material, int amount, String displayName, String ...lore) {
        this.itemStack = new ItemStack(material, amount);
        ItemMeta meta = this.itemStack.getItemMeta();
        meta.setDisplayName(displayName);
        meta.setLore(Arrays.asList(lore));
        this.itemStack.setItemMeta(meta);
        this.indexInInventory = -1;
    }

    public InventoryItem(Material material, int amount, String displayName) {
        this.itemStack = new ItemStack(material, amount);
        ItemMeta meta = this.itemStack.getItemMeta();
        meta.setDisplayName(displayName);
        this.itemStack.setItemMeta(meta);
        this.indexInInventory = -1;
    }

    public InventoryItem setOnClick(CallbackR<Boolean> onClick) {
        this.onClick = onClick;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InventoryItem that = (InventoryItem) o;
        return indexInInventory == that.indexInInventory &&
                Objects.equals(itemStack, that.itemStack) &&
                Objects.equals(onClick, that.onClick);
    }

    public boolean equals(ItemStack itemStack, int indexInInventory) {
        if (itemStack == null) return false;
        return itemStack.equals(this.itemStack) && indexInInventory == this.indexInInventory;
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemStack, indexInInventory, onClick);
    }
}
