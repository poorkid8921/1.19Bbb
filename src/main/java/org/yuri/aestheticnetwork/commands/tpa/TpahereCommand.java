package org.yuri.aestheticnetwork.commands.tpa;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.yuri.aestheticnetwork.utils.Messages.Languages;
import org.yuri.aestheticnetwork.utils.Instances.Type;
import org.yuri.aestheticnetwork.utils.Utils;

import static org.yuri.aestheticnetwork.utils.RequestManager.*;
import static org.yuri.aestheticnetwork.utils.Utils.translateo;

public class TpahereCommand implements CommandExecutor {
    @Override
    public boolean onCommand(final @NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player user))
            return true;

        if (args.length < 1) {
            user.sendMessage(Languages.WHO_TPA);
            return true;
        }

        Player recipient = Bukkit.getPlayer(args[0]);

        if (recipient == null) {
            user.sendMessage(Languages.EXCEPTION_PLAYER_OFFLINETPA);
            return true;
        }

        if (recipient.getName().equalsIgnoreCase(user.getName())) {
            user.sendMessage(Languages.EXCEPTION_PLAYER_TPSELF);
            return true;
        }

        TpaRequest tpr = getTPArequest(recipient.getName());

        if (tpr != null && tpr.getSender().equals(user)) {
            user.sendMessage(Languages.GLOBAL_EXCEPTION_ALREADY_REQ);
            return true;
        }

        if (Utils.manager1().get(
                "r." + recipient.getName() + ".t") != null) {
            user.sendMessage(translateo("&7You can't request this player since they've locked their tp requests."));
            return true;
        }

        addTPArequest(user, recipient, Type.TPAHERE);
        return true;
    }
}