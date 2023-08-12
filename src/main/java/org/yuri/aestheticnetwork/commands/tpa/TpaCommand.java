package org.yuri.aestheticnetwork.commands.tpa;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.yuri.aestheticnetwork.utils.Type;
import org.yuri.aestheticnetwork.utils.Utils;

import static org.yuri.aestheticnetwork.utils.RequestManager.*;
import static org.yuri.aestheticnetwork.utils.Utils.translate;

@SuppressWarnings("deprecation")
public class TpaCommand implements CommandExecutor {
    @Override
    public boolean onCommand(final @NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player user))
            return true;

        if (args.length < 1) {
            user.sendMessage(translate("&7You must specify who you want to teleport to."));
            return true;
        }

        Player recipient = Bukkit.getPlayer(args[0]);

        if (recipient == null) {
            user.sendMessage(translate("&7You can't send teleport requests to offline people!"));
            return true;
        }

        if (recipient.getName().equalsIgnoreCase(sender.getName())) {
            user.sendMessage(translate("&7You can't teleport to yourself!"));
            return true;
        }

        TpaRequest tpr = getTPArequest(recipient);

        if (tpr != null && tpr.getSender().equals(sender))
        {
            user.sendMessage(translate("&7You already have an ongoing request to this player."));
            return true;
        }

        if (Utils.manager1().get(
                "r." + recipient.getUniqueId() + ".t") != null) {
            user.sendMessage(translate("&7You can't request this player since they locked their tpa requests!"));
            return true;
        }

        tpa.remove(tpr);
        addTPArequest(user, recipient, Type.TPA);
        return true;
    }
}