package org.yuri.aestheticnetwork.commands.essentialsx;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.yuri.aestheticnetwork.utils.Utils;

import java.util.List;

import static org.yuri.aestheticnetwork.utils.Utils.translateo;
import static org.yuri.aestheticnetwork.utils.Utils.translate;

public class help implements CommandExecutor {
    List<String> help = List.of(
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
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        for (String a : help)
            commandSender.sendMessage(a);

        return true;
    }
}
