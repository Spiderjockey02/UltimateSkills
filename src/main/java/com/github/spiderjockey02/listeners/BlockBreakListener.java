package com.github.spiderjockey02.listeners;

import com.github.spiderjockey02.UltimateSkills;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockBreakListener implements Listener  {
    private final UltimateSkills plugin;
    public BlockBreakListener(UltimateSkills ultimateSkills) {
        this.plugin = ultimateSkills;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent e) {
        // Fetch block and user who broke it
        Block block = e.getBlock();
        Player player = e.getPlayer();

        try {
            if (e.isCancelled()) return;
            plugin.getSkillManager().manageBlockPoints(player, block);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
