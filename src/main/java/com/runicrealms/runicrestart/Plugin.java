package com.runicrealms.runicrestart;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public class Plugin extends JavaPlugin {

    public static BukkitTask countdown;
    public static int finish;
    public static int passed;


    private static Plugin instance;

    @Override
    public void onEnable() {
        instance = this;
    }

    @Override
    public void onDisable() {
        if (countdown != null) {
            countdown.cancel();
            countdown = null;
        }
    }

    public static Plugin getInstance() {
        return instance;
    }

}
