package me.oriharel.machinery.machine;

import me.oriharel.machinery.items.Fuel;
import me.oriharel.machinery.items.MachineProduce;

import java.util.List;

public interface IMachine {

  double getCost();

  List<Fuel> getFuel();

  Fuel getFuelPerUse();

  MachineType getType();

  List<MachineProduce> getTotalResourcesGained();

  List<MachineProduce> run();

  boolean build();

  Structure getStructure();
}
