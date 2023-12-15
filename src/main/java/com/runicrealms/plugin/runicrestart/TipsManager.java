package com.runicrealms.plugin.runicrestart;

import com.runicrealms.plugin.common.RunicCommon;
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
import java.util.UUID;

public class TipsManager implements Listener {

    private static final Set<UUID> tips = new HashSet<>();
    private static List<String> tipMessages;

    public static void setupTask() {
        tipMessages = RunicRestart.getInstance().getConfig().getStringList("tips.messages");
        Bukkit.getScheduler().runTaskTimerAsynchronously(RunicRestart.getInstance(), () -> {
                    String tip = ChatColor.translateAlternateColorCodes('&', tipMessages.get((int) Math.floor(Math.random() * tipMessages.size())));
                    for (UUID uuid : tips) {
                        Player player = Bukkit.getPlayer(uuid);
                        if (player != null) player.sendMessage(tip);
                    }
                }, RunicRestart.getInstance().getConfig().getInt("tips.delay") * 20L,
                RunicRestart.getInstance().getConfig().getInt("tips.interval") * 20L);
    }

    public static Set<UUID> getTips() {
        return tips;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        RunicCommon.getLuckPermsAPI().retrieveData(event.getPlayer().getUniqueId()).then((data) -> {
            if (data.containsKey("runic.tips") && data.getBoolean("runic.tips")) {
                tips.add(event.getPlayer().getUniqueId());
            } else if (!data.containsKey("runic.tips")) {
                tips.add(event.getPlayer().getUniqueId());
                RunicCommon.getLuckPermsAPI().savePayload(RunicCommon.getLuckPermsAPI().createPayload(event.getPlayer().getUniqueId(), (saveData) -> saveData.set("runic.tips", true)));
            }
        });
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        tips.remove(event.getPlayer().getUniqueId());
    }

}
