package com.runicrealms.runicrestart.command;

import com.runicrealms.libs.acf.BaseCommand;
import com.runicrealms.libs.acf.annotation.CatchUnknown;
import com.runicrealms.libs.acf.annotation.CommandAlias;
import com.runicrealms.libs.acf.annotation.Conditions;
import com.runicrealms.libs.acf.annotation.Default;
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