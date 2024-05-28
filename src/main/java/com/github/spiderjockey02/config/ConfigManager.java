package com.github.spiderjockey02.config;

import com.github.spiderjockey02.UltimateSkills;
import com.github.spiderjockey02.enums.SkillType;
import com.github.spiderjockey02.objects.LevelData;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigManager {
    private final UltimateSkills plugin;
    public Map<Integer, LevelData> levels = new HashMap<>();

    public ConfigManager(UltimateSkills plugin) {
        this.plugin = plugin;

        this.fetchLevels();
    }

    public void saveConfig() {
        this.plugin.saveDefaultConfig();
    }

    public void fetchLevels() {
        ConfigurationSection levelSection = this.plugin.getConfig().getConfigurationSection("levels");

        for(String key : levelSection.getKeys(false)){
            int xpNeeded = this.plugin.getConfig().getInt("levels." + key + ".xpneeded");
            List<String> lore = (List<String>) this.plugin.getConfig().getList("levels." + key + ".lore");
            if (lore == null) lore = new ArrayList<>();

            List<String> commands = (List<String>) this.plugin.getConfig().getList("levels." + key + ".commands");
            if (commands == null) commands = new ArrayList<>();

            levels.put(Integer.valueOf(key), new LevelData(xpNeeded, lore, commands));
        }
    }

    public Integer fetchPoints(SkillType type) {
        ConfigurationSection pointSection = this.plugin.getConfig().getConfigurationSection("points");
        // Default points to 3
        if (pointSection == null) return 3;
        return pointSection.getInt(type.getCapitalise());
    }
}
