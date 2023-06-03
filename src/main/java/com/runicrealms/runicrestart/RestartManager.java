package com.runicrealms.runicrestart;

import com.runicrealms.runicrestart.event.ServerShutdownEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashSet;
import java.util.Set;

public class RestartManager implements Listener {

    private int finish; // When we will restart (in how many seconds since start of counter)
    private int passed; // How many seconds have passed since start of counter
    private BukkitTask counter; // The counter object that loops every second until the decrement time
    private BukkitTask buffer; // The buffer timer that waits until the counter object should be initialized
    private Set<BukkitTask> tasks = new HashSet<>(); // set of tasks that represent the last sub-1-minute countdown

    public RestartManager() {
        if (RunicRestart.getInstance().getConfig().getInt("restart-buffer") >= 0) {
            buffer = Bukkit.getScheduler().runTaskLater(RunicRestart.getInstance(), () -> {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "runicrestart " + (RunicRestart.getInstance().getConfig().getInt("restart-duration")));
                buffer = null;
            }, 20L * 60L * (RunicRestart.getInstance().getConfig().getInt("restart-buffer")));
        }

        Bukkit.getPluginManager().registerEvents(this, RunicRestart.getInstance());
    }

    public void startNewCountdown(int minutesToRestart) {
        if (buffer != null || counter != null) cancelCurrentCountdown();
        finish = minutesToRestart;
        passed = 0;
        counter = Bukkit.getScheduler().runTaskTimer(RunicRestart.getInstance(), () -> {
            if (passed < finish) {
                int time = finish - passed;
                if (time == 120 || time == 60 || time == 30 || time == 20 || time == 10 || time == 5 || time == 3 || time == 2) {
                    Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&',
                            RunicRestart.getInstance().getConfig().getString("restart-message-format")
                                    .replaceAll("%time%", (finish - passed) + "")
                                    .replaceAll("%unit%", "minute" + (finish - passed > 1 ? "s" : ""))));
                }
                if (time == 1) {
                    tasks.add(Bukkit.getScheduler().runTaskLater(RunicRestart.getInstance(), () -> Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&',
                            RunicRestart.getInstance().getConfig().getString("restart-message-format")
                                    .replaceAll("%time%", "30")
                                    .replaceAll("%unit%", "seconds"))), 20L * 30L));
                    tasks.add(Bukkit.getScheduler().runTaskLater(RunicRestart.getInstance(), () -> Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&',
                            RunicRestart.getInstance().getConfig().getString("restart-message-format")
                                    .replaceAll("%time%", "10")
                                    .replaceAll("%unit%", "seconds"))), 20L * 50L));
                    for (int i = 0; i < 5; i++) {
                        final int current = i;
                        tasks.add(Bukkit.getScheduler().runTaskLater(RunicRestart.getInstance(), () -> Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&',
                                RunicRestart.getInstance().getConfig().getString("restart-message-format")
                                        .replaceAll("%time%", (5 - current) + "")
                                        .replaceAll("%unit%", "second" + ((5 - current) > 1 ? "s" : "")))), 20L * (55L + current)));
                    }
                }
                passed++;
            } else {
                RunicRestart.getAPI().beginShutdown();
            }
        }, 0L, 20L * 60L);
    }

    public void cancelCurrentCountdown() {
        if (buffer != null) {
            buffer.cancel();
            buffer = null;
        }
        if (counter != null) {
            counter.cancel();
            counter = null;
        }
        tasks.forEach(BukkitTask::cancel);
        tasks.clear();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (buffer == null) {
            if (finish - passed > 1) {
                event.getPlayer().sendMessage(ChatColor.RED + "Warning: this server is restarting in " + (finish - passed) + " minutes!");
            } else {
                event.getPlayer().sendMessage(ChatColor.RED + "Warning: this server is restarting less than a minute!");
            }
        }
    }

    @EventHandler
    public void onShutdown(ServerShutdownEvent event) {
        try {
            for (BukkitTask task : tasks) {
                if (!task.isCancelled()) {
                    task.cancel();
                }
            }
            if (counter != null) {
                if (!counter.isCancelled()) {
                    counter.cancel();
                }
            }
            if (buffer != null) {
                if (!buffer.isCancelled()) {
                    buffer.cancel();
                }
            }
            tasks = null;
            counter = null;
            buffer = null;
        } catch (Exception ignored) {

        }
    }

    public int getDefaultLifetime() {
        return RunicRestart.getInstance().getConfig().getInt("restart-buffer") + RunicRestart.getInstance().getConfig().getInt("restart-duration");
    }

}
