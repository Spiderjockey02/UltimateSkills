package com.github.spiderjockey02.objects;

import com.github.spiderjockey02.UltimateSkills;
import com.github.spiderjockey02.enums.SkillType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerData {
    private final UUID uuid;
    private Integer totalPoints;
    public final Map<SkillType, PlayerSkill> skills = new HashMap<>();

    public PlayerData(String uuid, Integer totalPoints) {
        this.totalPoints = totalPoints;
        this.uuid = UUID.fromString(uuid);
    }

    public UUID getUuid() {
        return uuid;
    }

    public Integer getTotalPoints() {
        return totalPoints;
    }

    public void updateTotalPoints(Integer points) {
        this.totalPoints+= points;
        UltimateSkills.getInstance().getDatabaseManager().updatePlayer(this.uuid, this.totalPoints);
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


