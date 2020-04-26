package com.runicrealms.runicrestart.command;

import com.runicrealms.runicrestart.Plugin;
import com.runicrealms.runicrestart.TipsManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ToggleTipsCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            if (TipsManager.getTips().contains((Player) sender)) {
                TipsManager.getTips().remove((Player) sender);
                sender.sendMessage(ChatColor.GREEN + "Disabled tips! Use /toggletips to enable them.");
                Bukkit.getScheduler().runTaskAsynchronously(Plugin.getInstance(), new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Plugin.getDataFileConfiguration().set("tips." + ((Player) sender).getUniqueId(), false);
                            Plugin.getDataFileConfiguration().save(Plugin.getDataFile());
                        } catch (Exception exception) {
                            exception.printStackTrace();
                        }
                    }
                });
            } else {
                TipsManager.getTips().add((Player) sender);
                sender.sendMessage(ChatColor.GREEN + "Enabled tips! Use /toggletips to disable them.");
                Bukkit.getScheduler().runTaskAsynchronously(Plugin.getInstance(), new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Plugin.getDataFileConfiguration().set("tips." + ((Player) sender).getUniqueId(), true);
                            Plugin.getDataFileConfiguration().save(Plugin.getDataFile());
                        } catch (Exception exception) {
                            exception.printStackTrace();
                        }
                    }
                });
            }
        } else {
            sender.sendMessage("You can't run this command from console!");
        }
        return true;
    }
}
