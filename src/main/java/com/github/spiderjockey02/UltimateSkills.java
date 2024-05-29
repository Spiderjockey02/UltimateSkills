package com.github.spiderjockey02;

import com.github.spiderjockey02.commands.CommandManager;
import com.github.spiderjockey02.managers.ConfigManager;
import com.github.spiderjockey02.database.Database;
import com.github.spiderjockey02.database.SQLDatabase;
import com.github.spiderjockey02.listeners.BlockBreakListener;
import com.github.spiderjockey02.listeners.EntityDeathListener;
import com.github.spiderjockey02.listeners.FishingListener;
import com.github.spiderjockey02.listeners.InventoryClickListener;
import com.github.spiderjockey02.managers.SkillManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Bukkit;

import java.sql.SQLException;

public class UltimateSkills  extends JavaPlugin {
    private SkillManager skillManager;
    private CommandManager commandManager;
    private ConfigManager configManager;
    private Database SQLDatabase;

    @Override
    public void onEnable() {
        Bukkit.getConsoleSender().sendMessage("Plugin Has been enabled!");
        // Load managers
        this.skillManager = new SkillManager(this);
        this.commandManager = new CommandManager(this);
        this.configManager = new ConfigManager(this);

        // Register events
        Bukkit.getPluginManager().registerEvents(new BlockBreakListener(this), this);
        Bukkit.getPluginManager().registerEvents(new EntityDeathListener(this), this);
        Bukkit.getPluginManager().registerEvents(new FishingListener(this), this);
        Bukkit.getPluginManager().registerEvents(new InventoryClickListener(), this);

        // Fetch config
        this.configManager.saveConfig();

        // Initialise database
        try {
            this.SQLDatabase = new SQLDatabase(getDataFolder().getAbsolutePath() + "/skills.db");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onDisable() {
        try {
            this.SQLDatabase.closeConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public SkillManager getSkillManager() {
        return this.skillManager;
    }

    public ConfigManager getConfigManager() {
        return this.configManager;
    }

    public Database getDatabaseManager() {
        return this.SQLDatabase;
    }

    public CommandManager getCommandManager() {
        return this.commandManager;
    }

    public static UltimateSkills getInstance() {
        return UltimateSkills.getPlugin(UltimateSkills.class);
    }
}