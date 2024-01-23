package com.runicrealms.plugin.runicrestart;

import com.runicrealms.plugin.common.event.RunicLoadedEvent;
import com.runicrealms.plugin.common.event.RunicShutdownEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

public class WhitelistHandler implements Listener {

    private final boolean wasWhitelistEnabled;
    private boolean hasLoaded = false;
    private boolean isShuttingDown = false;

    public WhitelistHandler() {
        Bukkit.getPluginManager().registerEvents(this, RunicRestart.getInstance());
        wasWhitelistEnabled = Bukkit.hasWhitelist();
        Bukkit.setWhitelist(true);
    }

    @EventHandler
    public void onLoaded(RunicLoadedEvent event) {
        Bukkit.setWhitelist(wasWhitelistEnabled);
        hasLoaded = true;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onShutdown(RunicShutdownEvent event) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.kickPlayer(ChatColor.GREEN + "Server restart in progress! We'll be back soon.");
        }
        Bukkit.setWhitelist(true);
        isShuttingDown = true;
    }

    @EventHandler
    public void onPreLogin(AsyncPlayerPreLoginEvent event) {
        if (!hasLoaded) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, ChatColor.YELLOW + "Server is still loading! Reconnect in a few seconds.");
        } else if (isShuttingDown) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, ChatColor.YELLOW + "Server is shutting down!");
        }
    }

    public void onDisable() {
        Bukkit.setWhitelist(wasWhitelistEnabled);
    }

}
