package main.commands.tpa;

import main.Practice;
import main.utils.Instances.TpaRequest;
import main.utils.Languages;
import main.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static main.utils.RequestManager.*;

public class TpaCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player user)) return true;

        if (args.length < 1) {
            user.sendMessage(Languages.WHO_TPA);
            return true;
        }

        Player recipient = Bukkit.getPlayer(args[0]);

        if (recipient == null) {
            user.sendMessage(Utils.translateo("&7You can't send teleport requests to offline players."));
            return true;
        }

        if (recipient.getName().equalsIgnoreCase(user.getName())) {
            user.sendMessage(Utils.translateo("&7You can't teleport to yourself."));
            return true;
        }

        TpaRequest tpr = getTPArequest(recipient.getName());

        if (tpr != null) {
            if (tpr.getSender().equals(user)) {
                user.sendMessage(Utils.translateo("&7You already have an ongoing request to this player."));
                return true;
            } else if (tpr.getReceiver().equals(user)) {
                tpaccept(tpr, user);
                return true;
            }
        }

        if (Practice.cc1.get("r." + recipient.getName() + ".t") != null) {
            user.sendMessage(Utils.translateo("&7You can't request this player since they've locked their tp requests."));
            return true;
        }

        addTPArequest(user, recipient, false);
        return true;
    }
}