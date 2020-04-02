package me.oriharel.machinery.machine;

import me.oriharel.machinery.Machinery;

import java.util.ArrayList;
import java.util.List;

public class MachineManager {
  private final Machinery machinery;
  private MachineFactory machineFactory;
  private List<IMachine> machines;

  public MachineManager(Machinery machinery) {
    this.machinery = machinery;
    this.machineFactory = new MachineFactory();
    this.machines = new ArrayList<IMachine>();
  }

  public void addMachine(IMachine machine) {
    machines.add(machine);
  }

  public void removeMachine(IMachine machine) {
    machines.remove(machine);
  }

  public List<IMachine> getMachines() {
    return machines;
  }

  public MachineFactory getMachineFactory() {
    return machineFactory;
  }
}
