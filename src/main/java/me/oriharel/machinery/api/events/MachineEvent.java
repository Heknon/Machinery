package me.oriharel.machinery.api.events;

import me.oriharel.machinery.machine.IMachine;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MachineEvent extends Event implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();
    private final IMachine machine;
    private boolean cancelled;

    public MachineEvent(IMachine machine) {
        this.machine = machine;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public IMachine getMachine() {
        return machine;
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
