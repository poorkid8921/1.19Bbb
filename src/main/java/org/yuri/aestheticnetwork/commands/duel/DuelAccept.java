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

import java.util.Map;
import java.util.UUID;

import static org.yuri.aestheticnetwork.utils.Initializer.api;
import static org.yuri.aestheticnetwork.utils.Initializer.teams;
import static org.yuri.aestheticnetwork.utils.Utils.translate;
import static org.yuri.aestheticnetwork.utils.duels.DuelManager.*;

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

        UUID targetUID = request.getSender().getUniqueId();
        Player recipient = Bukkit.getPlayer(targetUID);
        teams.putAll(Map.of(targetUID, 0,
                user.getUniqueId(), 0));

        int check = getAvailable(request.getType());
        if (check >= 6) {
            removeDUELrequest(request);
            user.sendMessage(translate("&7There are no open arenas yet."));
            return true;
        }

        if (request.IsLegacy()) {
            user.setMetadata("1.19.2",
                    new FixedMetadataValue(AestheticNetwork.getInstance(),
                            0));
            recipient.setMetadata("1.19.2",
                    new FixedMetadataValue(AestheticNetwork.getInstance(),
                            0));
        }

        startduel(user,
                recipient,
                request.getType(),
                1,
                request.getMaxrounds(),
                check + 1);
        User up = api.getUserManager().getUser(recipient.getUniqueId());
        up.data().add(Node.builder("permission:tab.scoreboard.duels").build());
        api.getUserManager().saveUser(up);

        User u = api.getUserManager().getUser(user.getUniqueId());
        u.data().add(Node.builder("permission:tab.scoreboard.duels").build());
        api.getUserManager().saveUser(u);
        return true;
    }
}