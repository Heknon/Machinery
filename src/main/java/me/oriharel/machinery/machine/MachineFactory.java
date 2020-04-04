package me.oriharel.machinery.machine;

import com.sun.javaws.exceptions.InvalidArgumentException;
import me.oriharel.machinery.exceptions.MachineNotFoundException;

import javax.annotation.Nullable;
import java.io.IOException;

class MachineFactory {
    @Nullable
    public Machine createMachine(String machineName, MachineType machineType) throws InvalidArgumentException {
        if (machineType == null) throw new InvalidArgumentException(new String[]{"Machine type must not be null (MachineFactory)"});
        Machine machine = null;
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
