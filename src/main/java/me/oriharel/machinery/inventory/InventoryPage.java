package me.oriharel.machinery.inventory;

import me.oriharel.machinery.machine.PlayerMachine;
import me.oriharel.machinery.utilities.Callback;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftInventoryCustom;
import org.bukkit.craftbukkit.v1_15_R1.util.CraftMagicNumbers;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Field;
import java.util.Set;

public class InventoryPage implements InventoryHolder {

    private static final Field ITEM_MAX_STACK;

    static {
        try {
            ITEM_MAX_STACK = net.minecraft.server.v1_15_R1.Item.class
                    .getDeclaredField("maxStackSize");
            ITEM_MAX_STACK.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }


    protected final int size;
    protected final String title;
    protected final InventoryItem fillment;
    protected final Set<InventoryItem> inventoryItems;
    protected final Inventory inventory;
    protected Callback onClose;
    protected boolean cancelClick = true;
    protected PlayerMachine owner;

    public InventoryPage(int size, String title, InventoryItem fillment, Set<InventoryItem> inventoryItems, PlayerMachine owner) {
        this.size = size;
        this.title = title;
        this.fillment = fillment;
        this.inventoryItems = inventoryItems;
        this.onClose = null;
        this.owner = owner;
        inventory = new CraftInventoryCustom(this, size, title);
        inventory.setMaxStackSize(20000000);
        populateItems();
    }

    public InventoryPage(int size, String title, InventoryItem fillment, Set<InventoryItem> inventoryItems,
                         Callback onClose, PlayerMachine owner) {
        this.size = size;
        this.title = title;
        this.fillment = fillment;
        this.inventoryItems = inventoryItems;
        this.onClose = onClose;
        this.owner = owner;
        inventory = new CraftInventoryCustom(this, size, title);
        inventory.setMaxStackSize(2000000000);
        populateItems();
    }

    private void populateItems() {
        ItemStack[] contents = inventory.getContents();
        inventoryItems.forEach(item -> contents[item.indexInInventory] = item);
        for (int i = 0; i < contents.length; i++) {
            ItemStack item = contents[i];
            if ((item == null || item.getType() == Material.AIR) && fillment != null) {
                contents[i] = fillment.clone();
            }
        }
        inventory.setContents(contents);
    }


    public InventoryPage setCancelClick(boolean cancelClick) {
        this.cancelClick = cancelClick;
        return this;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
