package org.yuri.aestheticnetwork.commands.tpa;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.yuri.aestheticnetwork.utils.Type;
import org.yuri.aestheticnetwork.utils.Utils;

import static org.yuri.aestheticnetwork.utils.Initializer.teams;
import static org.yuri.aestheticnetwork.utils.RequestManager.*;
import static org.yuri.aestheticnetwork.utils.Utils.translateo;

public class TpaCommand implements CommandExecutor {
    @Override
    public boolean onCommand(final @NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player user)) return true;

        if (args.length < 1) {
            user.sendMessage(translateo("&7You must specify who you want to teleport to"));
            return true;
        }

        Player recipient = Bukkit.getPlayer(args[0]);

        if (recipient == null) {
            user.sendMessage(translateo("&7You can't send teleport requests to offline people"));
            return true;
        }

        if (recipient.getName().equalsIgnoreCase(sender.getName())) {
            user.sendMessage(translateo("&7You can't teleport to yourself"));
            return true;
        }

        TpaRequest tpr = getTPArequest(recipient.getName());

        if (tpr != null && tpr.getSender().equals(sender)) {
            user.sendMessage(translateo("&7You already have an ongoing request to this player"));
            return true;
        }

        if (Utils.manager1().get("r." + recipient.getName() + ".t") != null) {
            user.sendMessage(translateo("&7You can't request this player since they've locked their tp requests"));
            return true;
        }

        if (teams.containsKey(recipient.getName())) {
            user.sendMessage(translateo("&7You can't request this player since they're in a duel"));
            return true;
        }

        tpa.remove(tpr);
        addTPArequest(user, recipient, Type.TPA);
        return true;
    }
}