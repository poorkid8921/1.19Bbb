package org.yuri.aestheticnetwork.commands.duel;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.yuri.aestheticnetwork.events;
import org.yuri.aestheticnetwork.utils.Utils;

import java.util.UUID;

import static org.yuri.aestheticnetwork.utils.RequestManager.*;
import static org.yuri.aestheticnetwork.utils.Utils.*;

public class DuelAccept implements CommandExecutor {
    public boolean onCommand(final @NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player user))
            return true;

        /*if (!user.hasPermission("has.staff"))
        {
            user.sendMessage(translate("&7You might not use this feature right now."));
            return true;
        }*/

        DuelRequest request = getDUELrequest(user);

        if (request == null) {
            user.sendMessage(translate("&7You got no active duel request."));
            return true;
        }

        UUID targetName = request.getSender().getUniqueId();
        Player recipient = Bukkit.getPlayer(targetName);
        events.teams.put(targetName, 0);
        events.teams.put(user.getUniqueId(), 1);

        int check =  getAvailable(request.getType());
        if (check >= 6) {
            removeDUELrequest(user);
            user.sendMessage(translate("&7There are no open arenas yet."));
            return true;
        }

        Utils.startduel(user,
                recipient,
                request.getType(),
                1,
                request.getMaxrounds(),
                check + 1,
                true);
        return true;
    }
}