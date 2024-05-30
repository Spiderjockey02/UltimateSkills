package com.github.spiderjockey02.objects;

import com.github.spiderjockey02.UltimateSkills;
import com.github.spiderjockey02.enums.SkillType;
import java.sql.SQLException;
import java.util.UUID;

public class PlayerSkill {
    private final UUID uuid;
    private final SkillType type;
    private Integer level;
    private Integer points;

    public PlayerSkill(String uuid, SkillType type, Integer level, Integer points) {
        this.uuid = UUID.fromString(uuid);;
        this.type = type;
        this.level = level;
        this.points = points;
    }

    public void addPoints(Integer points) {
        Integer newPoints = points + this.points;
        try {
            // Update skill type level and overall level
            UltimateSkills.getInstance().getDatabaseManager().updatePlayerSkill(this.uuid, this.type, this.level, newPoints);
            this.points = newPoints;
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateLevel(Integer newLevel){
        try {
            // Update skill type level and overall level
            UltimateSkills.getInstance().getDatabaseManager().updatePlayerSkill(this.uuid, this.type, newLevel, this.points);
            this.level = newLevel;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Integer getPoints() {
        return points;
    }

    public Integer getLevel() {
        return level;
    }

    public SkillType getType() {
        return type;
    }

    public UUID getUuid() {
        return uuid;
    }

    @Override
    public String toString() {
        return "UUID: " + this.uuid.toString() + ", type: " + this.type.toString() + ", level: " + this.level + ", points: " + this.points;
    }
}
