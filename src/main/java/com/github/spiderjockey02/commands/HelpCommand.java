package com.github.spiderjockey02.commands;

import com.github.spiderjockey02.utils.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import java.util.Collections;
import java.util.List;

public class HelpCommand extends UltimateCommand {
    public HelpCommand() {
        super(Collections.singletonList("help"), "Get list of available commands", "", true, "/skills help");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        sender.sendMessage(StringUtils.color("""
        &6&lSKILLS | HELP MENU
        &6/skills info &7- Displays your skill levels.
        &6/skills top [skill] &7- Displays the skill leaderboard.
        &6/skills help &7- Displays this message.
        """));
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String label, String[] args) {
        return List.of();
    }
}
