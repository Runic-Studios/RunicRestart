package com.runicrealms.runicrestart.event;

import com.runicrealms.runicrestart.RunicRestart;
import org.bukkit.Bukkit;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.List;
import java.util.logging.Level;

/**
 * Called when the server first intends to shut down. Allows all plugins to safely
 * save their data before the actual shut down event is called
 *
 * @author Skyfallin
 */
public final class PreShutdownEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean isCancelled = false;
    /*
    Keeps track of a list of plugins which must save and mark completed
    before the server can shut down
     */
    private final List<String> pluginsToSave;

    public PreShutdownEvent() {
        pluginsToSave = RunicRestart.getInstance().getConfig().getStringList("plugins-to-save");
    }

    /**
     * @param key
     * @return
     */
    public boolean markPluginSaved(String key) {
        this.pluginsToSave.remove(key);
        Bukkit.getLogger().log(Level.INFO, "[RunicRestart]: " + key + " confirmed shutdown");
        if (this.pluginsToSave.size() == 0) {
            if (RunicRestart.shouldShutdown) {
                // todo: call shutdown event instead
                if (!Bukkit.isPrimaryThread()) {
                    Bukkit.getScheduler().runTask(RunicRestart.getInstance(), Bukkit::shutdown);
                } else {
                    Bukkit.shutdown();
                }
            } else {
                Bukkit.getLogger().log(Level.INFO, "[RunicRestart] All plugins have confirmed shutdown! You are free to use console shutdown.");
            }
            return true;
        }
        return false;
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
     * Gets if this event is cancelled.
     *
     * @return If this event is cancelled
     */
    public boolean isCancelled() {
        return isCancelled;
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
}
