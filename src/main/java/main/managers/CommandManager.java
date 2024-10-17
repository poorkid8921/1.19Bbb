package main.managers;

import main.Economy;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;

public class CommandManager {
    public static void registerCommand(String name, CommandExecutor command) {
        Economy.INSTANCE.getCommand(name).setExecutor(command);
    }

    public static void tabCompleter(String name, TabCompleter completer) {
        Economy.INSTANCE.getCommand(name).setTabCompleter(completer);
    }
}
