package com.runicrealms.runicrestart;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TipsManager implements Listener {

    private static final Set<Player> tips = new HashSet<>();
    private static List<String> tipMessages;

    public static void setupTask() {
        tipMessages = RunicRestart.getInstance().getConfig().getStringList("tips.messages");
        Bukkit.getScheduler().scheduleSyncRepeatingTask(RunicRestart.getInstance(), () -> {
                    String tip = ChatColor.translateAlternateColorCodes('&', tipMessages.get((int) Math.floor(Math.random() * tipMessages.size())));
                    for (Player player : tips) {
                        player.sendMessage(tip);
                    }
                }, RunicRestart.getInstance().getConfig().getInt("tips.delay") * 20L,
                RunicRestart.getInstance().getConfig().getInt("tips.interval") * 20L);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Bukkit.getScheduler().runTaskLaterAsynchronously(RunicRestart.getInstance(), () -> {
            if (!RunicRestart.getDataFileConfiguration().contains("tips." + event.getPlayer().getUniqueId())) {
                try {
                    RunicRestart.getDataFileConfiguration().set("tips." + event.getPlayer().getUniqueId(), true);
                    RunicRestart.getDataFileConfiguration().save(RunicRestart.getDataFile());
                    tips.add(event.getPlayer());
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                return;
            }
            if (RunicRestart.getDataFileConfiguration().getBoolean("tips." + event.getPlayer().getUniqueId())) {
                tips.add(event.getPlayer());
            }
        }, 1L);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        tips.remove(event.getPlayer());
    }

    public static Set<Player> getTips() {
        return tips;
    }

}
