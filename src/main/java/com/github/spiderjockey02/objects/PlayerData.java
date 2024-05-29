package com.github.spiderjockey02.objects;

import com.github.spiderjockey02.enums.SkillType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerData {
    private final UUID uuid;
    private final Integer totalLevel;
    private final Integer totalPoints;
    public final Map<SkillType, PlayerSkill> skills = new HashMap<>();

    public PlayerData(String uuid, Integer totalPoints, Integer totalLevel) {
        this.totalPoints = totalPoints;
        this.totalLevel = totalLevel;
        this.uuid = UUID.fromString(uuid);
    }

    public UUID getUuid() {
        return uuid;
    }

    public Integer getTotalLevel() {
        return totalLevel;
    }

    public Integer getTotalPoints() {
        return totalPoints;
    }

    public PlayerSkill getSkill(SkillType skillType) {
        return skills.get(skillType);
    }

    public Boolean hasSkill(SkillType skillType) {
        return skills.containsKey(skillType);
    }

    public void addSkill(SkillType skillType, PlayerSkill playerSkill) {
        this.skills.put(skillType, playerSkill);
    }
}


