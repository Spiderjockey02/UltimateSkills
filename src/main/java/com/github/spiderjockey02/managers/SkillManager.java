package com.github.spiderjockey02.managers;

import com.github.spiderjockey02.UltimateSkills;
import com.github.spiderjockey02.addons.VaultAPIManager;
import com.github.spiderjockey02.enums.SkillType;
import com.github.spiderjockey02.objects.ConfigSkillData;
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
import java.util.stream.Collectors;

public class SkillManager {
    private final UltimateSkills plugin;
    public final Map<UUID, PlayerData> tempData = new HashMap<>();

    public SkillManager(UltimateSkills plugin) {
        this.plugin = plugin;
    }

    public void manageBlockPoints(Player player, Block block) {
        // Ignore the interaction of player was in creative
        if (player.getGameMode().equals(GameMode.CREATIVE)) return;

        // Get list of blocks
        ConfigManager configManager = plugin.getConfigManager();
        List<Material> MiningBlocks = configManager.getSkill(SkillType.MINING).getBlocks();
        List<Material> LumberingBlocks = configManager.getSkill(SkillType.LUMBERING).getBlocks();
        List<Material> farmingBlocks = configManager.getSkill(SkillType.FARMING).getBlocks();

        // Now check if it was an ore, wood or farm item
        Material material = block.getType();
        if (configManager.isDebugEnabled()) plugin.getLogger().info(player.getName() + " has just mined block: " + material);

        // For debugging purposes and adding points to correct skill type
        if (MiningBlocks.contains(material) && configManager.getSkill(SkillType.MINING).isEnabled()) {
            if (configManager.isDebugEnabled()) plugin.getLogger().info("Block was found in MiningBlock array, giving them points.");
            this.addPoints(player.getUniqueId(), SkillType.MINING);
        }

        // For debugging purposes and adding points to correct skill type
        if (LumberingBlocks.contains(material) && configManager.getSkill(SkillType.LUMBERING).isEnabled()) {
            if (configManager.isDebugEnabled()) plugin.getLogger().info("Block was found in LumberingBlocks array, giving them points.");
            this.addPoints(player.getUniqueId(), SkillType.LUMBERING);
        }

        // For debugging purposes and adding points to correct skill type
        if (farmingBlocks.contains(material) && configManager.getSkill(SkillType.FARMING).isEnabled()) {
            if (configManager.isDebugEnabled()) plugin.getLogger().info("Block was found in farmingBlocks array, check age.");
            // Add another check to ensure the crop was fully grown
            if (block.getBlockData() instanceof Ageable) {
                Ageable ageable = (Ageable) block.getBlockData();
                if (ageable.getAge() == ageable.getMaximumAge()) {
                    if (configManager.isDebugEnabled()) plugin.getLogger().info("Crop was fully grown, giving them points.");
                    // The wheat is fully grown
                    this.addPoints(player.getUniqueId(), SkillType.FARMING);
                }
            }
        }
    }

    public void addPoints(UUID playerId, SkillType type) {
        // Get Config manager
        ConfigSkillData skillData = plugin.getConfigManager().skills.get(type);

        // Fetch how many points to add from config file
        int points = skillData.getDefaultPoints();

        // Check if it's first time or not
        PlayerData playerData = this.getPlayerData(playerId);

        // Fetch or create player skill data
        PlayerSkill playerSkill = this.getPlayerSkill(playerId, type);

        // Add XP to user and refresh cache
        if (plugin.getConfigManager().isDebugEnabled()) plugin.getLogger().info("Giving player:" + playerId + " " + points + " to skill: " + type);
        playerSkill.addPoints(points);
        playerData.updateTotalPoints(points);
        this.tempData.put(playerId, playerData);

        // Check for level up
        checkLevelUp(playerData, type);
    }

    public void checkLevelUp(PlayerData playerData, SkillType type) {
        // Fetch or create player skill data
        PlayerSkill playerSkill = this.getPlayerSkill(playerData.getUuid(), type);

        // Get Config manager
        ConfigSkillData skillData = plugin.getConfigManager().skills.get(type);
        // Check for level up
        int level = playerSkill.getLevel();
        int currentXp = playerSkill.getPoints();
        if (currentXp >= skillData.getLevel(level+1).getXpNeeded()) {
            int newLevel = level + 1;

            // Send level up message to player
            if (plugin.getConfigManager().isDebugEnabled()) plugin.getLogger().info("Player: " + playerData.getUuid() + " has just leveled up to " + newLevel);
            Player player = this.plugin.getServer().getPlayer(playerData.getUuid());
            player.sendMessage(StringUtils.color("&6&lSkills &7» You have just levelled up to: &l&f" + newLevel + "&7."));

            // Get commands to run for rewards
            List<String> commands = skillData.getLevel(level+1).getCommands();
            for (String command : commands) {
                String newCommand = command.replace("%player%", player.getName());

                // Check for inbuilt API request
                if (newCommand.startsWith("[money]")) {
                    // Use Vault API to give money directly without using commands.
                    String[] args = newCommand.split("\\s+");
                    VaultAPIManager vaultAPIManager = new VaultAPIManager();
                    vaultAPIManager.deposit(player.getUniqueId(), Integer.parseInt(args[2]));
                } else if (newCommand.startsWith("[message]")) {
                    // Send a message directly to the user
                    player.sendMessage(newCommand.replace("[message] ", ""));
                } else {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), newCommand);
                }
            }

            // Update player's level
            this.tempData.get(playerData.getUuid()).getSkill(type).updateLevel(newLevel);
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
                .map(entry -> new AbstractMap.SimpleEntry<>(entry.getKey(), entry.getValue().getSkill(type)))
                .sorted((e1, e2) -> Integer.compare(e2.getValue().getPoints(), e1.getValue().getPoints()))
                .collect(Collectors.toList());

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

    public void removePlayer(UUID playerId) {
        this.tempData.remove(playerId);
    }
    public List<PlayerData> getTopPlayers() {
        return this.plugin.getDatabaseManager().getTopPlayers();
    }
}
