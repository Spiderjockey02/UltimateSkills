package com.github.spiderjockey02.gui;

import com.github.spiderjockey02.UltimateSkills;
import com.github.spiderjockey02.enums.SkillType;
import com.github.spiderjockey02.managers.SkillManager;
import com.github.spiderjockey02.objects.playerData;
import com.github.spiderjockey02.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ProfileGUI implements GUI {
    private final UUID uuid;


    public ProfileGUI(Player player) {
        this.uuid = player.getUniqueId();
    }

    // Build the Main Profile GUI
    public Inventory getInventory() {
        SkillManager skillManager = UltimateSkills.getInstance().getSkillManager();
        playerData data = skillManager.getPlayerData(this.uuid);

        Inventory inventory = Bukkit.createInventory(this, 27, StringUtils.color("&lSkills"));
        List<ItemStack> items = this.createSkillItem(data, skillManager);
        int index = 0;
        for (ItemStack item : items) {
            inventory.setItem(index * 2, item);
            index++;
        }
        return inventory;
    }

    @Override
    public void onInventoryClick(InventoryClickEvent e) {
        Player player = (Player)e.getWhoClicked();
        int slot = e.getSlot();
        e.setCancelled(true);

        if (slot == 0) {
            player.openInventory(new LevelGUI(this.uuid, SkillType.MINING).getInventory());
        }
    }

    private List<ItemStack> createSkillItem(playerData data, SkillManager skillManager) {
        List<Material> materials = List.of(Material.STONE, Material.WHEAT, Material.DIAMOND_SWORD, Material.FISHING_ROD, Material.DIAMOND_AXE);
        List<ItemStack> items = new ArrayList<>();
        int index = 0;
        for (SkillType skillType : SkillType.values()) {
            // Get player stats
            int currentXp = data.skills.get(skillType).getXp();
            int currentLvl = data.skills.get(skillType).getLevel();

            // Create item
            ItemStack item = new ItemStack(materials.get(index));
            ItemMeta itemStackMeta = item.getItemMeta();
            itemStackMeta.setDisplayName(StringUtils.color("&F&l" + skillType.getName()));

            // Create lore to display player stats
            int XpNeeded = UltimateSkills.getInstance().getConfigManager().levels.get(currentLvl+1).getXpNeeded();

            ArrayList<String> lore = new ArrayList<String>();
            lore.add(StringUtils.color("&fLevel: " + currentLvl));
            lore.add(StringUtils.color("&fExperience: " +currentXp));
            lore.add(StringUtils.color("&fProgress: &8[&r" + StringUtils.getProgressBar(currentXp, XpNeeded, 20, '|', "&a", "&c") + "&8]"));
            lore.add("");
            lore.add(StringUtils.color("&fRanking: &a#" + skillManager.getPlayerPosBySkillType(this.uuid, skillType) + " &f/ &c" + skillManager.getTotalPlayers(skillType)));
            lore.add("");
            lore.add(StringUtils.color("&f(( Click to view " + skillType.getCapitalise()  + " skill! ))"));

            // Add the data to the item
            itemStackMeta.setLore(lore);
            item.setItemMeta(itemStackMeta);
            items.add(item);
            index++;
        }
        return items;
    }
}
