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

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

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

        InventoryPage page = (InventoryPage) inventory.getHolder();
        ItemStack[] previousContents = inventories.get(inventory);
        ItemStack[] newContents = new ItemStack[inventory.getContents().length];
        List<ItemStack> nonFuelItems = new ArrayList<>();
        int i = 0;
        int previousSumFuels = page.owner.getFuels().stream().mapToInt(PlayerFuel::getEnergy).sum();
        for (ItemStack item : inventory.getContents()) {
            if (item == null || item.equals(previousContents[i]) || item.getType() == Material.AIR) {
                i++;
                continue;
            }
            boolean found = false;
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
                    nonFuelItems.add(item);
                    i++;
                    continue;
                }
                page.owner.getFuels().add(fuel);
            }
            newContents[i] = item;
            i++;
        }
        inventory.setContents(newContents);

        int totalEnergy = page.owner.getFuels().stream().mapToInt(PlayerFuel::getEnergy).sum();
        // if previous sum was zero and current is bigger than 0 start mine process
        if (previousSumFuels == 0 && previousSumFuels < totalEnergy) {
            page.owner.getMinerProcess().startProcess();
        }
        if (!nonFuelItems.isEmpty()) {
            for (ItemStack nonFuelItem : nonFuelItems) {
                if (Utils.inventoryHasSpaceForItemAdd(e.getPlayer().getInventory(), nonFuelItem))
                    e.getPlayer().getInventory().addItem(nonFuelItem);
                else
                    e.getPlayer().getLocation().getWorld().dropItemNaturally(e.getPlayer().getLocation(), nonFuelItem);
            }
            e.getPlayer().sendMessage("§c§lYou've added non fuel items to the fuels inventory. They were added back to your inventory.");
        }

        if (totalEnergy > page.owner.getMaxFuel()) {
            e.getPlayer().sendMessage("§c§lYou've surpassed the max fuel limit of this machine. Removing necessary fuels.");
            Set<PlayerFuel> fuelsRemoved = new HashSet<>();
            int fuelNeededToRemove = totalEnergy - page.owner.getMaxFuel();
            // sorting so that it'll remove the fuels with the least amount of fuel. ending with the maximized amount of fuel with the fuels entered
            List<PlayerFuel> sortedAscendingPlayerFuel = page.owner.getFuels().stream().sorted(Comparator.comparingInt(PlayerFuel::getEnergy)).collect(Collectors.toList());
            for (PlayerFuel playerFuel : sortedAscendingPlayerFuel) {
                if (fuelNeededToRemove <= 0) break;
                if (playerFuel.getAmount() > 1) {
                    int realEnergy = playerFuel.getEnergy() / playerFuel.getAmount();
                    int neededToRemove = (int) Math.ceil((double) realEnergy / fuelNeededToRemove);
                    if (neededToRemove <= playerFuel.getAmount()) {
                        PlayerFuel clone = playerFuel.clone();
                        clone.setAmount(neededToRemove);
                        playerFuel.setAmount(playerFuel.getAmount() - neededToRemove);
                        fuelsRemoved.add(clone);
                    }
                } else {
                    fuelsRemoved.add(playerFuel);
                    page.owner.getFuels().remove(playerFuel);
                    fuelNeededToRemove -= playerFuel.getEnergy();
                }
            }
            fuelsRemoved.forEach(f -> {
                if (Utils.inventoryHasSpaceForItemAdd(e.getPlayer().getInventory(), f))
                    e.getPlayer().getInventory().addItem(f.clone());
                else
                    e.getPlayer().getLocation().getWorld().dropItemNaturally(e.getPlayer().getLocation(), f.clone());
            });

        }
    }


    private void startMiningProcessIf0Fuel(PlayerMachine machine) {
        if (machine.getFuels().stream().mapToInt(ItemStack::getAmount).sum() == 0) {
            machine.getMinerProcess().startProcess();
        }
    }
}
