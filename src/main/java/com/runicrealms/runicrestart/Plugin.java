package com.runicrealms.runicrestart;

import org.bukkit.Bukkit;
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
        Bukkit.getPluginCommand("runicrestart").setExecutor(new RestartCommand());
        Bukkit.getScheduler().runTaskLater(this, new Runnable() {
            @Override
            public void run() {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "runicrestart 10");
            }
        }, 20L * 60L * 110L);
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
