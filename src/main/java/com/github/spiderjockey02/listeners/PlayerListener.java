package com.github.spiderjockey02.listeners;

import com.github.spiderjockey02.UltimateSkills;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {
    private final UltimateSkills plugin;

    public PlayerListener(UltimateSkills plugin) {
        this.plugin = plugin;
    }

    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        this.plugin.getSkillManager().getPlayerData(player.getUniqueId());
    }

    public void onPlayerQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        this.plugin.getSkillManager().removePlayer(player.getUniqueId());
    }
}
