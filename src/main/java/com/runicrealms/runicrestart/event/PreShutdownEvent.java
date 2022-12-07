package com.runicrealms.runicrestart.event;

import com.runicrealms.runicrestart.RunicRestart;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.List;

/**
 * Called when the server first intends to shut down. Allows all plugins to safely
 * save their data before the actual shut down event is called
 *
 * @author Skyfallin
 */
public final class PreShutdownEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    /*
    Keeps track of a list of plugins which must save and mark completed
    before the server can shut down
     */
    private final List<String> pluginsToSave;
    private boolean isCancelled = false;

    public PreShutdownEvent() {
        pluginsToSave = RunicRestart.getInstance().getConfig().getStringList("plugins-to-save");
    }

    /**
     * Gets a list of handlers handling this event.
     *
     * @return A list of handlers handling this event.
     */
    public static HandlerList getHandlerList() {
        return handlers;
    }

    /**
     * Gets a list of handlers handling this event.
     *
     * @return A list of handlers handling this event.
     */
    @Override
    public final HandlerList getHandlers() {
        return handlers;
    }

    public List<String> getPluginsToSave() {
        return pluginsToSave;
    }

    /**
     * Gets if this event is cancelled.
     *
     * @return If this event is cancelled
     */
    public boolean isCancelled() {
        return isCancelled;
    }

    /**
     * Sets if this event should be cancelled.
     *
     * @param cancel If this event should be cancelled.
     */
    public void setCancelled(final boolean cancel) {
        this.isCancelled = cancel;
    }

    /**
     * Used across our array of plugins to give a 'handshake' to RunicRestart when it is ready for shutdown
     *
     * @param key defined in the RunicRestart config.yml, a simple string key to identify a plugin, e.g. "guilds"
     * @return true if all plugins are ready for shutdown
     */
    public boolean markPluginSaved(String key) {
        return RunicRestart.getAPI().markPluginSaved(this, key);
    }
}
