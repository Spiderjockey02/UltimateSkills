package com.github.spiderjockey02.listeners;

import com.github.spiderjockey02.gui.GUI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class InventoryClickListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getClickedInventory() != null && event.getInventory().getHolder() != null && event.getInventory().getHolder() instanceof GUI) {
            event.setCancelled(true);
            ((GUI) event.getInventory().getHolder()).onInventoryClick(event);
        }
    }
}
