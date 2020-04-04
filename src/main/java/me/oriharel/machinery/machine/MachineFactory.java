package me.oriharel.machinery.machine;

import me.oriharel.machinery.exceptions.MachineNotFoundException;

import javax.annotation.Nullable;
import java.io.IOException;

class MachineFactory {
    @Nullable
    public IMachine createMachine(String machineName, MachineType machineType) {
        IMachine machine = null;
        try {
            switch (machineType) {
                case LUMBERJACK:
                    machine = new LumberjackMachine(machineName);
                    break;
                case EXCAVATOR:
                    machine = new ExcavatorMachine(machineName);
                    break;
                case MINER:
                    machine = new MineMachine(machineName);
                    break;
                case FARMER:
                    machine = new FarmMachine(machineName);
                    break;
            }
        } catch (IOException | MachineNotFoundException e) {
            e.printStackTrace();
        }
        return machine;
    }
}
