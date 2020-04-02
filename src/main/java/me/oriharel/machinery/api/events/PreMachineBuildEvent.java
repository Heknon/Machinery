package me.oriharel.machinery.api.events;

import me.oriharel.machinery.machine.Machine;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class PreMachineBuildEvent extends MachineEvent implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();

    private boolean cancelled;

    public PreMachineBuildEvent(Machine machine) {
        super(machine, buildLocation);
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public HandlerList getHandlers() {
        return HANDLERS;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }
}
