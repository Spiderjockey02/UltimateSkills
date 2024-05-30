package com.github.spiderjockey02;

import com.github.spiderjockey02.commands.CommandManager;
import com.github.spiderjockey02.listeners.*;
import com.github.spiderjockey02.managers.ConfigManager;
import com.github.spiderjockey02.database.Database;
import com.github.spiderjockey02.database.implementations.SQLDatabase;
import com.github.spiderjockey02.managers.SkillManager;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Bukkit;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

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
        this.commandManager = new CommandManager();
        this.configManager = new ConfigManager(this);

        // Register events
        List<Listener> listeners = Arrays.asList(
                new BlockBreakListener(this),
                new EntityDeathListener(this),
                new FishingListener(this),
                new InventoryClickListener(),
                new PlayerListener(this)
        );
        this.registerEvents(listeners);

        // Fetch config
        this.configManager.saveConfig();

        // Initialise database
        try {
            // Check if connection should be MySQL or SQLite
            this.SQLDatabase = new SQLDatabase(getDataFolder().getAbsolutePath() + "/skills.db", this);
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
    public void registerEvents(List<Listener> listeners) {
        for (Listener listener : listeners) {
            Bukkit.getPluginManager().registerEvents(listener, this);
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