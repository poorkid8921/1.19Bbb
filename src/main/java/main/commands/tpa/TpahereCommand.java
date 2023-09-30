package main.commands.tpa;

import main.utils.Messages.Languages;
import main.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static main.utils.RequestManager.addTPArequest;
import static main.utils.RequestManager.getTPArequest;
import static main.utils.Utils.translateo;

public class TpahereCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
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

        if (Utils.manager().get(
                "r." + recipient.getName() + ".t") != null) {
            user.sendMessage(translateo("&7You can't request this player since they've locked their tp requests."));
            return true;
        }

        addTPArequest(user, recipient, true);
        return true;
    }
}