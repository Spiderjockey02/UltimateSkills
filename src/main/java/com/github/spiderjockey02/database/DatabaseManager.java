package com.github.spiderjockey02.database;

import com.github.spiderjockey02.UltimateSkills;
import com.github.spiderjockey02.enums.SkillType;
import com.github.spiderjockey02.objects.PlayerData;
import com.github.spiderjockey02.objects.PlayerSkill;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DatabaseManager {
    private final Connection connection;

    public DatabaseManager(String path) throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite:" + path);

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
                    "totalLevel INTEGER NOT NULL DEFAULT 0, " +
                    "totalPoints INTEGER NOT NULL DEFAULT 0, " +
                    "FOREIGN KEY(skills) REFERENCES Skills(uuid))");
        }
    }

    public void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) connection.close();
    }

    public PlayerData fetchPlayerByUUID(UUID playerId) {
        try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM Players WHERE uuid =?")) {
            statement.setString(1, playerId.toString());
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    String uuid = resultSet.getString("uuid");
                    int level = resultSet.getInt("totalLevel");
                    int points = resultSet.getInt("totalPoints");
                    return new PlayerData(uuid, level, points);
                }
            }
        } catch (SQLException e) {
            UltimateSkills.getInstance().getLogger().info(e.getMessage());
        }
        return null;
    }

    public Integer fetchPlayerCount() {
        try (PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM Players")) {
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            }
        } catch (SQLException e) {
            UltimateSkills.getInstance().getLogger().info(e.getMessage());
        }
        return 0;
    }

    public Map<SkillType, PlayerSkill> fetchPlayerSkills(UUID playerId)  {
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
            UltimateSkills.getInstance().getLogger().info(e.getMessage());
        }
        return new HashMap<>();
    }

    public PlayerData addPlayer(UUID playerId) {
        try (PreparedStatement statement = connection.prepareStatement("INSERT INTO Players (uuid) VALUES (?)")) {
            statement.setString(1, playerId.toString());
            statement.executeUpdate();
            return fetchPlayerByUUID(playerId);
        } catch (SQLException e) {
            UltimateSkills.getInstance().getLogger().info(e.getMessage());
        }
        return null;
    }

    public Integer addPlayerSkill(UUID playerId, SkillType type) {
        try (PreparedStatement statement = connection.prepareStatement("INSERT INTO Skills (uuid, type) VALUES (?, ?)")) {
            statement.setString(1, playerId.toString());
            statement.setString(2, type.toString());
            return statement.executeUpdate();
        } catch (SQLException e) {
            UltimateSkills.getInstance().getLogger().info(e.getMessage());
        }
        return -1;
    }

    public Map<SkillType, PlayerSkill> updatePlayerSkill(UUID playerId, SkillType type, Integer newLevel, Integer newPoint) throws  SQLException {
        try (PreparedStatement statement = connection.prepareStatement("UPDATE Skills SET level = ?, points = ? WHERE uuid = ? AND type = ?")) {
            statement.setInt(1, newLevel);
            statement.setInt(2, newPoint);
            statement.setString(3, playerId.toString());
            statement.setString(4, type.toString());
            statement.executeUpdate();
            return fetchPlayerSkills(playerId);
        }
    }

    public PlayerData updatePlayer(UUID playerId, Integer newLevel, Integer newPoints) {
        try (PreparedStatement statement = connection.prepareStatement("UPDATE Players SET totalLevel = ?, totalPoints = ? WHERE uuid = ?")) {
            statement.setInt(1, newLevel);
            statement.setInt(2, newPoints);
            statement.setString(3, playerId.toString());
            ResultSet data = statement.executeQuery();
            if (data.next()) {
                String uuid = data.getString("uuid");
                int level = data.getInt("totalLevel");
                int points = data.getInt("totalPoints");
                return new PlayerData(uuid, level, points);
            }
        } catch (SQLException e) {
            UltimateSkills.getInstance().getLogger().info(e.getMessage());
        }
        return null;
    }
}
