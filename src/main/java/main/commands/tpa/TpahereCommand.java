package main.commands.tpa;

import main.Practice;
import main.utils.Instances.TpaRequest;
import main.utils.Languages;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static main.utils.RequestManager.addTPArequest;
import static main.utils.RequestManager.getTPArequest;

public class TpahereCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(Languages.WHO_TPA);
            return true;
        }

        Player recipient = Bukkit.getPlayer(args[0]);

        if (recipient == null) {
            sender.sendMessage(Languages.EXCEPTION_PLAYER_OFFLINETPA);
            return true;
        }

        String ren = recipient.getName();
        if (ren.equals(sender.getName())) {
            sender.sendMessage(Languages.EXCEPTION_PLAYER_TPSELF);
            return true;
        }

        TpaRequest tpr = getTPArequest(ren);

        if (tpr != null) {
            if (tpr.getSender().equals(sender)) {
                sender.sendMessage(Languages.GLOBAL_EXCEPTION_ALREADY_REQ);
                return true;
            }
        }

        if (Practice.config.get(
                "r." + ren + ".t") != null) {
            sender.sendMessage("§7You can't request this player since they've locked their tp requests.");
            return true;
        }

        addTPArequest((Player) sender, recipient, true);
        return true;
    }
}