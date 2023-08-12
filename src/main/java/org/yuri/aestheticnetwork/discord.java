package org.yuri.aestheticnetwork;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.awt.Color;
import static org.yuri.aestheticnetwork.AestheticNetwork.rgbGradient;

public class discord implements CommandExecutor {
    AestheticNetwork plugin = AestheticNetwork.getInstance();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (sender instanceof Player player) {
            String dsclink = plugin.config.getString("placeholders.discordlink");
            String formatted = plugin.config.getString("discordmessage");

            assert formatted != null;
            player.sendMessage(rgbGradient(formatted, new Color(229, 45, 39), new Color(179, 18, 23)) + ChatColor.translateAlternateColorCodes('&', " &f" + dsclink));
            return true;
        }

        return false;
    }
}