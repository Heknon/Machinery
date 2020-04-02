package me.oriharel.machinery.machine;

import javax.annotation.Nullable;

class MachineFactory {
  @Nullable
  public IMachine createMachine(String machineName, MachineType machineType) {
    IMachine machine = null;
    switch (machineType) {
      case LUMBERJACK:
        machine = new LumberjackMachine();
        break;
      case EXCAVATOR:
        machine = new ExcavatorMachine();
        break;
      case MINER:
        machine = new MineMachine(fuel, totalResourcesGained, fuelPerUse);
        break;
      case FARMER:
        machine = new FarmMachine();
        break;
    }
    return machine;
  }
}
