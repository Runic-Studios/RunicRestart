package com.runicrealms.runicrestart.command;

import com.runicrealms.runicrestart.RunicRestart;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashSet;

public class RunicRestartCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.isOp()) {
            if (args.length == 1) {
                if (isInt(args[0])) {
                    RunicRestart.finish = Integer.parseInt(args[0]);
                    RunicRestart.passed = 0;
                    RunicRestart.counter = Bukkit.getScheduler().runTaskTimer(RunicRestart.getInstance(), () -> {
                        if (RunicRestart.passed < RunicRestart.finish) {
                            boolean shouldDisplay = true;
                            int time = RunicRestart.finish - RunicRestart.passed;

                            if (time == 120 || time == 60 || time == 30 || time == 20 || time == 10 || time == 5 || time == 3 || time == 2) {
                                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&',
                                        RunicRestart.getInstance().getConfig().getString("restart-message-format")
                                                .replaceAll("%time%", (RunicRestart.finish - RunicRestart.passed) + "")
                                                .replaceAll("%unit%", "minute" + (RunicRestart.finish - RunicRestart.passed > 1 ? "s" : ""))));
                            }
                            if (time == 1) {
                                RunicRestart.tasks.add(Bukkit.getScheduler().runTaskLater(RunicRestart.getInstance(), () -> Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&',
                                        RunicRestart.getInstance().getConfig().getString("restart-message-format")
                                                .replaceAll("%time%", "30")
                                                .replaceAll("%unit%", "seconds"))), 20L * 30L));
                                RunicRestart.tasks.add(Bukkit.getScheduler().runTaskLater(RunicRestart.getInstance(), () -> Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&',
                                        RunicRestart.getInstance().getConfig().getString("restart-message-format")
                                                .replaceAll("%time%", "10")
                                                .replaceAll("%unit%", "seconds"))), 20L * 50L));
                                for (int i = 0; i < 5; i++) {
                                    final int current = i;
                                    RunicRestart.tasks.add(Bukkit.getScheduler().runTaskLater(RunicRestart.getInstance(), () -> Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&',
                                            RunicRestart.getInstance().getConfig().getString("restart-message-format")
                                                    .replaceAll("%time%", (5 - current) + "")
                                                    .replaceAll("%unit%", "second" + ((5 - current) > 1 ? "s" : "")))), 20L * (55L + current)));
                                }
                            }
                            RunicRestart.passed++;
                        } else {
                            RunicRestart.startShutdown();
                            for (Player player : Bukkit.getOnlinePlayers()) {
                                player.kickPlayer(ChatColor.GREEN + "Server restarting, we'll be back up soon!");
                            }
                        }
                    }, 0L, 20L * 60L);
                    sender.sendMessage(ChatColor.GREEN + "Started countdown.");
                } else if (args[0].equalsIgnoreCase("cancel")) {
                    if (RunicRestart.buffer != null) {
                        RunicRestart.buffer = null;
                        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', RunicRestart.getInstance().getConfig().getString("restart-cancel-message-format")));
                    } else if (RunicRestart.counter != null) {
                        RunicRestart.counter.cancel();
                        for (BukkitTask task : RunicRestart.tasks) {
                            task.cancel();
                        }
                        RunicRestart.tasks = new HashSet<BukkitTask>();
                        RunicRestart.counter = null;
                        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', RunicRestart.getInstance().getConfig().getString("restart-cancel-message-format")));
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
