package com.github.spiderjockey02.commands;

import com.github.spiderjockey02.enums.SkillType;
import com.github.spiderjockey02.gui.LevelGUI;
import com.github.spiderjockey02.gui.ProfileGUI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.Collections;
import java.util.List;

public class SkillCommand extends UltimateCommand {

    public SkillCommand() {
        super(Collections.singletonList("info"), "Get information on your skills", "ultimateskills.info", true, "/skills info");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player p = (Player) sender;
        if (args.length == 2) {
            if (SkillType.getSkills().contains(args[1])) {
                SkillType type = SkillType.valueOf(args[1]);
                p.openInventory(new LevelGUI(p.getUniqueId(), type).getInventory());
            }
        } else {
            p.openInventory(new ProfileGUI(p).getInventory());
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String label, String[] args) {
        return SkillType.getSkills();
    }
}
