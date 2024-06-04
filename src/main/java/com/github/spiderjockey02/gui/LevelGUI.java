package com.github.spiderjockey02.gui;

import com.github.spiderjockey02.UltimateSkills;
import com.github.spiderjockey02.enums.SkillType;
import com.github.spiderjockey02.managers.SkillManager;
import com.github.spiderjockey02.objects.ConfigSkillData;
import com.github.spiderjockey02.objects.LevelData;
import com.github.spiderjockey02.objects.PlayerData;
import com.github.spiderjockey02.objects.PlayerSkill;
import com.github.spiderjockey02.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
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
    private final Integer page;

    public LevelGUI(UUID playerId, SkillType type) {
        this.uuid = playerId;
        this.type = type;
        this.page = 0;
    }

    public LevelGUI(UUID playerId, SkillType type, int page) {
        this.uuid = playerId;
        this.type = type;
        this.page = page;
    }

    @Override
    public void onInventoryClick(InventoryClickEvent e) {
        Player player = (Player)e.getWhoClicked();
        int slot = e.getSlot();
        e.setCancelled(true);

        switch (slot) {
            case 38:
                int prevPage = Math.max(this.page - 1, 0);
                player.openInventory(new LevelGUI(this.uuid, this.type, prevPage).getInventory());
                break;
            case 42:
                int maxPage = (int) Math.ceil((double) UltimateSkills.getInstance().getConfigManager().getSkill(type).getMaxLevel() /19) - 1;
                int nextPage = Math.min(this.page + 1, maxPage);
                player.openInventory(new LevelGUI(this.uuid, this.type, nextPage).getInventory());
                break;
        }
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        return createInventory();
    }

    private Inventory createInventory() {
        SkillManager skillManager = UltimateSkills.getInstance().getSkillManager();
        ConfigSkillData configSkillData = UltimateSkills.getInstance().getConfigManager().getSkill(type);
        PlayerData player =  skillManager.getPlayerData(this.uuid);


        // Create inventory and layout
        Inventory inventory = Bukkit.createInventory(this, 45, StringUtils.color("&lSKILL " + this.type.getName()));
        Map<Integer, LevelData> levels = configSkillData.getLevels();
        List<Integer> positions = Arrays.asList(0,1,10,19,28,29,30,21,12,3,4,5,14,23,32,33,34,25,16,7);

        // Fetch levels from config file
        int index = 1;
        for (int level = 1; level <= levels.size(); level++) {
            if (level < 20) {
                int offSet = (int) Math.floor(this.page * 19);
                if (levels.containsKey(offSet + index)) {
                    ItemStack item = this.createItem(player.getSkill(this.type), levels.get(offSet + index), offSet + index);
                    item.setAmount(Math.min(offSet + index, 64));
                    inventory.setItem(positions.get(index), item);
                    index++;
                }
            }
        }
        // Previous page arrow
        inventory.setItem(38, this.createPreviousPageItem((int) Math.ceil((double) configSkillData.getMaxLevel() / 19)));

        // Next page arrow
        inventory.setItem(42, this.createNextPageItem((int) Math.ceil((double) configSkillData.getMaxLevel() / 19)));

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
        lore.add(StringUtils.color("&fExperience: " + StringUtils.formatNumber(userPoints) +" / " + StringUtils.formatNumber(leveldata.getXpNeeded())));
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
        lore.add(StringUtils.color("&fXP: " + StringUtils.formatNumber(userPoints)));
        lore.add(StringUtils.color("&fLevel: " + StringUtils.formatNumber(userLevel)));

        itemStackMeta.setLore(lore);
        item.setItemMeta(itemStackMeta);
        return item;
    }

    private ItemStack createNextPageItem(int maxPages) {
        ItemStack item = new ItemStack(Material.ARROW);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(StringUtils.color("&fNext Page"));

        ArrayList<String> lore = new ArrayList<>();
        lore.add(StringUtils.color("&6Click for the next page"));
        lore.add("");
        lore.add(StringUtils.color("&fViewing " + (this.page + 1) + " of " + maxPages));

        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        return item;
    }

    private ItemStack createPreviousPageItem(int maxPages) {
        ItemStack item = new ItemStack(Material.ARROW);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(StringUtils.color("&fPrevious Page"));

        ArrayList<String> lore = new ArrayList<>();
        lore.add(StringUtils.color("&6Click for the previous page"));
        lore.add("");
        lore.add(StringUtils.color("&fViewing " + (this.page + 1) + " of " + maxPages));

        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        return item;
    }
}
