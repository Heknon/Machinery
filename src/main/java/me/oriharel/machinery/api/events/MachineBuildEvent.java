package me.oriharel.machinery.api.events;

import me.oriharel.machinery.machine.IMachine;
import org.bukkit.event.HandlerList;

public class MachineBuildEvent extends MachineEvent {
  private static final HandlerList HANDLERS = new HandlerList();

  public MachineBuildEvent(IMachine machine) {
    super(machine);
  }

  public static HandlerList getHandlerList() {
    return HANDLERS;
  }

  public HandlerList getHandlers() {
    return HANDLERS;
  }
}
