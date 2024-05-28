package com.github.spiderjockey02.listeners;

import com.github.spiderjockey02.UltimateSkills;
import com.github.spiderjockey02.enums.SkillType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;

public class FishingListener implements Listener {
    private final UltimateSkills plugin;

    public FishingListener(UltimateSkills ultimateSkills) {
        this.plugin = ultimateSkills;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onFishing(PlayerFishEvent e) {
        if (e.isCancelled()) return;
        // Make sure something was caught (a fish)
        if (e.getCaught() == null || (e.getState() != PlayerFishEvent.State.CAUGHT_FISH && e.getState() != PlayerFishEvent.State.CAUGHT_ENTITY)) return;

        // Add points to player's Fishing skills
        this.plugin.getSkillManager().addPoints(e.getPlayer().getUniqueId(), SkillType.FISHING);
    }
}
