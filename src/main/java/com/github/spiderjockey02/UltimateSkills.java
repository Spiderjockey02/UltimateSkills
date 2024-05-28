package com.github.spiderjockey02;

import com.github.spiderjockey02.commands.CommandManager;
import com.github.spiderjockey02.config.ConfigManager;
import com.github.spiderjockey02.listeners.BlockBreakListener;
import com.github.spiderjockey02.listeners.EntityDeathListener;
import com.github.spiderjockey02.listeners.FishingListener;
import com.github.spiderjockey02.listeners.InventoryClickListener;
import com.github.spiderjockey02.managers.SkillManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Bukkit;

public class UltimateSkills  extends JavaPlugin {
    private SkillManager skillManager;
    private CommandManager commandManager;
    private ConfigManager configManager;

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
    }

    public SkillManager getSkillManager() {
        return this.skillManager;
    }

    public ConfigManager getConfigManager() {
        return this.configManager;
    }

    public static UltimateSkills getInstance() {
        return UltimateSkills.getPlugin(UltimateSkills.class);
    }
}