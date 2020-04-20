package me.oriharel.machinery.inventory;

import me.oriharel.machinery.utilities.Callback;
import me.oriharel.machinery.utilities.NMS;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.Objects;

public class InventoryItem extends ItemStack {
    protected int indexInInventory;
    protected Callback onClick = null;

    public InventoryItem(int indexInInventory, Material material, int amount, String displayName, String... lore) {
        super(material, amount);
        ItemMeta meta = NMS.getItemStackMetaReference(this);
        meta.setDisplayName(displayName);
        meta.setLore(Arrays.asList(lore));
        this.indexInInventory = indexInInventory;

    }

    public InventoryItem(int indexInInventory, ItemStack itemStack) {
        super(itemStack);
        this.indexInInventory = indexInInventory;
    }

    public InventoryItem(int indexInInventory, Material material, int amount, String displayName) {
        super(material, amount);
        ItemMeta meta = NMS.getItemStackMetaReference(this);
        meta.setDisplayName(displayName);
        this.indexInInventory = indexInInventory;
    }

    public InventoryItem runOnClick() {
        if (onClick == null) return this;
        onClick.apply();
        return this;
    }

    public InventoryItem setOnClick(Callback onClick) {
        this.onClick = onClick;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        InventoryItem that = (InventoryItem) o;
        return indexInInventory == that.indexInInventory &&
                Objects.equals(onClick, that.onClick);
    }

    public boolean equals(ItemStack itemStack, int indexInInventory) {
        if (itemStack == null) return false;
        return itemStack.equals(this) && indexInInventory == this.indexInInventory;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), indexInInventory, onClick);
    }
}
