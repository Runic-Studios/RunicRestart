package com.runicrealms.plugin.runicrestart.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PluginsReadyEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}