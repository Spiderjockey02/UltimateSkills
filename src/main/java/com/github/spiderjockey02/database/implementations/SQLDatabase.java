package com.github.spiderjockey02.database.implementations;

import com.github.spiderjockey02.UltimateSkills;
import com.github.spiderjockey02.database.Database;
import com.github.spiderjockey02.enums.SkillType;
import com.github.spiderjockey02.objects.PlayerData;
import com.github.spiderjockey02.objects.PlayerSkill;
import java.sql.*;
import java.util.*;

public class SQLDatabase extends Database {
    private final Connection connection;
    private final UltimateSkills plugin;

    public SQLDatabase(String path, UltimateSkills plugin) throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite:" + path);
        this.plugin = plugin;

        try (Statement statement = connection.createStatement()) {
            // Try and create Skills table
            statement.execute("CREATE TABLE IF NOT EXISTS Skills (" +
                    "uuid TEXT, " +
                    "type TEXT CHECK( type IN ('COMBAT','LUMBERING','MINING', 'FISHING', 'FARMING')), " +
                    "level INTEGER NOT NULL DEFAULT 0, " +
                    "points INTEGER NOT NULL DEFAULT 0," +
                    "PRIMARY KEY (uuid, type))");

            // Try and create Players table
            statement.execute("CREATE TABLE IF NOT EXISTS Players (" +
                    "uuid TEXT PRIMAY KEY, " +
                    "skills TEXT, " +
                    "totalPoints INTEGER NOT NULL DEFAULT 0, " +
                    "FOREIGN KEY(skills) REFERENCES Skills(uuid))");
        }
    }

    public void closeConnection() throws SQLException {

        if (connection != null && !connection.isClosed()) connection.close();
    }

    public PlayerData fetchPlayerByUUID(UUID playerId) {
        if (plugin.getConfigManager().isDebugEnabled()) plugin.getLogger().info("[DATABASE] Fetching player data for player: " + playerId);

        try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM Players WHERE uuid =?")) {
            statement.setString(1, playerId.toString());
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    String uuid = resultSet.getString("uuid");
                    int points = resultSet.getInt("totalPoints");
                    return new PlayerData(uuid, points);
                }
            }
        } catch (SQLException e) {
            plugin.getLogger().info(e.getMessage());
        }
        return null;
    }

    public Integer fetchPlayerCount() {
        if (plugin.getConfigManager().isDebugEnabled()) plugin.getLogger().info("[DATABASE] Fetching player count");

        try (PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM Players")) {
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            }
        } catch (SQLException e) {
            plugin.getLogger().info(e.getMessage());
        }
        return 0;
    }

    public Map<SkillType, PlayerSkill> fetchPlayerSkills(UUID playerId)  {
        if (plugin.getConfigManager().isDebugEnabled()) plugin.getLogger().info("[DATABASE] Fetching player skills");

        try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM Skills WHERE uuid=?")) {
            statement.setString(1, playerId.toString());
            try (ResultSet data = statement.executeQuery()) {
                Map<SkillType, PlayerSkill> skills = new HashMap<>();
                while (data.next()) {
                    String uuid = data.getString("uuid");
                    String type = data.getString("type");
                    int level = data.getInt("level");
                    int points = data.getInt("points");

                    SkillType skillType = SkillType.fromString(type);
                    skills.put(skillType, new PlayerSkill(uuid, skillType, level, points));
                }
                return skills;
            }
        } catch (SQLException e) {
            plugin.getLogger().info(e.getMessage());
        }
        return new HashMap<>();
    }

    public PlayerData addPlayer(UUID playerId) {
        if (plugin.getConfigManager().isDebugEnabled()) plugin.getLogger().info("[DATABASE] Adding player: " + playerId);
        try (PreparedStatement statement = connection.prepareStatement("INSERT INTO Players (uuid) VALUES (?)")) {
            statement.setString(1, playerId.toString());
            statement.executeUpdate();
            return fetchPlayerByUUID(playerId);
        } catch (SQLException e) {
            plugin.getLogger().info(e.getMessage());
        }
        return null;
    }

    public Integer addPlayerSkill(UUID playerId, SkillType type) {
        if (plugin.getConfigManager().isDebugEnabled()) plugin.getLogger().info("[DATABASE] Adding player skill: " + playerId);
        try (PreparedStatement statement = connection.prepareStatement("INSERT INTO Skills (uuid, type) VALUES (?, ?)")) {
            statement.setString(1, playerId.toString());
            statement.setString(2, type.toString());
            return statement.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().info(e.getMessage());
        }
        return -1;
    }

    public Map<SkillType, PlayerSkill> updatePlayerSkill(UUID playerId, SkillType type, Integer newLevel, Integer newPoint) throws SQLException {
        if (plugin.getConfigManager().isDebugEnabled()) plugin.getLogger().info("[DATABASE] Updating player's skill: " + playerId);
        try (PreparedStatement statement = connection.prepareStatement("UPDATE Skills SET level = ?, points = ? WHERE uuid = ? AND type = ?")) {
            statement.setInt(1, newLevel);
            statement.setInt(2, newPoint);
            statement.setString(3, playerId.toString());
            statement.setString(4, type.toString());
            statement.executeUpdate();
            return fetchPlayerSkills(playerId);
        }
    }

    public Integer updatePlayer(UUID playerId, Integer newPoints) {
        if (plugin.getConfigManager().isDebugEnabled()) plugin.getLogger().info("[DATABASE] Updating player: " + playerId);
        try (PreparedStatement statement = connection.prepareStatement("UPDATE Players SET totalPoints = ? WHERE uuid = ?")) {
            statement.setInt(1, newPoints);
            statement.setString(2, playerId.toString());
            return statement.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().info(e.getMessage());
        }
        return -1;
    }

    public List<PlayerData> getTopPlayers() {
        if (plugin.getConfigManager().isDebugEnabled()) plugin.getLogger().info("[DATABASE] Fetching top players");
        try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM Players ORDER BY totalPoints DESC LIMIT 10")) {
            ResultSet data = statement.executeQuery();
            List<PlayerData> players = new ArrayList<>();
            while (data.next()) {
                String uuid = data.getString("uuid");
                int points = data.getInt("totalPoints");
                players.add(new PlayerData(uuid, points));
            }
            return players;
        } catch (SQLException e) {
            plugin.getLogger().info(e.getMessage());
        }
        return new ArrayList<>();
    }

    public List<PlayerData> getTopPlayersBySkillType(SkillType skillType) {
        if (plugin.getConfigManager().isDebugEnabled()) plugin.getLogger().info("[DATABASE] Fetching top players by skill type: " + skillType);
        try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM Skills ORDER BY points DESC LIMIT 10 WHERE type = ?")) {
            statement.setString(1, skillType.toString());
            ResultSet data = statement.executeQuery();
            List<PlayerData> players = new ArrayList<>();
            while (data.next()) {
                String uuid = data.getString("uuid");
                int points = data.getInt("totalPoints");
                players.add(new PlayerData(uuid, points));
            }
            return players;
        } catch (SQLException e) {
            plugin.getLogger().info(e.getMessage());
        }
        return new ArrayList<>();
    }
}
