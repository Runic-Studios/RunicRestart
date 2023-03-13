package com.runicrealms.runicrestart;

import co.aikar.commands.ConditionFailedException;
import co.aikar.commands.PaperCommandManager;
import com.runicrealms.runicrestart.api.RunicRestartApi;
import com.runicrealms.runicrestart.command.*;
import com.runicrealms.runicrestart.config.ConfigLoader;
import com.runicrealms.runicrestart.event.ServerShutdownEvent;
import io.lumine.xikage.mythicmobs.MythicMobs;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RunicRestart extends JavaPlugin implements Listener {

    public static Set<BukkitTask> tasks = new HashSet<>();
    public static int finish;
    public static int passed;
    public static BukkitTask counter;
    public static BukkitTask buffer;
    public static List<String> pluginsToLoad;
    public static boolean hasWhitelist;
    public static boolean shouldShutdown = true;
    public static boolean isInMaintenance = false;
    private static RunicRestart instance;
    private static PaperCommandManager commandManager;
    private static RunicRestartApi runicRestartApi;
    private static FileConfiguration dataConfig;
    private static File dataFile;

    public static RunicRestart getInstance() {
        return instance;
    }

    public static RunicRestartApi getAPI() {
        return runicRestartApi;
    }

    public static FileConfiguration getDataFileConfiguration() {
        return dataConfig;
    }

    public static File getDataFile() {
        return dataFile;
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
        try {
            if (!this.getDataFolder().exists()) {
                this.getDataFolder().mkdirs();
            }
            dataFile = new File(this.getDataFolder(), "data.yml");
            if (!dataFile.exists()) {
                dataFile.createNewFile();
            }
            dataConfig = ConfigLoader.getYamlConfigFile("data.yml", this.getDataFolder());
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        hasWhitelist = Bukkit.hasWhitelist();
        Bukkit.setWhitelist(true);
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.kickPlayer("The server is still loading!");
        }
        pluginsToLoad = this.getConfig().getStringList("plugins-to-load");
        Bukkit.getPluginCommand("runicrestart").setExecutor(new RunicRestartCommand());
        Bukkit.getPluginCommand("toggletips").setExecutor(new ToggleTipsCommand());
        Bukkit.getPluginCommand("maintenance").setExecutor(new MaintenanceCommand());
        Bukkit.getPluginManager().registerEvents(this, this);
        Bukkit.getPluginManager().registerEvents(new TipsManager(), this);
        TipsManager.setupTask();
        if (this.getConfig().getInt("restart-buffer") >= 0) {
            buffer = Bukkit.getScheduler().runTaskLater(this, () -> {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "runicrestart " + (RunicRestart.getInstance().getConfig().getInt("restart-duration")));
                RunicRestart.buffer = null;
            }, 20L * 60L * (this.getConfig().getInt("restart-buffer")));
        }
        sanitizeMobs();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (buffer == null) {
            if (RunicRestart.finish - RunicRestart.passed > 1) {
                event.getPlayer().sendMessage(ChatColor.RED + "Warning: this server is restarting in " + (RunicRestart.finish - RunicRestart.passed) + " minutes!");
            } else {
                event.getPlayer().sendMessage(ChatColor.RED + "Warning: this server is restarting less than a minute!");
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPreJoin(AsyncPlayerPreLoginEvent event) {
        if (pluginsToLoad.size() > 0) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                    ChatColor.YELLOW + "This server is restarting! Please rejoin in a moment.");
        }
    }

    @EventHandler
    public void onShutdown(ServerShutdownEvent event) {
        try {
            for (BukkitTask task : tasks) {
                if (!task.isCancelled()) {
                    task.cancel();
                }
            }
            if (counter != null) {
                if (!counter.isCancelled()) {
                    counter.cancel();
                }
            }
            if (buffer != null) {
                if (!buffer.isCancelled()) {
                    buffer.cancel();
                }
            }
            tasks = null;
            counter = null;
            buffer = null;
        } catch (Exception ignored) {

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
    }

    /**
     * Method to remove all vanilla mobs which may have spawned / been left over on startup
     */
    private void sanitizeMobs() {
        Bukkit.getScheduler().runTaskLaterAsynchronously(RunicRestart.getInstance(), () -> {
            World world = Bukkit.getWorld("Alterra");
            assert world != null;
            for (LivingEntity livingEntity : world.getLivingEntities()) {
                if (livingEntity instanceof Player) continue;
                if (livingEntity instanceof ArmorStand) continue;
                if (!MythicMobs.inst().getMobManager().isActiveMob(livingEntity.getUniqueId())) {
                    Bukkit.getScheduler().runTask(RunicRestart.getInstance(), livingEntity::remove);
                }
            }
            world = Bukkit.getWorld("dungeons");
            assert world != null;
            for (LivingEntity livingEntity : world.getLivingEntities()) {
                if (livingEntity instanceof Player) continue;
                if (livingEntity instanceof ArmorStand) continue;
                if (!MythicMobs.inst().getMobManager().isActiveMob(livingEntity.getUniqueId())) {
                    Bukkit.getScheduler().runTask(RunicRestart.getInstance(), livingEntity::remove);
                }
            }
        }, 10 * 20L);
    }

}
