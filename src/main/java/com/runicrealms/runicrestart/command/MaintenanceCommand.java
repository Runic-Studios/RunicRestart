package com.runicrealms.runicrestart.command;

import com.runicrealms.runicrestart.RunicRestart;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MaintenanceCommand implements CommandExecutor {

    private static int countdown;
    private boolean first = true;
    private static int task;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.isOp()) {
            if (args.length >= 1) {
                if (isInt(args[0])) {
                    String message = args.length > 1 ? combineArgs(args, 1) : "";
                    countdown = Integer.parseInt(args[0]);
                    first = true;
                    task = Bukkit.getScheduler().scheduleSyncRepeatingTask(RunicRestart.getInstance(), new Runnable() {
                        @Override
                        public void run() {
                            if (first && countdown != 1) {
                                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&4[Notice] &cMaintenance in &e" + countdown + " minutes" + ((message != "") ? ": &c" + message : "")));
                                first = false;
                                countdown--;
                                return;
                            }
                            if (countdown % 5 == 0 || countdown == 4 || countdown == 3 || countdown == 2 || countdown == 1) {
                                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&4[Notice] &cMaintenance in &e" + countdown + " minute" + (countdown == 1 ? "" : "s") + ((message != "") ? ": &c" + message : "")));
                                if (countdown == 1) {
                                    Bukkit.getScheduler().cancelTask(task);
                                    Bukkit.getScheduler().runTaskLater(RunicRestart.getInstance(), new Runnable() {
                                        @Override
                                        public void run() {
                                            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&4[Notice] &cMaintenance in &e30 seconds" + ((message != "") ? ": &c" + message : "")));
                                        }
                                    }, 30 * 20);
                                    Bukkit.getScheduler().runTaskLater(RunicRestart.getInstance(), new Runnable() {
                                        @Override
                                        public void run() {
                                            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&4[Notice] &cMaintenance in &e10 seconds" + ((message != "") ? ": &c" + message : "")));
                                        }
                                    }, 50 * 20);
                                    Bukkit.getScheduler().runTaskLater(RunicRestart.getInstance(), new Runnable() {
                                        @Override
                                        public void run() {
                                            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&4[Notice] &cMaintenance in &e5 seconds" + ((message != "") ? ": &c" + message : "")));
                                        }
                                    }, 55 * 20);
                                    Bukkit.getScheduler().runTaskLater(RunicRestart.getInstance(), new Runnable() {
                                        @Override
                                        public void run() {
                                            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&4[Notice] &cMaintenance in &e3 seconds" + ((message != "") ? ": &c" + message : "")));
                                        }
                                    }, 57 * 20);
                                    Bukkit.getScheduler().runTaskLater(RunicRestart.getInstance(), new Runnable() {
                                        @Override
                                        public void run() {
                                            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&4[Notice] &cMaintenance in &e2 seconds" + ((message != "") ? ": &c" + message : "")));
                                        }
                                    }, 58 * 20);
                                    Bukkit.getScheduler().runTaskLater(RunicRestart.getInstance(), new Runnable() {
                                        @Override
                                        public void run() {
                                            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&4[Notice] &cMaintenance in &e1 seconds" + ((message != "") ? ": &c" + message : "")));
                                        }
                                    }, 59 * 20);
                                    Bukkit.getScheduler().runTaskLater(RunicRestart.getInstance(), new Runnable() {
                                        @Override
                                        public void run() {
                                            for (Player player : Bukkit.getOnlinePlayers()) {
                                                if (!player.isOp()) {
                                                    player.kickPlayer(ChatColor.translateAlternateColorCodes('&', "&aMaintenance has start. Check discord for more information."));
                                                }
                                            }
                                            Bukkit.setWhitelist(true);
                                        }
                                    }, 60 * 20);
                                }
                            }
                            countdown--;
                        }
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

}
