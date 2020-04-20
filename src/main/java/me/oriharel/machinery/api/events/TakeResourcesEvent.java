package me.oriharel.machinery.api.events;

import me.oriharel.machinery.machine.PlayerMachine;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * Called when a player takes items from a resource inventory of a machine
 */
public class TakeResourcesEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();

    private ItemStack resourceTaken;
    private PlayerMachine machine;
    private Inventory openedInventory;
    private int slotTakenFrom;

    public TakeResourcesEvent(ItemStack resourceTaken, PlayerMachine machine, Inventory openedInventory, int slotTakenFrom) {
        this.resourceTaken = resourceTaken;
        this.machine = machine;
        this.openedInventory = openedInventory;
        this.slotTakenFrom = slotTakenFrom;
    }

    public ItemStack getResourceTaken() {
        return resourceTaken;
    }

    public PlayerMachine getMachine() {
        return machine;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public Inventory getOpenedInventory() {
        return openedInventory;
    }

    public int getSlotTakenFrom() {
        return slotTakenFrom;
    }
}
