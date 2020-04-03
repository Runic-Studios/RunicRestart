package com.runicrealms.runicrestart;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashSet;
import java.util.Set;

public class Plugin extends JavaPlugin {

    public static Set<BukkitTask> tasks = new HashSet<BukkitTask>();
    public static int finish;
    public static int passed;
    public static BukkitTask counter;
    public static BukkitTask buffer;

    private static Plugin instance;

    @Override
    public void onEnable() {
        instance = this;
        this.getConfig().options().copyDefaults(true);
        this.saveDefaultConfig();
        Bukkit.getPluginCommand("runicrestart").setExecutor(new RestartCommand());
        if (this.getConfig().getInt("restart-buffer") >= 0) {
            buffer = Bukkit.getScheduler().runTaskLater(this, new Runnable() {
                @Override
                public void run() {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "runicrestart " + (Plugin.getInstance().getConfig().getInt("restart-duration")));
                    Plugin.buffer = null;
                }
            }, 20L * 60L * (this.getConfig().getInt("restart-buffer")));
        }
    }

    @Override
    public void onDisable() {
        for (BukkitTask task : tasks) {
            if (task.isCancelled() == false){
                task.cancel();
            }
        }
        if (counter != null) {
            if (!counter.isCancelled()) {
                counter.cancel();
            }
        }
        if (buffer != null) {
            if (!buffer.isCancelled()) {
                buffer.cancel();
            }
        }
        tasks = null;
        counter = null;
        buffer = null;
    }

    public static Plugin getInstance() {
        return instance;
    }

}
