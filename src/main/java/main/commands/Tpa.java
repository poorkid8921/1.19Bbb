package main.commands;

import main.utils.Initializer;
import main.utils.Instances.TpaRequest;
import main.utils.Initializer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static main.utils.RequestManager.addTPArequest;
import static main.utils.RequestManager.getTPArequest;

public class Tpa implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(Initializer.WHO_TPA);
            return true;
        }

        Player recipient = Bukkit.getPlayer(args[0]);

        if (recipient == null) {
            sender.sendMessage("§7You can't send teleport requests to offline players.");
            return true;
        }

        String ren = recipient.getName();
        String sn = sender.getName();
        if (ren.equals(sn)) {
            sender.sendMessage("§7You can't teleport to yourself.");
            return true;
        }

        TpaRequest tpr = getTPArequest(ren);

        if (tpr != null) {
            if (tpr.getSenderF().equals(sn)) {
                sender.sendMessage(Initializer.GLOBAL_EXCEPTION_ALREADY_REQ);
                return true;
            }
        }

        if (!Initializer.tpa.contains(ren)) {
            sender.sendMessage("§7You can't request this player since they've locked their tp requests.");
            return true;
        }

        addTPArequest((Player) sender, recipient, false);
        return true;
    }
}