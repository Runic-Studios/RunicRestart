package com.runicrealms.plugin.runicrestart.api;

import com.runicrealms.plugin.runicrestart.event.PreShutdownEvent;

import java.util.List;

public interface RunicRestartApi {

    /**
     * Begins the server shutdown process
     */
    void beginShutdown();

    /**
     * ?
     *
     * @return
     */
    List<String> getPluginsToLoad();

    /**
     * @return the message sent when players are kicked for shutdown
     */
    String getShutdownMessage();

    /**
     * @return true if the server is currently shutting down
     */
    boolean isShuttingDown();

    /**
     * @param key of the plugin "core"
     */
    void markPluginLoaded(String key);

    /**
     * Used by other plugins as a handshake to confirm their data has been written to database
     *
     * @param event that triggered shutdown
     * @param key   of the plugin "core"
     * @return true if the plugin has saved
     */
    boolean markPluginSaved(PreShutdownEvent event, String key);
}
