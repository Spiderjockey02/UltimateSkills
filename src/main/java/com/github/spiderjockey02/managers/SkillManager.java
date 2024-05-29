package com.github.spiderjockey02.managers;

import com.github.spiderjockey02.UltimateSkills;
import com.github.spiderjockey02.enums.SkillType;
import com.github.spiderjockey02.objects.PlayerData;
import com.github.spiderjockey02.objects.PlayerSkill;
import com.github.spiderjockey02.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Player;
import java.util.*;

public class SkillManager {
    private final UltimateSkills plugin;
    public final Map<UUID, PlayerData> tempData = new HashMap<>();

    public SkillManager(UltimateSkills plugin) {
        this.plugin = plugin;
    }

    public void manageBlockPoints(Player player, Block block) {
        // Ignore the interaction of player was in creative
        if (player.getGameMode().equals(GameMode.CREATIVE)) return;

        // Now check if it was an ore, wood or farm item
        Material material = block.getType();
        if (material == Material.OAK_LOG) {
            this.addPoints(player.getUniqueId(), SkillType.LUMBERING);
        } else if (material == Material.STONE) {
            this.addPoints(player.getUniqueId(), SkillType.MINING);
        } else if (material == Material.WHEAT) {
            // Check if the crop is fully grown
            if (block.getBlockData() instanceof Ageable ageable) {
                if (ageable.getAge() == ageable.getMaximumAge()) {
                    // The wheat is fully grown
                    this.addPoints(player.getUniqueId(), SkillType.FARMING);
                }
            }
        }
    }

    public void addPoints(UUID playerId, SkillType type) {
        // Fetch how many points to add from config file
        int points = this.plugin.getConfigManager().fetchPoints(type);

        // Check if it's first time or not
        PlayerData playerData = this.getPlayerData(playerId);


        // Fetch or create player skill data
        PlayerSkill playerSkill = this.getPlayerSkill(playerId,type);

        // Add XP to user and refresh cache
        playerSkill.addPoints(points);
        playerData.updateTotalPoints(points);
        this.tempData.put(playerId, playerData);

        // Check for level up
        int level = playerSkill.getLevel();
        int currentXp = playerSkill.getPoints();
        if (currentXp >= this.plugin.getConfigManager().levels.get(level+1).getXpNeeded()) {
            int newLevel = level + 1;
            // Send level up message to player
            Player player = this.plugin.getServer().getPlayer(playerId);
            player.sendMessage(StringUtils.color("&6&lSkills &7» You have just levelled up to: &l&f" + newLevel + "&7."));

            // Get commands to run for rewards
            List<String> commands = this.plugin.getConfigManager().levels.get(newLevel).getCommands();
            for (String command : commands) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("%player%", player.getName()));
            }

            // Update player's level
            this.tempData.get(playerId).getSkill(type).updateLevel(newLevel);
        }
    }

    public PlayerData getPlayerData(UUID playerId) {
        if (!this.tempData.containsKey(playerId)) {
            // Fetch player from Database
            PlayerData player = this.plugin.getDatabaseManager().fetchPlayerByUUID(playerId);
            if (player == null) player = this.plugin.getDatabaseManager().addPlayer(playerId);

            // Fetch their skills from database
            Map<SkillType, PlayerSkill> skills = this.plugin.getDatabaseManager().fetchPlayerSkills(player.getUuid());
            for (PlayerSkill skill : skills.values()) {
                player.addSkill(skill.getType(), skill);
            }
            this.tempData.put(playerId, player);
        }

        return this.tempData.get(playerId);
    }

    public PlayerSkill getPlayerSkill(UUID playerId, SkillType type) {
        // Check from cache
        PlayerData playerData = this.getPlayerData(playerId);
        if (playerData.hasSkill(type)) return playerData.getSkill(type);

        // Fetch from database
        Map<SkillType, PlayerSkill> playerSkills = this.plugin.getDatabaseManager().fetchPlayerSkills(playerId);
        if (!playerSkills.containsKey(type)) {
            Integer a = this.plugin.getDatabaseManager().addPlayerSkill(playerId, type);
            playerSkills = this.plugin.getDatabaseManager().fetchPlayerSkills(playerId);
        }

        playerData.addSkill(type, playerSkills.get(type));
        this.tempData.put(playerId, playerData);
        return playerData.getSkill(type);
    }

    public Integer getTotalPlayers(SkillType type) {
        return this.plugin.getDatabaseManager().fetchPlayerCount();

    }

    public Integer getPlayerPosBySkillType(UUID playerId, SkillType type) {
        if (!this.tempData.containsKey(playerId)) return -1;

        // Fetch from database
        List<Map.Entry<UUID, PlayerSkill>> sortedPlayers = this.tempData.entrySet().stream()
                .filter(entry -> entry.getKey() != null && entry.getValue().getSkill(type) != null)
                .map(entry -> Map.entry(entry.getKey(), entry.getValue().getSkill(type)))
                .sorted((e1, e2) -> Integer.compare(e2.getValue().getPoints(), e1.getValue().getPoints()))// Sort by score in descending order
                .toList();

        for (int i = 0; i < sortedPlayers.size(); i++) {
            if (sortedPlayers.get(i).getKey().equals(playerId)) {
                return i + 1; // Rank is 1-based
            }
        }
        return -1;
    }

    public List<PlayerData> getTopPlayersBySkillType(SkillType skillType) {
        // Fetch from database instead
        return this.plugin.getDatabaseManager().getTopPlayersBySkillType(skillType);
    }

    public List<PlayerData> getTopPlayers() {
        return this.plugin.getDatabaseManager().getTopPlayers();
    }
}
