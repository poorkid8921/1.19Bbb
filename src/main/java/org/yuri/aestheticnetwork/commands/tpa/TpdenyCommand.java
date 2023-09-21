package org.yuri.aestheticnetwork.commands.tpa;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.yuri.aestheticnetwork.utils.Messages.Languages;

import static org.yuri.aestheticnetwork.utils.RequestManager.getTPArequest;
import static org.yuri.aestheticnetwork.utils.RequestManager.removeTPArequest;
import static org.yuri.aestheticnetwork.utils.Utils.translate;

@SuppressWarnings("deprecation")
public class TpdenyCommand implements CommandExecutor {
    public boolean onCommand(final @NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player))
            return true;

        TpaRequest request = getTPArequest(player.getName());
        if (request == null) {
            player.sendMessage(Languages.EXCEPTION_NO_ACTIVE_TPAREQ);
            return true;
        }

        Player recipient = Bukkit.getPlayer(request.getSender().getUniqueId());
        if (recipient != null) {
            recipient.sendMessage(translate("#fc282f" + player.getDisplayName() + " &7denied your teleportation request."));
            removeTPArequest(getTPArequest(recipient.getName()));
            player.sendMessage(translate("&7You have successfully deny #fc282f" + recipient.getDisplayName() + "&7's &7request."));
        }
        removeTPArequest(request);

        return true;
    }
}