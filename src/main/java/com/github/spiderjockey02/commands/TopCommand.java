package com.github.spiderjockey02.commands;

import com.github.spiderjockey02.enums.SkillType;
import com.github.spiderjockey02.gui.TopGUI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class TopCommand extends UltimateCommand {
    public TopCommand() {
        super(Collections.singletonList("top"), "Get users skills", "ultimateskills.top", true, "/skills top");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player p = (Player) sender;
        p.openInventory(new TopGUI(p, SkillType.MINING).getInventory());
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String label, String[] args) {
        return SkillType.getSkills();
    }
}
