package com.runicrealms.runicrestart.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CatchUnknown;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import com.runicrealms.plugin.common.RunicCommon;
import com.runicrealms.runicrestart.TipsManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandAlias("toggletips")
public class ToggleTipsCommand extends BaseCommand {

    @Default
    @CatchUnknown
    public void onCommand(Player player) {
        if (TipsManager.getTips().contains(player.getUniqueId())) {
            TipsManager.getTips().remove(player.getUniqueId());
            player.sendMessage(ChatColor.GREEN + "Disabled tips! Use /toggletips to enable them.");
            RunicCommon.getLuckPermsAPI().savePayload(RunicCommon.getLuckPermsAPI().createPayload(player.getUniqueId(), (data) -> data.set("runic.tips", false)));
        } else {
            TipsManager.getTips().add(player.getUniqueId());
            player.sendMessage(ChatColor.GREEN + "Enabled tips! Use /toggletips to disable them.");
            RunicCommon.getLuckPermsAPI().savePayload(RunicCommon.getLuckPermsAPI().createPayload(player.getUniqueId(), (data) -> data.set("runic.tips", true)));
        }
    }
}
