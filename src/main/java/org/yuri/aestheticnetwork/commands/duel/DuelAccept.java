package org.yuri.aestheticnetwork.commands.duel;

import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.jetbrains.annotations.NotNull;
import org.yuri.aestheticnetwork.AestheticNetwork;
import org.yuri.aestheticnetwork.utils.Languages;

import java.util.Map;

import static org.yuri.aestheticnetwork.utils.Initializer.lp;
import static org.yuri.aestheticnetwork.utils.Initializer.teams;
import static org.yuri.aestheticnetwork.utils.Utils.*;
import static org.yuri.aestheticnetwork.utils.duels.DuelManager.*;

public class DuelAccept implements CommandExecutor {
    public boolean onCommand(final @NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player user))
            return true;

        String msg = Languages.EXCEPTION_NO_DUEL_REQ;
        DuelRequest request;

        if (args.length == 0) {
            request = getDUELrequest(user.getName());
        } else {
            request = getDUELrequest(user.getName(),
                    args[0].toLowerCase());
            msg = Languages.EXCEPTION_NO_ACTIVE_DUELREQ + translateA("#fc282f") + args[0] + ".";
        }

        if (request == null) {
            user.sendMessage(msg);
            return true;
        }

        String targetUID = request.getSender().getName();
        Player recipient = Bukkit.getPlayer(targetUID);
        teams.putAll(Map.of(targetUID, 0,
                user.getName(), 1));

        int check = getAvailable(request.getType());
        if (check >= 6) {
            removeDUELrequest(request);
            user.sendMessage(Languages.EXCEPTION_NO_ARENAS_OPEN);
            return true;
        }

        startduel(user,
                recipient,
                request.getType(),
                1,
                request.getMaxrounds(),
                check + 1);
        User up = lp.getUserManager().getUser(recipient.getUniqueId());
        up.data().add(Node.builder("permission:tab.scoreboard.duels").build());
        lp.getUserManager().saveUser(up);

        User u = lp.getUserManager().getUser(user.getUniqueId());
        u.data().add(Node.builder("permission:tab.scoreboard.duels").build());
        lp.getUserManager().saveUser(u);
        return true;
    }
}