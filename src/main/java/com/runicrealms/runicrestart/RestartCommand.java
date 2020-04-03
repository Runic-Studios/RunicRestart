package com.runicrealms.runicrestart;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashSet;

public class RestartCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.isOp()) {
            if (args.length == 1) {
                if (isInt(args[0])) {
                    Plugin.finish = Integer.parseInt(args[0]);
                    Plugin.passed = 0;
                    Plugin.counter = Bukkit.getScheduler().runTaskTimer(Plugin.getInstance(), new Runnable() {
                        @Override
                        public void run() {
                            if (Plugin.passed < Plugin.finish) {
                                if ((Plugin.finish - Plugin.passed) % 5 == 0 || (Plugin.finish - Plugin.passed) <= 5) {
                                    Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&',
                                            Plugin.getInstance().getConfig().getString("restart-message-format")
                                                    .replaceAll("%time%", (Plugin.finish - Plugin.passed) + "")
                                                    .replaceAll("%unit%", "minute" + (Plugin.finish - Plugin.passed > 1 ? "s" : ""))));
                                }
                                if ((Plugin.finish - Plugin.passed == 1)) {
                                    Plugin.tasks.add(Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), new Runnable() {
                                        @Override
                                        public void run() {
                                            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&',
                                                    Plugin.getInstance().getConfig().getString("restart-message-format")
                                                            .replaceAll("%time%", "30")
                                                            .replaceAll("%unit%", "seconds")));
                                        }
                                    }, 20L * 30L));
                                    Plugin.tasks.add(Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), new Runnable() {
                                        @Override
                                        public void run() {
                                            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&',
                                                    Plugin.getInstance().getConfig().getString("restart-message-format")
                                                            .replaceAll("%time%", "10")
                                                            .replaceAll("%unit%", "seconds")));
                                        }
                                    }, 20L * 50L));
                                    for (int i = 0; i < 5; i++) {
                                        final int current = new Integer(i);
                                        Plugin.tasks.add(Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), new Runnable() {
                                            @Override
                                            public void run() {
                                                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&',
                                                        Plugin.getInstance().getConfig().getString("restart-message-format")
                                                                .replaceAll("%time%", (5 - current) + "")
                                                                .replaceAll("%unit%", "second" + ((5 - current) > 1 ? "s" : ""))));
                                            }
                                        }, 20L * (55L + current)));
                                    }
                                }
                                Plugin.passed++;
                            } else {
                                Bukkit.shutdown();
                            }
                        }
                    }, 0L, 20L * 60L);
                    sender.sendMessage(ChatColor.GREEN + "Started countdown.");
                } else if (args[0].equalsIgnoreCase("cancel")) {
                    if (Plugin.buffer != null) {
                        Plugin.buffer = null;
                        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', Plugin.getInstance().getConfig().getString("restart-cancel-message-format")));
                    } else if (Plugin.counter != null) {
                        Plugin.counter.cancel();
                        for (BukkitTask task : Plugin.tasks) {
                            task.cancel();
                        }
                        Plugin.tasks = new HashSet<BukkitTask>();
                        Plugin.counter = null;
                        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', Plugin.getInstance().getConfig().getString("restart-cancel-message-format")));
                    } else {
                        sender.sendMessage(ChatColor.RED + "There isn't a restart to cancel.");
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "Command syntax is: /runicrestart <minutes> or /runicrestart cancel");
                }
            } else {
                sender.sendMessage(ChatColor.RED + "Command syntax is: /runicrestart <minutes> or /runicrestart cancel");
            }
        } else {
            sender.sendMessage(ChatColor.RED + "You do not have permission to do that!");
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
