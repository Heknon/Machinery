package me.oriharel.machinery.machine;

import me.oriharel.machinery.Machinery;
import me.oriharel.machinery.data.Chance;
import me.oriharel.machinery.data.MaterialChance;
import me.oriharel.machinery.data.ZenCoinChance;
import me.oriharel.machinery.fuel.PlayerFuel;
import me.oriharel.machinery.upgrades.AbstractUpgrade;
import me.oriharel.machinery.utilities.RandomCollection;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class MachineResourceGetProcess {
    protected List<ItemStack> itemsGained;
    protected PlayerMachine machine;
    protected int minePeriod;
    protected BukkitRunnable process;
    protected RandomCollection<Chance> materialChances;
    protected double lootAmplifier;
    protected Random random;

    public MachineResourceGetProcess(PlayerMachine machine) {
        itemsGained = new ArrayList<>();
        this.machine = machine;
        this.minePeriod = 20;
        this.random = new Random();
        this.materialChances = null;
        this.process = null;
        this.lootAmplifier = 1;
        initializeMaterialChances();
    }

    public void startProcess() {
        List<AbstractUpgrade> upgrades = machine.getUpgrades();
        upgrades.forEach(upgrade -> {
            upgrade.applyUpgradeModifier(this);
        });
        process = new BukkitRunnable() {
            @Override
            public void run() {
                if (machine.getFuels().isEmpty() || machine.getFuels().stream().mapToInt(PlayerFuel::getEnergy).sum() < machine.getFuelDeficiency()) {
                    cancel();
                    Player player = Bukkit.getPlayer(machine.getOwner());
                    if (player == null || !player.isOnline()) return;
                    player.sendMessage("§c§lOne of your machines doesn't have enough fuel to operate!");
                }
                getResources();
                upgrades.forEach(upgrade -> {
                    if (!upgrade.isRunOnlyOnProcessStart()) upgrade.applyUpgradeModifier(MachineResourceGetProcess.this);
                });
                insertResources();
                runFuelRemoval();
            }
        };
        process.runTaskTimerAsynchronously(Machinery.getInstance(), 0, minePeriod);
    }

    public void endProcess() {
        process.cancel();
    }

    public boolean isRunning() {
        return !process.isCancelled();
    }

    protected void getResources() {
        List<ItemStack> itemsGained = this.itemsGained;


        if (materialChances == null) {
            initializeMaterialChances();
        }
        Chance chance = materialChances.next();
        if (chance instanceof MaterialChance) {
            MaterialChance materialChance = (MaterialChance) chance;
            materialChance.getMaterials().forEach(mat -> itemsGained.add(new ItemStack(mat, ThreadLocalRandom.current().nextInt(chance.getMinimumAmount(),
                    chance.getMaximumAmount()))));
        } else if (chance instanceof ZenCoinChance) {
            double amount = ThreadLocalRandom.current().nextDouble(chance.getMinimumAmount(), chance.getMaximumAmount());
            machine.addZenCoinsGained(amount);
        }
    }

    public void runFuelRemoval() {
        Optional<PlayerFuel> fuelWithEnoughEnergy =
                machine.getFuels().stream().filter(fuel -> fuel.getEnergy() >= machine.getFuelDeficiency()).findAny();
        System.out.println("BEFORE: " + machine.getFuels().stream().mapToInt(PlayerFuel::getEnergy).sum());
        if (fuelWithEnoughEnergy.isPresent()) {
            PlayerFuel fuel = fuelWithEnoughEnergy.get();
            fuel.setEnergySloppy(fuel.getEnergy() - machine.getFuelDeficiency());
            System.out.println("AFTER: " + machine.getFuels().stream().mapToInt(PlayerFuel::getEnergy).sum());
            return;
        }


        AtomicInteger energySum = new AtomicInteger();
        Map<Integer, PlayerFuel> fuelCanRemoveFuelMap = new HashMap<>();
        machine.getFuels().forEach(fuel -> {
            energySum.addAndGet(fuel.getEnergy());
            fuelCanRemoveFuelMap.put(fuel.getEnergy(), fuel);
            if (energySum.get() >= machine.getFuelDeficiency()) {
                for (Map.Entry<Integer, PlayerFuel> fuelEntry : fuelCanRemoveFuelMap.entrySet()) {
                    fuelEntry.getValue().setEnergy(fuelEntry.getKey() - Math.max(fuelEntry.getKey(), machine.fuelDeficiency));
                    if (fuelEntry.getValue().getEnergy() <= 0) {
                        machine.getFuels().remove(fuelEntry.getValue());
                    }
                }
            }
        });
    }

    protected void insertResources() {
        HashMap<Material, ItemStack> resourcesGained = machine.getResourcesGained();
        AtomicReference<Double> totalAmount = new AtomicReference<>((double) 0);
        itemsGained.forEach(
                item -> {
                    totalAmount.updateAndGet(v -> v + item.getAmount());
                    if (resourcesGained.containsKey(item.getType())) {
                        ItemStack prev = resourcesGained.get(item.getType());
                        prev.setAmount(prev.getAmount() + item.getAmount());
                        return;
                    }
                    resourcesGained.put(item.getType(), item.clone());
                }
        );
        machine.setTotalResourcesGained(machine.getTotalResourcesGained() + totalAmount.get());
    }

    /**
     * Uses MaterialChance class to define materials given for a specific chance, in a specific range
     */
    private void initializeMaterialChances() {
        materialChances = new RandomCollection<Chance>();
        addZenCoinChances();
        switch (machine.machineType) {
            case LUMBERJACK:
                addLumberjackMaterialChances();
                break;
            case EXCAVATOR:
                addExcavatorMaterialChances();
                break;
            case MINER:
                addMinerMaterialChances();
                break;
            case FARMER:
                addFarmerMaterialChances();
                break;
            case ALL:
                addMinerMaterialChances();
                addFarmerMaterialChances();
                addExcavatorMaterialChances();
                addLumberjackMaterialChances();
                break;
        }
    }

    private void addZenCoinChances() {
        materialChances.add(1, new ZenCoinChance(10, 50));
    }

    private void addMinerMaterialChances() {
        materialChances.add(3, new MaterialChance(Material.DIAMOND, (int) Math.floor(2 * lootAmplifier), (int) Math.floor(3 * lootAmplifier)));
        materialChances.add(5, new MaterialChance(Material.DIAMOND_ORE, (int) Math.floor(1 * lootAmplifier), (int) Math.floor(2 * lootAmplifier)));
        materialChances.add(10, new MaterialChance(Material.GOLD_ORE, (int) Math.floor(3 * lootAmplifier), (int) Math.floor(5 * lootAmplifier)));
        materialChances.add(30, new MaterialChance(Material.IRON_ORE, (int) Math.floor(5 * lootAmplifier), (int) Math.floor(10 * lootAmplifier)));
        materialChances.add(50, new MaterialChance((int) Math.floor(3 * lootAmplifier), (int) Math.floor(8 * lootAmplifier), Material.COAL, Material.IRON_INGOT));
        materialChances.add(90, new MaterialChance((int) Math.floor(5 * lootAmplifier), (int) Math.floor(15 * lootAmplifier), Material.COBBLESTONE, Material.STONE));
    }

    private void addFarmerMaterialChances() {
        materialChances.add(100, new MaterialChance(new ArrayList<>(Tag.CROPS.getValues()), (int) Math.floor(3 * lootAmplifier), (int) Math.floor(10 * lootAmplifier)));
    }

    private void addExcavatorMaterialChances() {
        materialChances.add(1, new MaterialChance(Material.GRASS_BLOCK, (int) Math.floor(5 * lootAmplifier), (int) Math.floor(7 * lootAmplifier)));
        materialChances.add(3, new MaterialChance(Material.DIRT, (int) Math.floor(8 * lootAmplifier), (int) Math.floor(10 * lootAmplifier)));
    }

    private void addLumberjackMaterialChances() {
        MaterialChance chance = new MaterialChance((int) Math.floor(5 * lootAmplifier), (int) Math.floor(10 * lootAmplifier));
        chance.getMaterials().addAll(Tag.LOGS.getValues());
        materialChances.add(100, chance);
    }

    public RandomCollection<Chance> getMaterialChances() {
        return materialChances;
    }

    public double getLootAmplifier() {
        return lootAmplifier;
    }

    public void setLootAmplifier(double lootAmplifier) {
        this.lootAmplifier = lootAmplifier;
    }

    public List<ItemStack> getItemsGained() {
        return itemsGained;
    }

    public void setItemsGained(List<ItemStack> itemsGained) {
        this.itemsGained = itemsGained;
    }

    public PlayerMachine getMachine() {
        return machine;
    }

    public void setMachine(PlayerMachine machine) {
        this.machine = machine;
    }

    public int getMinePeriod() {
        return minePeriod;
    }

    public void setMinePeriod(int minePeriod) {
        this.minePeriod = minePeriod;
    }

}
