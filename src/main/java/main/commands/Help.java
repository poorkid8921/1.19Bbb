package main.commands;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class Help implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] strings) {
        sender.sendMessage(
                ChatColor.YELLOW + "---- " +
                        ChatColor.GOLD + "Help " +
                        ChatColor.YELLOW + "----",

                ChatColor.GOLD +
                        "/msglock " +
                        ChatColor.YELLOW +
                        "- Toggle whether want to receive any messages from other players.",

                ChatColor.GOLD +
                        "/tpatoggle " +
                        ChatColor.YELLOW +
                        "- Toggle whether you want to receive tp requests.");
        return true;
    }
}
