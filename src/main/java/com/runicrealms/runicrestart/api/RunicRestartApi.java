package com.runicrealms.runicrestart.api;

import com.runicrealms.runicrestart.Plugin;
import org.bukkit.Bukkit;

import java.util.logging.Level;

public class RunicRestartApi {

    public static void markPluginLoaded(String key) {
        Plugin.pluginsToLoad.remove(key);
        Bukkit.getLogger().log(Level.INFO, "[RunicRestart] " + key + " confirmed startup");
        if (!Bukkit.isPrimaryThread()) {
            Bukkit.getScheduler().runTask(Plugin.getInstance(), () -> {
                Bukkit.getPluginManager().callEvent(new PluginLoadedEvent(key));
            });
        } else {
            Bukkit.getPluginManager().callEvent(new PluginLoadedEvent(key));
        }
        if (Plugin.pluginsToLoad.size() <= 0) {
            if (!Plugin.hasWhitelist) {
                if (!Bukkit.isPrimaryThread()) {
                    Bukkit.getScheduler().runTask(Plugin.getInstance(), () -> {
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

    public static void markPluginSaved(String key) {
        Plugin.pluginsToSave.remove(key);
        Bukkit.getLogger().log(Level.INFO, "[RunicRestart] " + key + " confirmed shutdown");
        if (Plugin.pluginsToSave.size() == 0) {
            if (Plugin.shouldShutdown) {
                if (!Bukkit.isPrimaryThread()) {
                    Bukkit.getScheduler().runTask(Plugin.getInstance(), Bukkit::shutdown);
                } else {
                    Bukkit.shutdown();
                }
            } else {
                Bukkit.getLogger().log(Level.INFO, "[RunicRestart] All plugins have confirmed shutdown! You are free to use console shutdown.");
            }
        }
    }

}
