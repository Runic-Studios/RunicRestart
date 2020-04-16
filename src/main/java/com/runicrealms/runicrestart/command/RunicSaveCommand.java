package com.runicrealms.runicrestart.command;

import com.runicrealms.runicrestart.Plugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RunicSaveCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.isOp()) {
            Plugin.shouldShutdown = false;
            Plugin.startShutdown();
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aSaving and kicking players..."));
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.kickPlayer(ChatColor.GREEN + "Server restarting, we'll be back up soon!");
            }
        } else {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou don't have permission to use that command!"));
        }
        return true;
    }

}
