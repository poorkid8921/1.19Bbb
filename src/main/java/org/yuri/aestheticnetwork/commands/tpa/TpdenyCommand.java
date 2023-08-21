package org.yuri.aestheticnetwork.commands.tpa;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static org.yuri.aestheticnetwork.utils.RequestManager.getTPArequest;
import static org.yuri.aestheticnetwork.utils.RequestManager.removeTPArequest;
import static org.yuri.aestheticnetwork.utils.Utils.translate;
import static org.yuri.aestheticnetwork.utils.Utils.translateo;

public class TpdenyCommand implements CommandExecutor {
    public boolean onCommand(final @NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player))
            return true;

        TpaRequest request = getTPArequest(player);
        if (request == null) {
            player.sendMessage(translateo("&7You got no active teleport request."));
            return true;
        }

        Player recipient = Bukkit.getPlayer(request.getSender().getUniqueId());
        if (recipient != null) {
            recipient.sendMessage(translate("&c" + player.getDisplayName() + " &7denied your teleportation request"));
            removeTPArequest(getTPArequest(recipient));
            player.sendMessage(translate("&7You have successfully deny &c" + recipient.getDisplayName() + "'s &7request."));
        }
        removeTPArequest(request);

        return true;
    }
}