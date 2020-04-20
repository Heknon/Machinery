package me.oriharel.machinery.inventory;

import me.oriharel.machinery.api.events.GiveResourcesEvent;
import me.oriharel.machinery.api.events.TakeResourcesEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.Map;

public class Listeners implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getInventory().getHolder() == null) return;
        if (!(e.getInventory().getHolder() instanceof InventoryPage)) return;
        InventoryPage inventory = (InventoryPage) e.getInventory().getHolder();
        ItemStack clicked = e.getCurrentItem();
        if (clicked == null) return;
        for (InventoryItem item : inventory.inventoryItems) {
            if (item.indexInInventory == e.getSlot()) {
                if (item instanceof InventoryNavigationItem) {
                    InventoryNavigationItem inventoryNavigationItem = (InventoryNavigationItem) item;
                    inventoryNavigationItem.runOnClick();
                    inventoryNavigationItem.navigate();
                } else {
                    item.runOnClick();

                }
            }
        }
        e.setCancelled(inventory.cancelClick);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        if (e.getInventory().getHolder() == null) return;
        if (!(e.getInventory().getHolder() instanceof InventoryPage)) return;
        InventoryPage inventory = (InventoryPage) e.getInventory().getHolder();
        if (inventory.onClose != null) inventory.onClose.apply();
    }



    // REPLACED WITH SIGN MENU
