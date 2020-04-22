package me.oriharel.machinery.inventory;

import me.oriharel.machinery.Machinery;
import me.oriharel.machinery.fuel.PlayerFuel;
import me.oriharel.machinery.machine.PlayerMachine;
import me.oriharel.machinery.utilities.NMS;
import me.oriharel.machinery.utilities.Utils;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Listeners implements Listener {

    private Machinery machinery;
    private Map<Inventory, ItemStack[]> inventories;

    public Listeners(Machinery machinery) {
        this.machinery = machinery;
        this.inventories = new HashMap<>();
    }

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

    @EventHandler
    public void onInventoryOpenHandleFuels(InventoryOpenEvent e) {
        Inventory inventory = e.getInventory();
        if (!(inventory.getHolder() instanceof InventoryPage)) return;
        if (!e.getView().getTitle().equalsIgnoreCase("fuels")) return;

        inventories.put(inventory, inventory.getContents());
    }

    @EventHandler // some brain damaging shit code to read
    public void onInventoryCloseHandleFuels(InventoryCloseEvent e) {
        Inventory inventory = e.getInventory();
        if (!(inventory.getHolder() instanceof InventoryPage)) return;
        if (!e.getView().getTitle().equalsIgnoreCase("fuels")) return;

        // TODO: Check if fuel goes above max

        InventoryPage page = (InventoryPage) inventory.getHolder();
        ItemStack[] previousContents = inventories.get(inventory);
        ItemStack[] newContents = new ItemStack[inventory.getContents().length];
        boolean addedNonFuelItem = false;
        int i = 0;
        int previousSumFuels = page.owner.getFuels().stream().mapToInt(PlayerFuel::getEnergy).sum();
        for (ItemStack item : inventory.getContents()) {
            if (item == null || item.equals(previousContents[i]) || item.getType() == Material.AIR) {
                i++;
                continue;
            }
            boolean found = false;
            System.out.println("-------------------------------");
            for (String key : NMS.getItemStackNBTTMapClone(item).keySet()) {
                if (machinery.getFuelManager().getNbtIds().containsKey(key)) {
                    found = true;
                    newContents[i] = item;
                    Optional<PlayerFuel> fuelInMachine = page.owner.getFuels().stream().filter(f -> f.getName().equalsIgnoreCase(key)).findAny();
                    if (fuelInMachine.isPresent()) {
                        fuelInMachine.get().setAmount(fuelInMachine.get().getAmount() + item.getAmount() - previousContents[i].getAmount());
                    } else {
                        page.owner.getFuels().add(machinery.getFuelManager().getPlayerFuelItemByNbtId(key, item.getAmount()));
                    }
                    break;
                }
            }
            if (found) {
                i++;
                continue;
            }

            Optional<PlayerFuel> fuelInMachine = page.owner.getFuels().stream().filter(f -> f.getType() == item.getType()).findAny();
            if (fuelInMachine.isPresent()) {
                fuelInMachine.get().setAmount(fuelInMachine.get().getAmount() + item.getAmount() - previousContents[i].getAmount());
            } else {
                PlayerFuel fuel = machinery.getFuelManager().getPlayerFuelItem(item.getType(), item.getAmount());
                if (fuel == null) {
                    addedNonFuelItem = true;
                    if (Utils.inventoryHasSpaceForItemAdd(e.getPlayer().getInventory(), item))
                        e.getPlayer().getInventory().addItem(item);
                    else
                        e.getPlayer().getLocation().getWorld().dropItemNaturally(e.getPlayer().getLocation(), item);
                    i++;
                    continue;
                }
                page.owner.getFuels().add(fuel);
            }
            newContents[i] = item;
            i++;
        }
        inventory.setContents(newContents);
        // if previous sum was zero and current is bigger than 0 start mine process
        if (previousSumFuels == 0 && previousSumFuels < page.owner.getFuels().stream().mapToInt(PlayerFuel::getEnergy).sum()) {
            page.owner.getMinerProcess().startProcess();
        }
        if (addedNonFuelItem) {
            e.getPlayer().sendMessage("§c§lYou added a non fuel item to the fuels inventory. It was added back to your inventory.");
        }
    }


    private void startMiningProcessIf0Fuel(PlayerMachine machine) {
        if (machine.getFuels().stream().mapToInt(ItemStack::getAmount).sum() == 0) {
            machine.getMinerProcess().startProcess();
        }
    }
}
