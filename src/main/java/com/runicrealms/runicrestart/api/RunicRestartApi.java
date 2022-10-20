package com.runicrealms.runicrestart.api;

import com.runicrealms.runicrestart.RunicRestart;
import com.runicrealms.runicrestart.event.PluginLoadedEvent;
import com.runicrealms.runicrestart.event.PluginsReadyEvent;
import org.bukkit.Bukkit;

import java.util.logging.Level;

public class RunicRestartApi {

    public static void markPluginLoaded(String key) {
        RunicRestart.pluginsToLoad.remove(key);
        Bukkit.getLogger().log(Level.INFO, "[RunicRestart] " + key + " confirmed startup");
        if (!Bukkit.isPrimaryThread()) {
            Bukkit.getScheduler().runTask(RunicRestart.getInstance(), () -> {
                Bukkit.getPluginManager().callEvent(new PluginLoadedEvent(key));
            });
        } else {
            Bukkit.getPluginManager().callEvent(new PluginLoadedEvent(key));
        }
        if (RunicRestart.pluginsToLoad.size() <= 0) {
            if (!RunicRestart.hasWhitelist) {
                if (!Bukkit.isPrimaryThread()) {
                    Bukkit.getScheduler().runTask(RunicRestart.getInstance(), () -> {
                        Bukkit.setWhitelist(false);
                        Bukkit.getPluginManager().callEvent(new PluginsReadyEvent());
                    });
                } else {
                    Bukkit.setWhitelist(false);
                    Bukkit.getPluginManager().callEvent(new PluginsReadyEvent());
                }
            }
        }
    }
}
