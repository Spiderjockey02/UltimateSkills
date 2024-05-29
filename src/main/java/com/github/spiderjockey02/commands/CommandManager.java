package com.github.spiderjockey02.commands;
import com.github.spiderjockey02.UltimateSkills;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import java.util.List;
import java.util.ArrayList;

public class CommandManager implements CommandExecutor, TabCompleter {
    private final UltimateSkills plugin;
    public final List<UltimateCommand> commands = new ArrayList<>();

    public CommandManager(UltimateSkills plugin) {
        this.plugin = plugin;
        UltimateSkills.getInstance().getCommand("skills").setExecutor(this);
        UltimateSkills.getInstance().getCommand("skills").setTabCompleter(this);
        registerCommands();
    }

    public void registerCommands() {
        registerCommand(new SkillCommand());
        registerCommand(new HelpCommand());
        registerCommand(new TopCommand());
    }

    public void registerCommand(UltimateCommand command) {
        commands.add(command);
    }

    public void unregisterCommand(Command command) {
        commands.remove(command);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        for (UltimateCommand command : commands) {
            // We don't want to execute other commands or ones that are disabled
            if (!command.aliases.isEmpty() && !(command.aliases.contains(args[0]) && command.enabled)) {
                continue;
            }

            if (command.onlyForPlayers && !(sender instanceof Player)) {
                // Must be a player
                sender.sendMessage("Only players can use this command.");
                return false;
            }

            if (!((sender.hasPermission(command.permission) || command.permission
                    .equalsIgnoreCase("") || command.permission
                    .equalsIgnoreCase("ultimateskills.")) && command.enabled)) {
                // No permissions
                sender.sendMessage("No permission");
                return false;
            }

            command.execute(sender, args);
            return true;
        }

        // Unknown command message
        sender.sendMessage("Unknown command message");
        return false;
    }

    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command cmd, @NotNull String label, String[] args) {
        // Handle the tab completion if it's a sub-command.
        if (args.length == 1) {
            List<String> result = new ArrayList<>();
            for (UltimateCommand command : commands) {
                for (String alias : command.aliases) {
                    if (alias.toLowerCase().startsWith(args[0].toLowerCase()) && (
                            command.enabled && (commandSender.hasPermission(command.permission)
                                    || command.permission.equalsIgnoreCase("") || command.permission
                                    .equalsIgnoreCase("ultimateskills.")))) {
                        result.add(alias);
                    }
                }
            }
            return result;
        }

        // Let the sub-command handle the tab completion
        for (UltimateCommand command : commands) {
            if (command.aliases.contains(args[0]) && (command.enabled && (
                    commandSender.hasPermission(command.permission) || command.permission.equalsIgnoreCase("")
                            || command.permission.equalsIgnoreCase("ultimateskills.")))) {
                return command.onTabComplete(commandSender, cmd, label, args);
            }
        }
        return null;
    }
}
