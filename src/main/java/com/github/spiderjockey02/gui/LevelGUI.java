package com.github.spiderjockey02.gui;

import com.github.spiderjockey02.UltimateSkills;
import com.github.spiderjockey02.enums.SkillType;
import com.github.spiderjockey02.managers.SkillManager;
import com.github.spiderjockey02.objects.LevelData;
import com.github.spiderjockey02.objects.PlayerSkillData;
import com.github.spiderjockey02.objects.playerData;
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
        playerData player =  skillManager.getPlayerData(this.uuid);

        // Create inventory and layout
        Inventory inventory = Bukkit.createInventory(this, 45, StringUtils.color("&lSKILL " + this.type.getName()));
        List<Integer> positions = List.of(1,10,19,28,29,30,21,12,3,4,5,14,23,32,33,34,25,16,7);

        // Fetch levels from config file
        int index = 0;
        Map<Integer, LevelData> levels = UltimateSkills.getInstance().getConfigManager().levels;
        for (Map.Entry<Integer, LevelData> level : levels.entrySet()) {
            ItemStack item = this.createItem(player.skills.get(this.type), level.getValue(), index+1);
            inventory.setItem(positions.get(index), item);
            index++;
        }

        // Add Info stats and then return inventory for displaying
        inventory.setItem(40, this.createInfoItem(player));
        return inventory;
    }

    private ItemStack createItem(PlayerSkillData skillData, LevelData leveldata, Integer level) {
        ItemStack item;
        if (skillData.getLevel() >= level) {
            item = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
        } else if (skillData.getLevel()+1 == level) {
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
        lore.add(StringUtils.color("&fExperience: " + skillData.getXp() +" / " + leveldata.getXpNeeded()));
        lore.add(StringUtils.color("&fProgress: &8[&r" + StringUtils.getProgressBar(skillData.getXp() , leveldata.getXpNeeded(), 20, '|', "&a", "&c") + "&8]"));
        lore.add("");
        lore.add(StringUtils.color("&fRewards:"));
        lore.addAll(StringUtils.color(leveldata.getLore().stream().map(s -> "&f- " + s).collect(Collectors.toList())));

        // Add the data to the item
        itemStackMeta.setLore(lore);
        item.setItemMeta(itemStackMeta);
        return item;
    }

    private ItemStack createInfoItem(playerData data) {
        ItemStack item = new ItemStack(Material.BOOK);
        ItemMeta itemStackMeta = item.getItemMeta();
        // Create item
        itemStackMeta.setDisplayName(StringUtils.color("&fInfo"));

        // Create lore for player stats
        ArrayList<String> lore = new ArrayList<String>();
        lore.add(StringUtils.color("&fProgress:"));
        lore.add(StringUtils.color("&f" + data.skills.get(this.type).getXp()));
        lore.add(StringUtils.color("&f" + data.skills.get(this.type).getLevel()));

        itemStackMeta.setLore(lore);
        item.setItemMeta(itemStackMeta);
        return item;
    }
}
