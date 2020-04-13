package com.runicrealms.runicrestart.api;

import com.runicrealms.runicrestart.Plugin;
import org.bukkit.Bukkit;

import java.util.logging.Level;

public class RunicRestartApi {

    public static void markPluginLoaded(String key) {
        Plugin.pluginsToLoad.remove(key);
        Bukkit.getLogger().log(Level.INFO, "[RunicRestart] " + key + " confirmed startup");
        if (Plugin.pluginsToLoad.size() == 0) {
            if (Plugin.hasWhitelist == false) {
                Bukkit.setWhitelist(false);
            }
        }
    }

    public static void markPluginSaved(String key) {
        Plugin.pluginsToSave.remove(key);
        Bukkit.getLogger().log(Level.INFO, "[RunicRestart] " + key + " confirmed shutdown");
        if (Plugin.pluginsToSave.size() == 0) {
            Bukkit.shutdown();
        }
    }

}
