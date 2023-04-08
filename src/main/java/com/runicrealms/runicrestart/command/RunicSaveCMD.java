package com.runicrealms.runicrestart.command;

import com.runicrealms.libs.acf.BaseCommand;
import com.runicrealms.libs.acf.annotation.CatchUnknown;
import com.runicrealms.libs.acf.annotation.CommandAlias;
import com.runicrealms.libs.acf.annotation.Conditions;
import com.runicrealms.libs.acf.annotation.Default;
import com.runicrealms.runicrestart.RunicRestart;
import com.runicrealms.runicrestart.event.PreShutdownEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;

@CommandAlias("runicsave|rsave")
public class RunicSaveCMD extends BaseCommand implements Listener {

    @Default
    @CatchUnknown
    @Conditions("is-op")
    public void onCommand(CommandSender commandSender) {
        RunicRestart.shouldShutdown = false;
        commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aSaving and kicking players..."));
        PreShutdownEvent preShutdownEvent = new PreShutdownEvent();
        Bukkit.getPluginManager().callEvent(preShutdownEvent);
    }
}