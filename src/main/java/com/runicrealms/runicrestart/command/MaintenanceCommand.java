package com.runicrealms.runicrestart.command;

import com.runicrealms.runicrestart.RunicRestart;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class MaintenanceCommand implements CommandExecutor {

    private static int countdown;
    private static int task;
    private boolean first = true;

    private static String combineArgs(String[] args, int start) {
        StringBuilder builder = new StringBuilder();
        for (int i = start; i < args.length; i++) {
            builder.append(args[i]);
            if (i != args.length - 1) {
                builder.append(" ");
            }
        }
        return builder.toString();
    }

    private static boolean isInt(String number) {
        try {
            Integer.parseInt(number);
            return true;
        } catch (Exception exception) {
            return false;
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender.isOp()) {
            if (args.length >= 1) {
                if (isInt(args[0])) {
                    String message = args.length > 1 ? combineArgs(args, 1) : "";
                    countdown = Integer.parseInt(args[0]);
                    first = true;
                    task = Bukkit.getScheduler().scheduleSyncRepeatingTask(RunicRestart.getInstance(), () -> {
                        if (first && countdown != 1) {
                            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&4[Notice] &cMaintenance in &e" + countdown + " minutes" + ((!message.equals("")) ? ": &c" + message : "")));
                            first = false;
                            countdown--;
                            return;
                        }
                        if (countdown % 5 == 0 || countdown == 4 || countdown == 3 || countdown == 2 || countdown == 1) {
                            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&4[Notice] &cMaintenance in &e" + countdown + " minute" + (countdown == 1 ? "" : "s") + ((!message.equals("")) ? ": &c" + message : "")));
                            if (countdown == 1) {
                                Bukkit.getScheduler().cancelTask(task);
                                Bukkit.getScheduler().runTaskLater(RunicRestart.getInstance(), () -> Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&4[Notice] &cMaintenance in &e30 seconds" + ((!message.equals("")) ? ": &c" + message : ""))), 30 * 20);
                                Bukkit.getScheduler().runTaskLater(RunicRestart.getInstance(), () -> Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&4[Notice] &cMaintenance in &e10 seconds" + ((!message.equals("")) ? ": &c" + message : ""))), 50 * 20);
                                Bukkit.getScheduler().runTaskLater(RunicRestart.getInstance(), () -> Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&4[Notice] &cMaintenance in &e5 seconds" + ((!message.equals("")) ? ": &c" + message : ""))), 55 * 20);
                                Bukkit.getScheduler().runTaskLater(RunicRestart.getInstance(), () -> Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&4[Notice] &cMaintenance in &e3 seconds" + ((!message.equals("")) ? ": &c" + message : ""))), 57 * 20);
                                Bukkit.getScheduler().runTaskLater(RunicRestart.getInstance(), () -> Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&4[Notice] &cMaintenance in &e2 seconds" + ((!message.equals("")) ? ": &c" + message : ""))), 58 * 20);
                                Bukkit.getScheduler().runTaskLater(RunicRestart.getInstance(), () -> Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&4[Notice] &cMaintenance in &e1 seconds" + ((!message.equals("")) ? ": &c" + message : ""))), 59 * 20);
                                Bukkit.getScheduler().runTaskLater(RunicRestart.getInstance(), () -> {
                                    for (Player player : Bukkit.getOnlinePlayers()) {
                                        if (!player.isOp()) {
                                            player.kickPlayer(ChatColor.translateAlternateColorCodes('&', "&aMaintenance has start. Check discord for more information."));
                                        }
                                    }
                                    Bukkit.setWhitelist(true);
                                }, 60 * 20);
                            }
                        }
                        countdown--;
                    }, 0L, 20 * 60);
                } else {
                    sender.sendMessage(ChatColor.RED + "That is not a number");
                }
            } else {
                sender.sendMessage(ChatColor.RED + "Please enter in how many minutes maintenance should start");
            }
        } else {
            sender.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
        }
        return true;
    }

}
