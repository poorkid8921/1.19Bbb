package main.commands;

import com.google.common.collect.ImmutableList;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

public class Help implements CommandExecutor, TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        int i = 1;
        if (args.length > 0) {
            try {
                i = Integer.parseInt(args[0]);
            } catch (NumberFormatException ignored) {
            }
        }
        switch (i) {
            case 2 -> sender.sendMessage(
                    ChatColor.YELLOW + "---- " +
                            ChatColor.GOLD + "Help | Page 2/3" +
                            ChatColor.YELLOW + " ----",

                    ChatColor.GOLD +
                            "/kit " +
                            ChatColor.YELLOW +
                            "- Create or use a kit.",

                    ChatColor.GOLD +
                            "/killeffect " +
                            ChatColor.YELLOW +
                            "- Choose a kill-effect.",

                    ChatColor.GOLD +
                            "/report " +
                            ChatColor.YELLOW +
                            "- Report a player who broke our rules.",

                    ChatColor.GOLD +
                            "/duel " +
                            ChatColor.YELLOW +
                            "- Open the duel selector, or duel a player in a gamemode you specify.");
            case 3 -> sender.sendMessage(
                    ChatColor.YELLOW + "---- " +
                            ChatColor.GOLD + "Help | Page 3/3" +
                            ChatColor.YELLOW + " ----",

                    ChatColor.GOLD +
                            "/ffa " +
                            ChatColor.YELLOW +
                            "- Teleport to the FFA arena.",

                    ChatColor.GOLD +
                            "/flat " +
                            ChatColor.YELLOW +
                            "- Teleport to the Flat arena.",

                    ChatColor.GOLD +
                            "/back " +
                            ChatColor.YELLOW +
                            "- Teleport back to the place where you died.",

                    ChatColor.GOLD +
                            "/discord " +
                            ChatColor.YELLOW +
                            "- Get to know our discord link.");
            default -> sender.sendMessage(
                    ChatColor.YELLOW + "---- " +
                            ChatColor.GOLD + "Help | Page 1/3" +
                            ChatColor.YELLOW + " ----",

                    ChatColor.GOLD +
                            "/msglock " +
                            ChatColor.YELLOW +
                            "- Toggle whether want to receive any messages from other players.",

                    ChatColor.GOLD +
                            "/tpatoggle " +
                            ChatColor.YELLOW +
                            "- Toggle whether you want to receive tp requests.",

                    ChatColor.GOLD +
                            "/rtp " +
                            ChatColor.YELLOW +
                            "- Random teleport around the world.",

                    ChatColor.GOLD +
                            "/kit " +
                            ChatColor.YELLOW +
                            "- Create or use a kit.");
        }

        return true;
    }

    @Override
    public java.util.List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return ImmutableList.of("1", "2", "3");
    }
}
