package com.github.spiderjockey02.database.implementations;

import com.github.spiderjockey02.database.Database;
import com.github.spiderjockey02.enums.SkillType;
import com.github.spiderjockey02.objects.PlayerData;
import com.github.spiderjockey02.objects.PlayerSkill;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MySQLDatabase extends Database {

    public void closeConnection() throws SQLException {

    }


    public PlayerData fetchPlayerByUUID(UUID playerId) {
        return null;
    }

    public Integer fetchPlayerCount() {
        return 0;
    }

    public Map<SkillType, PlayerSkill> fetchPlayerSkills(UUID playerId) {
        return Map.of();
    }

    public PlayerData addPlayer(UUID playerId) {
        return null;
    }

    public Integer addPlayerSkill(UUID playerId, SkillType type) {
        return 0;
    }

    public Map<SkillType, PlayerSkill> updatePlayerSkill(UUID playerId, SkillType type, Integer newLevel, Integer newPoint) throws SQLException {
        return Map.of();
    }

    public Integer updatePlayer(UUID playerId, Integer newPoints) {
        return 0;
    }

    public List<PlayerData> getTopPlayers() {
        return List.of();
    }

    public List<PlayerData> getTopPlayersBySkillType(SkillType skillType) {
        return List.of();
    }
}
