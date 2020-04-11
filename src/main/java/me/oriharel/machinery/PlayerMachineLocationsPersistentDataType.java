package me.oriharel.machinery;

import org.bukkit.Location;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;

public class PlayerMachineLocationsPersistentDataType implements PersistentDataType<Long[], Location[]> {

    public PlayerMachineLocationsPersistentDataType() {

    }

    @Override
    public Class<Long[]> getPrimitiveType() {
        return Long[].class;
    }

    @Override
    public Class<Location[]> getComplexType() {
        return Location[].class;
    }

    @Override
    public Long[] toPrimitive(Location[] locations, PersistentDataAdapterContext persistentDataAdapterContext) {
        return Arrays.stream(locations).map(Utils::locationToLong).toArray(Long[]::new);
    }

    @Override
    public Location[] fromPrimitive(Long[] longs, PersistentDataAdapterContext persistentDataAdapterContext) {
        return Arrays.stream(longs).map(Utils::longToLocation).toArray(Location[]::new);
    }
}
