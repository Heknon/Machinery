package me.oriharel.machinery.machine;

import me.oriharel.machinery.exceptions.NotMachineTypeException;

public enum MachineType {
    LUMBERJACK,
    EXCAVATOR,
    MINER,
    FARMER,
    ALL;

    static MachineType getMachine(String machineType) throws NotMachineTypeException {
        if (machineType == null) throw new NotMachineTypeException("You must supply a string which is not null");
        switch (machineType.toUpperCase()) {
            case "LUMBERJACK":
                return MachineType.LUMBERJACK;
            case "EXCAVATOR":
                return MachineType.EXCAVATOR;
            case "MINER":
                return MachineType.MINER;
            case "FARMER":
                return MachineType.FARMER;
            case "ALL":
                return MachineType.ALL;
            default:
                throw new NotMachineTypeException("The given machine type string is not a machine type. (" + machineType + ")");
        }
    }

    public String toTitle() {
        switch (this) {
            case LUMBERJACK:
                return "Lumberjack";
            case EXCAVATOR:
                return "Excavator";
            case MINER:
                return "Miner";
            case FARMER:
                return "Farmer";
            case ALL:
                return "All Miner";
        }
        return "Miner";
    }
}
