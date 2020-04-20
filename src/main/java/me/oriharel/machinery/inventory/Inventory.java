package me.oriharel.machinery.inventory;

import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;

import java.util.Map;

public class Inventory implements InventoryHolder {

    protected Map<String, InventoryPage> routes;
    protected InventoryPage currentPage;
    protected Player viewer;

    /**
     * Construct an inventory.
     *
     * @param routes must have a route named start as it will be the player's initial route
     * @param viewer the inventory viewer
     */
    public Inventory(Map<String, InventoryPage> routes, Player viewer) {
        this.routes = routes;
        this.viewer = viewer;
    }

    public void start() {
        viewer.openInventory(getInventory());
    }

    public void addRoute(String name, InventoryPage page) {
        routes.put(name, page);
    }

    public void navigateToNamedRoute(String name) {
        InventoryPage page = routes.get(name);
        viewer.openInventory(page.getInventory());
        currentPage = page;
    }

    @Override
    public org.bukkit.inventory.Inventory getInventory() {
        InventoryPage page = routes.get("start");
        currentPage = page;
        return page.getInventory();
    }
}
