package com.runicrealms.plugin.runicrestart.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PluginLoadedEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private String plugin;

    public PluginLoadedEvent(String plugin) {
        this.plugin = plugin;
    }

    public String getPlugin() {
        return this.plugin;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
