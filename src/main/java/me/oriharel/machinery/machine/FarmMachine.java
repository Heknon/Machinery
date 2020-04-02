package me.oriharel.machinery.machine;

import me.oriharel.machinery.items.Fuel;
import me.oriharel.machinery.items.MachineProduce;

import java.util.List;

class FarmMachine implements IMachine {

  private final Fuel fuelPerUse;
  private List<Fuel> fuel;
  private List<MachineProduce> totalResourcesGained;

  public FarmMachine(List<Fuel> fuel, List<MachineProduce> totalResourcesGained, Fuel fuelPerUse) {

    this.fuel = fuel;
    this.totalResourcesGained = totalResourcesGained;
    this.fuelPerUse = fuelPerUse;
  }

  @Override
  public Structure getStructure() {
    return null;
  }

  @Override
  public List<Fuel> getFuel() {
    return fuel;
  }

  @Override
  public double getCost() {
    return 0;
  }

  @Override
  public Fuel getFuelPerUse() {
    return fuelPerUse;
  }

  @Override
  public MachineType getType() {
    return MachineType.FARMER;
  }

  @Override
  public List<MachineProduce> getTotalResourcesGained() {
    return totalResourcesGained;
  }

  @Override
  public List<MachineProduce> run() {
    return null;
  }

  @Override
  public boolean build() {
    return false;
  }
}
