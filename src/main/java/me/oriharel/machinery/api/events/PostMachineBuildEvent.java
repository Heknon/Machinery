package me.oriharel.machinery.api.events;

import me.oriharel.machinery.machine.PlayerMachine;
import org.bukkit.Location;
import org.bukkit.event.HandlerList;

public class PostMachineBuildEvent extends MachineEvent<PlayerMachine> {
    private static final HandlerList HANDLERS = new HandlerList();

    public PostMachineBuildEvent(PlayerMachine machine, Location loc) {
        super(machine, loc);
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public HandlerList getHandlers() {
        return HANDLERS;
    }
}
