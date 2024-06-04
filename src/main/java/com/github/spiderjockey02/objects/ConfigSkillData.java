package com.github.spiderjockey02.objects;

import com.github.spiderjockey02.enums.SkillType;
import org.bukkit.Material;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ConfigSkillData {
    private final SkillType type;
    private final Integer defaultPoints;
    private final Boolean enabled;
    private final Map<Integer, LevelData> levels;
    private final List<Material> blocks;

    public ConfigSkillData(SkillType type, Integer defaultPoints, Boolean enabled, Map<Integer, LevelData> levels, List<Material> blocks) {
        this.type = type;
        this.defaultPoints = defaultPoints;
        this.enabled = enabled;
        this.levels = levels;
        this.blocks = blocks;
    }

    public SkillType getType() {
        return type;
    }

    public Integer getDefaultPoints() {
        return defaultPoints;
    }

    public LevelData getLevel(int i) {
        return levels.get(i);
    }

    public Map<Integer, LevelData> getLevels() {
        return levels;
    }

    public Integer getMaxLevel() {
        return Collections.max(levels.keySet());
    }

    public Boolean isEnabled() {
        return enabled;
    }

    public List<Material> getBlocks() {
        return blocks;
    }

    @Override
    public String toString() {
        return "type: " + this.type + ", enabled: " + this.enabled + ", levels: " + this.levels + ", blocks: " + this.blocks;
    }
}
