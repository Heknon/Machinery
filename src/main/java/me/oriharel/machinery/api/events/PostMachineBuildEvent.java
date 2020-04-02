package me.oriharel.machinery.api.events;

import me.oriharel.machinery.machine.IMachine;
import org.bukkit.event.HandlerList;

public class PostMachineBuildEvent extends MachineEvent {
    private static final HandlerList HANDLERS = new HandlerList();

    public PostMachineBuildEvent(IMachine machine) {
        super(machine, buildLocation);
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public HandlerList getHandlers() {
        return HANDLERS;
    }
}
