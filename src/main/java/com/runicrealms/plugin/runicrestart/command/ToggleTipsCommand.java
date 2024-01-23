package com.runicrealms.plugin.runicrestart.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CatchUnknown;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import com.runicrealms.plugin.common.RunicAPI;
import com.runicrealms.plugin.common.RunicCommon;
import com.runicrealms.plugin.runicrestart.RunicRestart;
import com.runicrealms.plugin.runicrestart.TipsManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandAlias("toggletips")
public class ToggleTipsCommand extends BaseCommand {

    @Default
    @CatchUnknown
    public void onCommand(Player player) {
        if (RunicRestart.getTipsManager().getTips().contains(player.getUniqueId())) {
            RunicRestart.getTipsManager().getTips().remove(player.getUniqueId());
            player.sendMessage(ChatColor.GREEN + "Disabled tips! Use /toggletips to enable them.");
            RunicAPI.getLuckPermsAPI().savePayload(RunicAPI.getLuckPermsAPI().createPayload(player.getUniqueId(), (data) -> data.set("runic.tips", false)));
        } else {
            RunicRestart.getTipsManager().getTips().add(player.getUniqueId());
            player.sendMessage(ChatColor.GREEN + "Enabled tips! Use /toggletips to disable them.");
            RunicAPI.getLuckPermsAPI().savePayload(RunicAPI.getLuckPermsAPI().createPayload(player.getUniqueId(), (data) -> data.set("runic.tips", true)));
        }
    }
}
