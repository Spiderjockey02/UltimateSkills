package com.github.spiderjockey02.managers;

import com.github.spiderjockey02.UltimateSkills;
import com.github.spiderjockey02.enums.SkillType;
import com.github.spiderjockey02.objects.ConfigSkillData;
import com.github.spiderjockey02.objects.LevelData;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.ConfigurationSection;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigManager {
    private final UltimateSkills plugin;
    public Map<SkillType, ConfigSkillData> skills = new HashMap<>();

    public ConfigManager(UltimateSkills plugin) {
        this.plugin = plugin;
    }

    public void saveConfig() {
        // Load default config file
        this.plugin.saveDefaultConfig();

        // Load Skills' config files
        this.loadSkillConfigs();
    }

    public void loadSkillConfigs() {
        for (SkillType skillType : SkillType.values()) {
            YamlConfiguration config = loadSkillData("skills" + File.separator + skillType.getCapitalise() + ".yml");
            Boolean enabled = config.getBoolean("enabled");
            Integer defaultPoints = config.getInt("defaultPoints");

            // Get level data
            Map<Integer, LevelData> levels = this.getLevels(config);

            // Get blocks
            List<?> blockSection = config.getList("blocks");
            List<Material> blocks = new ArrayList<>();
            if (blockSection != null) {
                for (Object key : blockSection) {
                    Material material = Material.getMaterial((String) key);
                    blocks.add(material);
                }
            }
            // Create Config data and add to cache
            ConfigSkillData skillData = new ConfigSkillData(skillType, defaultPoints, enabled, levels, blocks);
            this.skills.put(skillType, skillData);
        }
    }

    private YamlConfiguration loadSkillData(String path) {
        File customConfigFile = new File(this.plugin.getDataFolder(), path);
        if (!customConfigFile.exists()) {
            customConfigFile.getParentFile().mkdirs();
            plugin.saveResource(path, false);
        }

        YamlConfiguration customConfig = new YamlConfiguration();
        try {
            customConfig.load(customConfigFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
        return customConfig;
    }

    public ConfigSkillData getSkill(SkillType skillType) {
        return this.skills.get(skillType);
    }

    private Map<Integer, LevelData> getLevels(YamlConfiguration config) {
        ConfigurationSection levelSection = config.getConfigurationSection("levels");

        Map<Integer, LevelData> levels = new HashMap<>();
        for(String key : levelSection.getKeys(false)) {
            int xpNeeded = config.getInt("levels." + key + ".xpneeded");

            // Ge level's lore
            List<String> lore = (List<String>) config.getList("levels." + key + ".lore");
            if (lore == null) lore = new ArrayList<>();

            // Get the commands that will be run when the player gets to that level
            List<String> commands = (List<String>) config.getList("levels." + key + ".commands");
            if (commands == null) commands = new ArrayList<>();

            levels.put(Integer.valueOf(key), new LevelData(xpNeeded, lore, commands));
        }
        return levels;
    }

    public Boolean isDebugEnabled() {
        return plugin.getConfig().getBoolean("debug");
    };

    public Boolean isMobStackRespected() {
        return !plugin.getConfig().getBoolean("mobStackingCountAsOne");
    }
}
