package com.runicrealms.plugin.runicrestart;

import com.runicrealms.plugin.runicrestart.api.RunicRestartApi;
import com.runicrealms.plugin.runicrestart.event.PluginLoadedEvent;
import com.runicrealms.plugin.runicrestart.event.PluginsReadyEvent;
import com.runicrealms.plugin.runicrestart.event.PreShutdownEvent;
import io.lumine.mythic.bukkit.MythicBukkit;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.jetbrains.annotations.NotNull;

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
        MythicBukkit.inst().getMobManager().despawnAllMobs();
        // Trigger pre shutdown
        PreShutdownEvent preShutdownEvent = new PreShutdownEvent();
        Bukkit.getPluginManager().callEvent(preShutdownEvent);
        // Clear all mobs
        MythicBukkit.inst().getMobManager().despawnAllMobs();
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
        if (RunicRestart.pluginsToLoad.size() == 0) {
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
    private void onServerJoin(AsyncPlayerPreLoginEvent event) {
        if (IS_SHUTTING_DOWN) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "Server shutting down");
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        if (!event.getPlayer().isOp()) {
            return;
        }

        this.stopLogic(event, event.getMessage(), event.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onServerCommand(ServerCommandEvent event) {
        this.stopLogic(event, event.getCommand(), event.getSender());
    }

    private void stopLogic(@NotNull Cancellable cancellable, @NotNull String rawMessage, @NotNull CommandSender sender) {
        String message = rawMessage.split(" ")[0];

        if (message.length() <= 1) {
            return;
        }

        String command = message.substring(cancellable instanceof PlayerCommandPreprocessEvent ? 1 : 0);

        if (!command.equalsIgnoreCase("stop")) {
            return;
        }

        cancellable.setCancelled(true);
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aShutting server down safely..."));
        this.beginShutdown();
    }
}
