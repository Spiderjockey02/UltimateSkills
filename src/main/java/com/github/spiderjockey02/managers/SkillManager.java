package com.github.spiderjockey02.managers;

import com.github.spiderjockey02.UltimateSkills;
import com.github.spiderjockey02.enums.SkillType;
import com.github.spiderjockey02.objects.PlayerSkillData;
import com.github.spiderjockey02.objects.playerData;
import com.github.spiderjockey02.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class SkillManager {
    private final UltimateSkills plugin;
    public final Map<UUID, playerData> tempData = new HashMap<>();

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
            if (block.getBlockData() instanceof Ageable) {
                Ageable ageable = (Ageable) block.getBlockData();
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
        if (this.tempData.containsKey(playerId)) {
            playerData player = this.tempData.get(playerId);
            player.skills.get(type).addXp(points);
            this.tempData.put(playerId, player);
        } else {
            this.tempData.put(playerId, new playerData(playerId));
        }

        // Check for level up
        int level = this.tempData.get(playerId).skills.get(type).getLevel();
        int currentXp = this.tempData.get(playerId).skills.get(type).getXp();
        if (currentXp > this.plugin.getConfigManager().levels.get(level+1).getXpNeeded()) {
            int newLevel = level + 1;
            // Send level up message to player
            Player player = this.plugin.getServer().getPlayer(playerId);
            player.sendMessage(StringUtils.color("&6&lSkills &7» You have just levelled up to: &l&f" + newLevel + "&7."));

            // Get commands to run for rewards
            List<String> commands = this.plugin.getConfigManager().levels.get(level+1).getCommands();
            for (String command : commands) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("%player%", player.getName()));
            }

            // Update player's level
            this.tempData.get(playerId).skills.get(type).setLevel(newLevel);
        }
    }

    public playerData getPlayerData(UUID playerId) {
        if (this.tempData.containsKey(playerId)) {
            return this.tempData.get(playerId);
        } else {
            playerData newPlayer = new playerData(playerId);
            this.tempData.put(playerId, newPlayer);
            return this.tempData.get(playerId);
        }
    }

    public Integer getTotalPlayers(SkillType type) {
        return Math.toIntExact(tempData.values().stream()
                .map(data -> data.skills.get(type))
                .filter(score -> score.getXp() > 0)
                .count());
    }

    public Integer getPlayerPosBySkillType(UUID playerId, SkillType type) {
        List<Map.Entry<UUID, PlayerSkillData>> sortedPlayers = tempData.entrySet().stream()
                .map(entry -> Map.entry(entry.getKey(), entry.getValue().skills.get(type)))
                .filter(entry -> entry.getValue() != null)
                .sorted((e1, e2) -> Integer.compare(e2.getValue().getXp(), e1.getValue().getXp()))// Sort by score in descending order
                .collect(Collectors.toList());

        for (int i = 0; i < sortedPlayers.size(); i++) {
            if (sortedPlayers.get(i).getKey().equals(playerId)) {
                return i + 1; // Rank is 1-based
            }
        }

        return 0;
    }

    public List<playerData> getTopPlayers(SkillType skillType, int topN) {
        return tempData.values().stream()
                .filter(data -> data.skills.get(skillType) != null)
                .sorted(Comparator.comparingInt(data -> data.skills.get(skillType).getXp()))
                .limit(topN)
                .collect(Collectors.toList());
    }
}
