package com.github.spiderjockey02.addons;

import com.github.spiderjockey02.UltimateSkills;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.UUID;

public class VaultAPIManager {
    private Economy economy;

    public VaultAPIManager() {
        RegisteredServiceProvider<Economy> rsp = UltimateSkills.getInstance().getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) return;
        economy = rsp.getProvider();
    }

    public void deposit(OfflinePlayer offlinePlayer, double amount) {
        economy.depositPlayer(offlinePlayer, amount);
    }

    public void deposit(UUID uuid, double amount) {
        deposit(Bukkit.getOfflinePlayer(uuid), amount);
    }
}
