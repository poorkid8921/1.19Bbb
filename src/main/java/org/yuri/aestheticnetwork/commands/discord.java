package org.yuri.aestheticnetwork.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.yuri.aestheticnetwork.AestheticNetwork;

import java.awt.*;

import static net.md_5.bungee.api.ChatColor.translateAlternateColorCodes;
import static org.yuri.aestheticnetwork.AestheticNetwork.rgbGradient;

public class discord implements CommandExecutor {
    AestheticNetwork plugin = AestheticNetwork.getInstance();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player pp) {
            String dsclink = plugin.getConfig().getString("placeholders.discordlink");
            String formatted = plugin.getConfig().getString("discordmessage");
            pp.sendMessage(rgbGradient(formatted, new Color(229, 45, 39), new Color(179, 18, 23)) + translateAlternateColorCodes('&', " &f" + dsclink));
            return true;
        }

        return false;
    }
}