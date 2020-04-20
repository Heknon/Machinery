package me.oriharel.machinery.api.events;

import me.oriharel.machinery.machine.PlayerMachine;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * Called when a player adds items to a resource inventory of a machine
 */
public class GiveResourcesEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();

    private ItemStack resourceGiven;
    private PlayerMachine machine;
    private Inventory openedInventory;

    public GiveResourcesEvent(ItemStack resourceGiven, PlayerMachine machine, Inventory openedInventory) {
        this.resourceGiven = resourceGiven;
        this.machine = machine;
        this.openedInventory = openedInventory;
    }

    public ItemStack getResourceGiven() {
        return resourceGiven;
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
}
