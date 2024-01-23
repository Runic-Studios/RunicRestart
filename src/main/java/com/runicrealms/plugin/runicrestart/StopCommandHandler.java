package com.runicrealms.plugin.runicrestart;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.jetbrains.annotations.NotNull;

public class StopCommandHandler implements Listener {

    public StopCommandHandler() {
        Bukkit.getPluginManager().registerEvents(this, RunicRestart.getInstance());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        if (!event.getPlayer().isOp()) {
            return;
        }

        commandLogic(event, event.getMessage(), event.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onServerCommand(ServerCommandEvent event) {
        commandLogic(event, event.getCommand(), event.getSender());
    }

    private void commandLogic(@NotNull Cancellable cancellable, @NotNull String rawMessage, @NotNull CommandSender sender) {
        String message = rawMessage.split(" ")[0];

        if (message.length() <= 1) {
            return;
        }

        String command = message.substring(cancellable instanceof PlayerCommandPreprocessEvent ? 1 : 0);

        if (command.equalsIgnoreCase("reload")) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cReloading plugins is disabled. Please use /rstop."));
            cancellable.setCancelled(true);
            return;
        }

        if (!command.equalsIgnoreCase("stop")) {
            return;
        }

        cancellable.setCancelled(true);
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aShutting server down safely..."));
    }
}
