package com.github.spiderjockey02.addons;

import com.craftaro.ultimatestacker.api.UltimateStackerApi;
import com.craftaro.ultimatestacker.api.stack.entity.EntityStack;
import com.github.spiderjockey02.UltimateSkills;
import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.java.JavaPlugin;

public class MobStackingAPIManager {

    public Integer getSizeOfStack(LivingEntity entity) {
        if (JavaPlugin.getPlugin(UltimateSkills.class).getServer().getPluginManager().isPluginEnabled("UltimateStacker")) {
            EntityStack stacked = UltimateStackerApi.getEntityStackManager().getStackedEntity(entity);
            return stacked.getAmount();
        } else {
            return 1;
        }
    }
}
