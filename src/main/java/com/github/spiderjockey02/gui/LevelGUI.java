package com.github.spiderjockey02.gui;

import com.github.spiderjockey02.UltimateSkills;
import com.github.spiderjockey02.enums.SkillType;
import com.github.spiderjockey02.managers.SkillManager;
import com.github.spiderjockey02.objects.LevelData;
import com.github.spiderjockey02.objects.PlayerData;
import com.github.spiderjockey02.objects.PlayerSkill;
import com.github.spiderjockey02.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class LevelGUI implements GUI {
    private final UUID uuid;
    private final SkillType type;


    public LevelGUI(UUID playerId, SkillType type) {
        this.uuid = playerId;
        this.type = type;
    }

    @Override
    public void onInventoryClick(InventoryClickEvent e) {
        e.setCancelled(true);
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        SkillManager skillManager = UltimateSkills.getInstance().getSkillManager();
        PlayerData player =  skillManager.getPlayerData(this.uuid);

        // Create inventory and layout
        Inventory inventory = Bukkit.createInventory(this, 45, StringUtils.color("&lSKILL " + this.type.getName()));
        List<Integer> positions = List.of(1,10,19,28,29,30,21,12,3,4,5,14,23,32,33,34,25,16,7);

        // Fetch levels from config file
        int index = 0;
        Map<Integer, LevelData> levels = UltimateSkills.getInstance().getConfigManager().levels;
        for (Map.Entry<Integer, LevelData> level : levels.entrySet()) {
            ItemStack item = this.createItem(player.getSkill(this.type), level.getValue(), index+1);
            inventory.setItem(positions.get(index), item);
            index++;
        }

        // Add Info stats and then return inventory for displaying
        inventory.setItem(40, this.createInfoItem(player.getSkill(this.type)));
        return inventory;
    }

    private ItemStack createItem(PlayerSkill skillData, LevelData leveldata, Integer level) {
        ItemStack item;
        Integer userLevel = 0, userPoints = 0;
        if (skillData != null) {
            userLevel = skillData.getLevel();
            userPoints = skillData.getPoints();
        }

        if (userLevel >= level) {
            item = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
        } else if (userLevel+1 == level) {
            item = new ItemStack(Material.ORANGE_STAINED_GLASS_PANE);
        } else {
            item = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        }

        ItemMeta itemStackMeta = item.getItemMeta();
        // Create item
        itemStackMeta.setDisplayName(StringUtils.color("&F&lLevel > " + level));

        // Create lore for player stats
        ArrayList<String> lore = new ArrayList<String>();
        lore.add(StringUtils.color("&fProgress:"));
        lore.add(StringUtils.color("&fExperience: " + userPoints +" / " + leveldata.getXpNeeded()));
        lore.add(StringUtils.color("&fProgress: &8[&r" + StringUtils.getProgressBar(userPoints, leveldata.getXpNeeded(), 20, '|', "&a", "&c") + "&8]"));
        lore.add("");
        lore.add(StringUtils.color("&fRewards:"));
        lore.addAll(StringUtils.color(leveldata.getLore().stream().map(s -> "&f- " + s).collect(Collectors.toList())));

        // Add the data to the item
        itemStackMeta.setLore(lore);
        item.setItemMeta(itemStackMeta);
        return item;
    }

    private ItemStack createInfoItem(PlayerSkill data) {
        ItemStack item = new ItemStack(Material.BOOK);

        Integer userLevel = 0, userPoints = 0;
        if (data != null) {
            userLevel = data.getLevel();
            userPoints = data.getPoints();
        }

        ItemMeta itemStackMeta = item.getItemMeta();
        // Create item
        itemStackMeta.setDisplayName(StringUtils.color("&fInfo"));

        // Create lore for player stats
        ArrayList<String> lore = new ArrayList<String>();
        lore.add(StringUtils.color("&fProgress:"));
        lore.add(StringUtils.color("&fXP: " + userPoints));
        lore.add(StringUtils.color("&fLevel: " + userLevel));

        itemStackMeta.setLore(lore);
        item.setItemMeta(itemStackMeta);
        return item;
    }
}
