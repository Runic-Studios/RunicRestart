package com.runicrealms.runicrestart;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class RestartCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            if (isInt(args[0])) {
                Plugin.finish = Integer.parseInt(args[0]);
                Plugin.passed = 0;
                Plugin.countdown = Bukkit.getScheduler().runTaskTimer(Plugin.getInstance(), new Runnable() {
                    @Override
                    public void run() {
                        if (Plugin.passed < Plugin.finish) {
                            if (Plugin.passed % 10 == 0 || Plugin.passed <= 5) {
                                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&c[&4Restart&c] &2Server restarting in &a" + (Plugin.passed - Plugin.finish) + " &2minutes"));
                            }
                            Plugin.passed++;
                        } else {
                            Bukkit.shutdown();
                        }
                    }
                }, 0L, 20L * 60L);
            } else {
                sender.sendMessage(ChatColor.RED + "Command syntax is: /runicrestart <minutes>");
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Command syntax is: /runicrestart <minutes>");
        }
        return true;
    }

    private static boolean isInt(String number) {
        try {
            Integer.parseInt(number);
            return true;
        } catch (Exception exception) {
            return false;
        }
    }

}
