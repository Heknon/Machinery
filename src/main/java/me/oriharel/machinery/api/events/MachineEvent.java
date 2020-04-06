package me.oriharel.machinery.api.events;

import me.oriharel.machinery.machine.Machine;
import org.bukkit.Location;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MachineEvent<T extends Machine> extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private final T machine;
    private final Location buildLocation;

    public MachineEvent(T machine, Location buildLocation) {
        this.machine = machine;
        this.buildLocation = buildLocation;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public T getMachine() {
        return machine;
    }

    public Location getBuildLocation() {
        return buildLocation;
    }
}
