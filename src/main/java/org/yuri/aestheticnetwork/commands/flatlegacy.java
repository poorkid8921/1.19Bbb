package org.yuri.aestheticnetwork.commands;

import io.papermc.lib.PaperLib;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.yuri.aestheticnetwork.AestheticNetwork;

import static org.yuri.aestheticnetwork.utils.Utils.translate;

public class flatlegacy implements CommandExecutor {
    AestheticNetwork plugin = AestheticNetwork.getInstance();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("has.staff")) {
            sender.sendMessage(translate("&7This feature is disabled to the public."));
            return true;
        }

        if (sender instanceof Player pp)
            PaperLib.teleportAsync(pp, new Location(plugin.lflat.getWorld(),
                    plugin.lflat.getX(),
                    plugin.lflat.getY(),
                    plugin.lflat.getZ(),
                    pp.getLocation().getYaw(),
                    pp.getLocation().getPitch())).thenAccept(reason -> pp.setMetadata("1.19.2", new FixedMetadataValue(plugin, 0)));

        return true;
    }
}