package com.runicrealms.runicrestart.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CatchUnknown;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Conditions;
import co.aikar.commands.annotation.Default;
import com.runicrealms.runicrestart.RunicRestart;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

@CommandAlias("runicstop|rstop")
public class RunicStopCMD extends BaseCommand {

    @Default
    @CatchUnknown
    @Conditions("is-op")
    public void onCommand(CommandSender commandSender) {
        commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aShutting server down safely..."));
        RunicRestart.getAPI().beginShutdown();
    }

}