//    @EventHandler
//    public void onResourceGive(GiveResourcesEvent e) {
//        Map<Material, ItemStack> resourcesGained = e.getMachine().getResourcesGained();
//        ItemStack givenResource = e.getResourceGiven();
//
//        System.out.println(givenResource);
//        System.out.println("Before give: " + resourcesGained.get(givenResource.getType()));
//
//        if (!resourcesGained.containsKey(givenResource.getType())) {
//            resourcesGained.put(givenResource.getType(), givenResource);
//            return;
//        }
//
//        ItemStack resourceInMachine = resourcesGained.get(givenResource.getType());
//
//        resourceInMachine.setAmount(resourceInMachine.getAmount() + givenResource.getAmount());
//
//        System.out.println("After give: " + resourcesGained.get(givenResource.getType()));
//    }
//
//    @EventHandler
//    public void onResourceTake(TakeResourcesEvent e) {
//        Map<Material, ItemStack> resourcesGained = e.getMachine().getResourcesGained();
//        ItemStack takenResource = e.getResourceTaken();
//
//        System.out.println(takenResource);
//        ItemStack resourceInMachine = resourcesGained.get(takenResource.getType());
//        System.out.println("Before take: " + resourceInMachine);
//
//
//        // since item is taken, we remove from the amount of that resource the amount taken
//        resourceInMachine.setAmount(resourceInMachine.getAmount() - takenResource.getAmount());
//        System.out.println("After take: " + resourceInMachine);
//
//        ItemStack resourceToSetInInventory = resourceInMachine.clone();
//        // an inventory item can only hold up to 64 items in a stack. An itemstack of more than that is stored so i get the minimum just in case there is less than 64
//        // in the resource bank
//        resourceToSetInInventory.setAmount(Math.min(resourceInMachine.getAmount(), 64));
//        e.getOpenedInventory().setItem(e.getSlotTakenFrom(), resourceToSetInInventory);
//    }
//
//    /**
//     * Handles the calling of {@link me.oriharel.machinery.api.events.TakeResourcesEvent}
//     */
//    @EventHandler
//    public void onInventoryClickTakeResourceHandler(InventoryClickEvent e) {
//        if (e.getClickedInventory() == null) return;
//        if (!e.getClickedInventory().equals(e.getView().getTopInventory())) return;
//        if (!(e.getClickedInventory().getHolder() instanceof InventoryPage)) return;
//        if (!e.getView().getTitle().equalsIgnoreCase("resources")) return;
//        // negate all cases that an item is not taken
//        if (e.getClick() != ClickType.LEFT && e.getClick() != ClickType.RIGHT && e.getClick() != ClickType.SHIFT_LEFT && e.getClick() != ClickType.SHIFT_RIGHT && e.getClick() != ClickType.DOUBLE_CLICK)
//            return;
//
//        InventoryPage page = (InventoryPage) e.getClickedInventory().getHolder();
//        ItemStack clickedItem = e.getCurrentItem();
//
//        // on double click all items of same kind (up to 64) are compacted to one item.
//        if (clickedItem != null && e.getClick() == ClickType.DOUBLE_CLICK) {
//            Material type = clickedItem.getType();
//            ItemStack compactItem = clickedItem.clone();
//            compactItem.setAmount(0);
//            for (ItemStack item : page.getInventory().getContents()) {
//                if (compactItem.getAmount() > 64) break;
//                // doesn't break out of loop once compactItem + item reaches 64 since another item might fill it up to 64
//                if (item.getType() == type && compactItem.getAmount() + item.getAmount() <= 64) compactItem.setAmount(compactItem.getAmount() + item.getAmount());
//            }
//            Bukkit.getPluginManager().callEvent(new TakeResourcesEvent(compactItem, page.owner, e.getView().getTopInventory(), e.getSlot()));
//            return;
//        }
//
//        if (clickedItem != null && clickedItem.getType() != Material.AIR) {
//            if (e.getCursor() != null && e.getCursor().getType() == clickedItem.getType()) {
//                Bukkit.getPluginManager().callEvent(new TakeResourcesEvent(clickedItem.clone(), page.owner, e.getView().getTopInventory(), e.getSlot()));
//            } else if (e.getClick() == ClickType.RIGHT) {
//                System.out.println("------------------");
//                System.out.println(clickedItem);
//                ItemStack item = clickedItem.clone();
//                item.setAmount((item.getAmount() / 2) + 1);
//                System.out.println(item);
//                Bukkit.getPluginManager().callEvent(new TakeResourcesEvent(item, page.owner, e.getView().getTopInventory(), e.getSlot()));
//            } else {
//                System.out.println("CLONE: " + clickedItem.clone());
//                clickedItem.clone().setAmount(0);
//                Bukkit.getPluginManager().callEvent(new TakeResourcesEvent(clickedItem.clone(), page.owner, e.getView().getTopInventory(), e.getSlot()));
//            }
//        }
//    }
//
//    /**
//     * Handles the calling of {@link me.oriharel.machinery.api.events.GiveResourcesEvent}
//     */
//    @EventHandler
//    public void onInventoryClickGiveResourceHandler(InventoryClickEvent e) {
//        if (e.getClickedInventory() == null) return;
//        if (!(e.getView().getTopInventory().getHolder() instanceof InventoryPage)) return;
//        if (!e.getView().getTitle().equalsIgnoreCase("resources")) return;
//
//        ClickType clickType = e.getClick();
//        // negate all cases that an item is not given in
//        if (clickType != ClickType.LEFT && clickType != ClickType.RIGHT && clickType != ClickType.SHIFT_LEFT && clickType != ClickType.SHIFT_RIGHT) return;
//
//        InventoryPage page = (InventoryPage) e.getView().getTopInventory().getHolder();
//        if (page == null) return;
//        ItemStack clickedItem = e.getCurrentItem();
//        ItemStack cursorItem = e.getCursor();
//        boolean clickedTopInventory = e.getClickedInventory().equals(e.getView().getTopInventory());
//
//        // cannot give items when shift clicking the item cursor
//        if (clickedTopInventory) {
//            if (clickType == ClickType.SHIFT_RIGHT || clickType == ClickType.SHIFT_LEFT) return;
//            if (clickedItem != null && clickedItem.getType() != Material.AIR && cursorItem != null && cursorItem.getType() != Material.AIR)
//                Bukkit.getPluginManager().callEvent(new GiveResourcesEvent(cursorItem.clone(), page.owner, e.getView().getTopInventory()));
//            else if (cursorItem != null && cursorItem.getType() != Material.AIR && clickType == ClickType.RIGHT && (clickedItem == null || clickedItem.getType() == Material.AIR)) {
//                ItemStack resource = cursorItem.clone();
//                resource.setAmount(1);
//                Bukkit.getPluginManager().callEvent(new GiveResourcesEvent(resource, page.owner, e.getView().getTopInventory()));
//            } else if (cursorItem != null && cursorItem.getType() != Material.AIR) {
//                Bukkit.getPluginManager().callEvent(new GiveResourcesEvent(cursorItem.clone(), page.owner, e.getView().getTopInventory()));
//            }
//        } else if (clickedItem != null && clickedItem.getType() != Material.AIR && (clickType == ClickType.SHIFT_RIGHT || clickType == ClickType.SHIFT_LEFT)) {
//            Bukkit.getPluginManager().callEvent(new GiveResourcesEvent(clickedItem.clone(), page.owner, e.getView().getTopInventory()));
//        }
//    }
//
//    /**
//     * Handles the calling of {@link me.oriharel.machinery.api.events.GiveResourcesEvent} for when a player drags to spread out resources
//     */
//    @EventHandler
//    public void onInventoryDragEvent(InventoryDragEvent e) {
//        if (!(e.getInventory().getHolder() instanceof InventoryPage)) return;
//        if (!e.getView().getTitle().equalsIgnoreCase("resources")) return;
//
//        Collection<ItemStack> newItems = e.getNewItems().values();
//
//        final ItemStack[] itemStack = {null};
//        newItems.forEach(is -> {
//            if (itemStack[0] == null) {
//                itemStack[0] = is.clone();
//                itemStack[0].setAmount(0);
//            }
//            itemStack[0].setAmount(itemStack[0].getAmount() + is.getAmount());
//        });
//        Bukkit.getPluginManager().callEvent(new GiveResourcesEvent(itemStack[0], ((InventoryPage) e.getInventory().getHolder()).owner, e.getInventory()));
//    }
}
