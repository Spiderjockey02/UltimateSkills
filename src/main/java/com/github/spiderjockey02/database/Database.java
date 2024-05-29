package com.github.spiderjockey02.database;

import com.github.spiderjockey02.enums.SkillType;
import com.github.spiderjockey02.objects.PlayerData;
import com.github.spiderjockey02.objects.PlayerSkill;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public abstract class Database {

    public abstract void closeConnection() throws SQLException;

    public abstract PlayerData fetchPlayerByUUID(UUID playerId);

    public abstract Integer fetchPlayerCount();

    public abstract Map<SkillType, PlayerSkill> fetchPlayerSkills(UUID playerId);

    public abstract PlayerData addPlayer(UUID playerId);

    public abstract Integer addPlayerSkill(UUID playerId, SkillType type);

    public abstract Map<SkillType, PlayerSkill> updatePlayerSkill(UUID playerId, SkillType type, Integer newLevel, Integer newPoint) throws SQLException;

    public abstract Integer updatePlayer(UUID playerId, Integer newPoints);

    public abstract List<PlayerData> getTopPlayers();

    public abstract List<PlayerData> getTopPlayersBySkillType(SkillType skillType);
}
