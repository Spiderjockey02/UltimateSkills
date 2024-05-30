package com.github.spiderjockey02.gui;

import com.github.spiderjockey02.UltimateSkills;
import com.github.spiderjockey02.enums.SkillType;
import com.github.spiderjockey02.managers.SkillManager;
import com.github.spiderjockey02.objects.PlayerData;
import com.github.spiderjockey02.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import java.util.ArrayList;
import java.util.UUID;
import java.util.List;

public class TopGUI implements GUI {
    private final UUID uuid;

    public TopGUI(Player player) {
        this.uuid = player.getUniqueId();
    }

    @Override
    public void onInventoryClick(InventoryClickEvent e) {
        e.setCancelled(true);
    }

    @Override
    public Inventory getInventory() {
        SkillManager skillManager =  UltimateSkills.getInstance().getSkillManager();
        Inventory inventory = Bukkit.createInventory(this, 36, StringUtils.color("&lSKILL TOP"));
        List<PlayerData> topPlayers = skillManager.getTopPlayers();

        int index = 0;
        List<Integer> positions = List.of(4,12,14,20,24);
        for (PlayerData playerData : topPlayers) {
            if (index > 4) break;
            ItemStack head = this.createHead(playerData);
            inventory.setItem(positions.get(index), head);
            index++;
        }

        inventory.setItem(31, this.createHead(skillManager.getPlayerData(this.uuid)));
        return inventory;
    }

    private ItemStack createHead(PlayerData playerData) {
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        // Get player head
        SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
        OfflinePlayer player = Bukkit.getOfflinePlayer(playerData.getUuid());
        skullMeta.setOwningPlayer(player);

        // Add title
        skullMeta.setDisplayName(StringUtils.color("&a" + player.getName()));

        // Add Lore
        ArrayList<String> lore = new ArrayList<String>();
        lore.add(StringUtils.color("&fExperience: " + playerData.getTotalPoints()));
        skullMeta.setLore(lore);
        skull.setItemMeta(skullMeta);

        return skull;
    }
}
