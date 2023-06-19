package com.runicrealms.runicrestart;

import co.aikar.commands.ConditionFailedException;
import co.aikar.commands.PaperCommandManager;
import com.runicrealms.runicrestart.api.RunicRestartApi;
import com.runicrealms.runicrestart.command.MaintenanceCommand;
import com.runicrealms.runicrestart.command.RunicRestartCommand;
import com.runicrealms.runicrestart.command.RunicSaveCMD;
import com.runicrealms.runicrestart.command.RunicStopCMD;
import com.runicrealms.runicrestart.command.ToggleTipsCommand;
import io.lumine.mythic.bukkit.MythicBukkit;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class RunicRestart extends JavaPlugin implements Listener {


    public static List<String> pluginsToLoad;
    public static boolean hasWhitelist;
    public static boolean shouldShutdown = true;
    public static boolean isInMaintenance = false;
    private static RunicRestart instance;
    private static PaperCommandManager commandManager;
    private static RunicRestartApi runicRestartApi;
    private static RestartManager restartManager;

    public static RunicRestart getInstance() {
        return instance;
    }

    public static RestartManager getRestartManager() {
        return restartManager;
    }

    public static RunicRestartApi getAPI() {
        return runicRestartApi;
    }

    @Override
    public void onEnable() {
        instance = this;
        commandManager = new PaperCommandManager(this);
        runicRestartApi = new ShutdownManager();
        registerACFCommands();
        commandManager.getCommandConditions().addCondition("is-console-or-op", context -> {
            if (!(context.getIssuer().getIssuer() instanceof ConsoleCommandSender) && !context.getIssuer().getIssuer().isOp()) // ops can execute console commands
                throw new ConditionFailedException("Only the console may run this command!");
        });
        commandManager.getCommandConditions().addCondition("is-op", context -> {
            if (!context.getIssuer().getIssuer().isOp())
                throw new ConditionFailedException("You must be an operator to run this command!");
        });
        commandManager.getCommandConditions().addCondition("is-player", context -> {
            if (!(context.getIssuer().getIssuer() instanceof Player))
                throw new ConditionFailedException("This command cannot be run from console!");
        });
        this.getConfig().options().copyDefaults(true);
        this.saveDefaultConfig();

        hasWhitelist = Bukkit.hasWhitelist();
        Bukkit.setWhitelist(true);
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.kickPlayer("The server is still loading!");
        }
        pluginsToLoad = this.getConfig().getStringList("plugins-to-load");
        Bukkit.getPluginCommand("maintenance").setExecutor(new MaintenanceCommand());
        Bukkit.getPluginManager().registerEvents(this, this);
        Bukkit.getPluginManager().registerEvents(new TipsManager(), this);
        TipsManager.setupTask();

        restartManager = new RestartManager();

        sanitizeMobs();
    }


    @EventHandler(priority = EventPriority.LOW)
    public void onPreJoin(AsyncPlayerPreLoginEvent event) {
        if (pluginsToLoad.size() > 0) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                    ChatColor.YELLOW + "This server is restarting! Please rejoin in a moment.");
        }
    }


    /**
     * Initialize commands using Aikur's Command Framework
     */
    private void registerACFCommands() {
        if (commandManager == null) {
            Bukkit.getLogger().info(ChatColor.DARK_RED + "ERROR: FAILED TO INITIALIZE ACF COMMANDS");
            return;
        }
        commandManager.registerCommand(new RunicSaveCMD());
        commandManager.registerCommand(new RunicStopCMD());
        commandManager.registerCommand(new RunicRestartCommand());
        commandManager.registerCommand(new ToggleTipsCommand());
    }

    /**
     * Method to remove all vanilla mobs which may have spawned / been left over on startup
     */
    // TODO: possible try with resources / removal of duplicated code
    private void sanitizeMobs() {
        Bukkit.getScheduler().runTaskLater(RunicRestart.getInstance(), () -> {
            World world = Bukkit.getWorld("Alterra");
            removeMobs(world);
            world = Bukkit.getWorld("dungeons");
            removeMobs(world);
        }, 10 * 20L);
    }

    private void removeMobs(World world) {
        if(world != null) {
            for (LivingEntity livingEntity : world.getLivingEntities()) {
                if (livingEntity instanceof Player) continue;
                if (livingEntity instanceof ArmorStand) continue;
                if (!MythicBukkit.inst().getMobManager().isActiveMob(livingEntity.getUniqueId())) {
                    Bukkit.getScheduler().runTask(RunicRestart.getInstance(), livingEntity::remove);
                }
            }
        }
    }

}
