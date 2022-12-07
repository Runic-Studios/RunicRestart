package com.runicrealms.runicrestart.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CatchUnknown;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Conditions;
import co.aikar.commands.annotation.Default;
import com.runicrealms.runicrestart.RunicRestart;
import com.runicrealms.runicrestart.ShutdownManager;
import com.runicrealms.runicrestart.event.PreShutdownEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("runicstop|rstop")
public class RunicStopCMD extends BaseCommand {

    @Default
    @CatchUnknown
    @Conditions("is-op")
    public void onCommand(CommandSender commandSender) {
        commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aShutting server down safely..."));
        ShutdownManager.setIsShuttingDown(true);
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.kickPlayer(RunicRestart.getAPI().getShutdownMessage());
        }
        PreShutdownEvent preShutdownEvent = new PreShutdownEvent();
        Bukkit.getPluginManager().callEvent(preShutdownEvent);
    }

}