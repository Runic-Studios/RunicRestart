package com.runicrealms.plugin.runicrestart;

import co.aikar.commands.ConditionFailedException;
import co.aikar.commands.PaperCommandManager;
import com.runicrealms.plugin.common.event.RunicShutdownEvent;
import com.runicrealms.plugin.runicrestart.command.RunicRestartCommand;
import com.runicrealms.plugin.runicrestart.command.RunicStopCMD;
import com.runicrealms.plugin.runicrestart.command.ToggleTipsCommand;
import io.lumine.mythic.bukkit.MythicBukkit;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class RunicRestart extends JavaPlugin implements Listener {


    private static RunicRestart instance;
    private static PaperCommandManager commandManager;
    private static RestartManager restartManager;
    private static TipsManager tipsManager;
    private static WhitelistHandler whitelistHandler;

    public static RunicRestart getInstance() {
        return instance;
    }

    public static RestartManager getRestartManager() {
        return restartManager;
    }

    public static TipsManager getTipsManager() {
        return tipsManager;
    }

    @Override
    public void onEnable() {
        instance = this;

        this.getConfig().options().copyDefaults(true);
        this.saveDefaultConfig();

        commandManager = new PaperCommandManager(this);

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

        commandManager.registerCommand(new RunicStopCMD());
        commandManager.registerCommand(new RunicRestartCommand());
        commandManager.registerCommand(new ToggleTipsCommand());

        // deprecated
//        Bukkit.getPluginCommand("maintenance").setExecutor(new MaintenanceCommand());

        tipsManager = new TipsManager();
        new StopCommandHandler();
        whitelistHandler = new WhitelistHandler();
        restartManager = new RestartManager();

        Bukkit.getPluginManager().registerEvents(this, this);

        sanitizeMobs();
    }

    @Override
    public void onDisable(){
        whitelistHandler.onDisable();
    }

    /**
     * Attempts a safe runic shutdown
     */
    public static void shutdown() {
        Bukkit.getScheduler().runTask(RunicRestart.getInstance(), () -> Bukkit.getPluginManager().callEvent(new RunicShutdownEvent()));
    }

    /**
     * Method to remove all vanilla mobs which may have spawned / been left over on startup
     */
    // TODO: possible try with resources / removal of duplicated code
    // TODO: move to a different plugin
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
