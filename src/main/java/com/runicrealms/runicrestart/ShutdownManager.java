package com.runicrealms.runicrestart;

import com.runicrealms.runicrestart.api.RunicRestartApi;
import com.runicrealms.runicrestart.event.PluginLoadedEvent;
import com.runicrealms.runicrestart.event.PluginsReadyEvent;
import com.runicrealms.runicrestart.event.PreShutdownEvent;
import io.lumine.xikage.mythicmobs.MythicMobs;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.util.List;
import java.util.logging.Level;

public class ShutdownManager implements Listener, RunicRestartApi {

    private static boolean IS_SHUTTING_DOWN;

    public ShutdownManager() {
        IS_SHUTTING_DOWN = false;
        Bukkit.getPluginManager().registerEvents(this, RunicRestart.getInstance());
    }

    /**
     * @param isShuttingDown whether the server is currently shutting down
     */
    public static void setIsShuttingDown(boolean isShuttingDown) {
        IS_SHUTTING_DOWN = isShuttingDown;
    }

    @Override
    public void beginShutdown() {
        ShutdownManager.setIsShuttingDown(true);
        // Trigger redis save by kicking all players
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.kickPlayer(RunicRestart.getAPI().getShutdownMessage());
        }
        MythicMobs.inst().getMobManager().despawnAllMobs();
        // Trigger pre shutdown
        PreShutdownEvent preShutdownEvent = new PreShutdownEvent();
        Bukkit.getPluginManager().callEvent(preShutdownEvent);
        // Clear all mobs
        MythicMobs.inst().getMobManager().despawnAllMobs();
        // TODO: call this proper shutdown event in the correct place?
//        Bukkit.getPluginManager().callEvent(new ServerShutdownEvent());
//        Bukkit.getScheduler().runTaskLater(getInstance(), () -> {
//            if (shouldShutdown) {
//                Bukkit.shutdown();
//            }
//        }, 20 * 10);
    }

    @Override
    public List<String> getPluginsToLoad() {
        return RunicRestart.pluginsToLoad;
    }

    @Override
    public String getShutdownMessage() {
        return ChatColor.GREEN + "Server restart in progress! We'll be back soon.";
    }

    @Override
    public boolean isShuttingDown() {
        return IS_SHUTTING_DOWN;
    }

    @Override
    public void markPluginLoaded(String key) {
        RunicRestart.pluginsToLoad.remove(key);
        Bukkit.getLogger().log(Level.INFO, "[RunicRestart] " + key + " confirmed startup");
        if (!Bukkit.isPrimaryThread()) {
            Bukkit.getScheduler().runTask(RunicRestart.getInstance(), () -> Bukkit.getPluginManager().callEvent(new PluginLoadedEvent(key)));
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

    @Override
    public boolean markPluginSaved(PreShutdownEvent event, String key) {
        List<String> pluginsToSave = event.getPluginsToSave();
        pluginsToSave.remove(key);
        Bukkit.getLogger().log(Level.INFO, "[RunicRestart]: " + key + " confirmed shutdown. " + pluginsToSave.size() + " plugin(s) remaining.");
        if (pluginsToSave.size() == 0) {
            if (RunicRestart.shouldShutdown) {
                // todo: call shutdown event instead?
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

    @EventHandler
    public void onServerJoin(AsyncPlayerPreLoginEvent event) {
        if (IS_SHUTTING_DOWN) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "Server shutting down");
        }
    }
}
