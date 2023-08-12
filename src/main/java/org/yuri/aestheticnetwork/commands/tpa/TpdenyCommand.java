package org.yuri.aestheticnetwork.commands.tpa;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static org.yuri.aestheticnetwork.utils.RequestManager.*;
import static org.yuri.aestheticnetwork.utils.Utils.translate;

public class TpdenyCommand implements CommandExecutor {
    public boolean onCommand(final @NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player))
            return true;

        String lookup = null;
        if (args.length > 0)
        {
            Player trp = Bukkit.getPlayer(args[0]);

            if (trp == null) {
                player.sendMessage(translate("&c" + args[0] + " &7is offline."));
                return true;
            }

            lookup = trp.getName();
        }

        TpaRequest request = getTPArequest(player);
        if (request == null) {
            player.sendMessage(translate("&7You got no active teleport request."));
            return true;
        }

        Player recipient = Bukkit.getPlayer(request.getSender().getUniqueId());
        if (recipient != null) {
            recipient.sendMessage(translate("&c" + player.getDisplayName() + " &7denied your teleportation request"));
            removeTPArequest(recipient);
            player.sendMessage(translate("&7You have successfully deny &c" + recipient.getDisplayName() + "'s &7request."));
        }
        removeTPArequest(player);

        return true;
    }
}