package com.runicrealms.runicrestart.command;

import com.runicrealms.runicrestart.api.ServerShutdownEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RunicStopCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.isOp()) {
            Bukkit.getPluginManager().callEvent(new ServerShutdownEvent());
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aShutting server down safely..."));
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.kickPlayer(ChatColor.GREEN + "Server restarting, we'll be back up soon!");
            }
        } else {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou don't have permission to use that command!"));
        }
        return true;
    }

}
