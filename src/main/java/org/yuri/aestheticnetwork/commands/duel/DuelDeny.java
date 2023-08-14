package org.yuri.aestheticnetwork.commands.duel;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static org.yuri.aestheticnetwork.utils.RequestManager.*;
import static org.yuri.aestheticnetwork.utils.Utils.translate;

public class DuelDeny implements CommandExecutor {
    public boolean onCommand(final @NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player))
            return true;

        DuelRequest request = getDUELrequest(player);
        if (request == null) {
            player.sendMessage(translate("&7You got no duel request."));
            return true;
        }

        Player recipient = Bukkit.getPlayer(request.getSender().getUniqueId());
        if (recipient != null) {
            recipient.sendMessage(translate("&c" + player.getDisplayName() + " &7denied your duel request"));
            duel.remove(getDUELrequest(recipient));
            player.sendMessage(translate("&7You have successfully deny &c" + recipient.getDisplayName() + "'s &7request."));
        }
        duel.remove(request);

        return true;
    }
}