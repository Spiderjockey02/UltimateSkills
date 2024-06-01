package com.github.spiderjockey02.listeners;

import com.github.spiderjockey02.UltimateSkills;
import com.github.spiderjockey02.addons.MobStackingAPIManager;
import com.github.spiderjockey02.enums.SkillType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class EntityDeathListener implements Listener {
    private final UltimateSkills plugin;

    public EntityDeathListener(UltimateSkills ultimateSkills) {
        this.plugin = ultimateSkills;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntity() instanceof Player) return;
        if (event.getEntity().getKiller() == null) return;
        Player player = event.getEntity().getKiller();
        if (player == null) return;

        // Check for any stacking plugins resulting in more mobs being killed
        int entitySize = 1;
        if (plugin.getConfigManager().isMobStackRespected()) {
            MobStackingAPIManager mobStackingAPIManager = new MobStackingAPIManager();
            entitySize = mobStackingAPIManager.getSizeOfStack(event.getEntity());
        }

        for (int i = 0; i < entitySize; i++) {
            // Add points to player's Combat skills
            plugin.getSkillManager().addPoints(player.getUniqueId(), SkillType.COMBAT);
        }
    }
}
