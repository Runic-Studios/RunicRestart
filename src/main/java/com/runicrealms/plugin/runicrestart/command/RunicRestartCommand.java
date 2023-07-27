package com.runicrealms.plugin.runicrestart.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CatchUnknown;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Conditions;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import com.runicrealms.plugin.runicrestart.RunicRestart;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

@CommandAlias("runicrestart")
@Conditions("is-op")
@CommandPermission("runic.op")
public class RunicRestartCommand extends BaseCommand {

    /**
     * Used for parsing command args
     *
     * @param number the string arg that was entered
     * @return true if it is an int
     */
    private static boolean isInt(String number) {
        try {
            Integer.parseInt(number);
            return true;
        } catch (Exception exception) {
            return false;
        }
    }

    @Default
    @CatchUnknown
    public void onCommand(CommandSender sender, String[] args) {
        if (args.length == 0 || !isInt(args[0])) {
            sender.sendMessage(ChatColor.RED + "Please specify the minutes until restart");
            return;
        }
        int time = Integer.parseInt(args[0]);
        RunicRestart.getRestartManager().startNewCountdown(time);
    }

    @Subcommand("cancel")
    public void onCommandCancel(CommandSender sender, String[] args) {
        boolean quiet = args.length > 0 && args[0].equalsIgnoreCase("quiet");
        RunicRestart.getRestartManager().cancelCurrentCountdown();
        if (!quiet)
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', RunicRestart.getInstance().getConfig().getString("restart-cancel-message-format")));
    }

    @Subcommand("delay")
    public void onCommandDelay(CommandSender sender, String[] args) {
        if (args.length == 0 || !isInt(args[0])) {
            sender.sendMessage(ChatColor.RED + "Please specify the minutes until restart");
            return;
        }
        int time = Integer.parseInt(args[0]);
        boolean quiet = args.length > 1 && args[1].equalsIgnoreCase("quiet");
        RunicRestart.getRestartManager().startNewCountdown(time);
        if (!quiet)
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', RunicRestart.getInstance().getConfig().getString("restart-cancel-message-format")));
    }

}
