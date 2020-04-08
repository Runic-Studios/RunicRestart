package com.runicrealms.runicrestart.api;

import com.runicrealms.runicrestart.Plugin;
import org.bukkit.Bukkit;

public class RunicRestartApi {

    public static void markPluginLoaded(String key) {
        Plugin.pluginsToLoad.remove(key);
        if (Plugin.pluginsToLoad.size() == 0) {
            Bukkit.setWhitelist(false);
        }
    }

    public static void markPluginSaved(String key) {
        Plugin.pluginsToSave.remove(key);
        if (Plugin.pluginsToSave.size() == 0) {
            Bukkit.shutdown();
        }
    }

}
