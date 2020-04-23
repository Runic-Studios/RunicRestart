package com.runicrealms.runicrestart;

import com.runicrealms.runicrestart.api.ServerShutdownEvent;
import com.runicrealms.runicrestart.command.RunicRestartCommand;
import com.runicrealms.runicrestart.command.RunicSaveCommand;
import com.runicrealms.runicrestart.command.RunicStopCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Plugin extends JavaPlugin implements Listener {

    public static Set<BukkitTask> tasks = new HashSet<BukkitTask>();
    public static int finish;
    public static int passed;
    public static BukkitTask counter;
    public static BukkitTask buffer;

    private static Plugin instance;

    public static List<String> pluginsToLoad;
    public static List<String> pluginsToSave;
    public static boolean hasWhitelist;
    public static boolean shouldShutdown = true;

    @Override
    public void onEnable() {
        instance = this;
        this.getConfig().options().copyDefaults(true);
        this.saveDefaultConfig();
        hasWhitelist = new Boolean(Bukkit.hasWhitelist());
        Bukkit.setWhitelist(true);
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.kickPlayer("The server is still loading!");
        }
        pluginsToLoad = this.getConfig().getStringList("plugins-to-load");
        pluginsToSave = this.getConfig().getStringList("plugins-to-save");
        Bukkit.getPluginCommand("runicrestart").setExecutor(new RunicRestartCommand());
        Bukkit.getPluginCommand("runicstop").setExecutor(new RunicStopCommand());
        Bukkit.getPluginCommand("rstop").setExecutor(new RunicStopCommand());
        Bukkit.getPluginCommand("runicsave").setExecutor(new RunicSaveCommand());
        Bukkit.getPluginCommand("rsave").setExecutor(new RunicSaveCommand());
        Bukkit.getPluginManager().registerEvents(this, this);
        if (this.getConfig().getInt("restart-buffer") >= 0) {
            buffer = Bukkit.getScheduler().runTaskLater(this, new Runnable() {
                @Override
                public void run() {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "runicrestart " + (Plugin.getInstance().getConfig().getInt("restart-duration")));
                    Plugin.buffer = null;
                }
            }, 20L * 60L * (this.getConfig().getInt("restart-buffer")));
        }
        for (String key : this.getConfig().getConfigurationSection("recurring-messages").getKeys(false)) {
            Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
                @Override
                public void run() {
                    List<String> messages = getConfig().getStringList("recurring-messages." + key + ".messages");
                    Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', messages.get((int) Math.floor(Math.random() * messages.size()))));
                }
            }, 20L * this.getConfig().getInt("recurring-messages." + key + ".delay"), 20L *  this.getConfig().getInt("recurring-messages." + key + ".interval"));
        }
    }

    @EventHandler
    public void onShutdown(ServerShutdownEvent event) {
        try {
            for (BukkitTask task : tasks) {
                if (task.isCancelled() == false) {
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
        } catch (Exception exception) {}
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (pluginsToLoad.size() > 0) {
            event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&cERROR - you have joined before the runic realms plugins have loaded their data! PLEASE RELOG TO AVOID ISSUES!"));
        }
    }

    public static Plugin getInstance() {
        return instance;
    }

    public static void startShutdown() {
        Bukkit.getPluginManager().callEvent(new ServerShutdownEvent());
        Bukkit.getScheduler().runTaskLater(getInstance(), new Runnable() {
            @Override
            public void run() {
                if (shouldShutdown) {
                    Bukkit.shutdown();
                }
            }
        }, 20 * 10);
    }

}
