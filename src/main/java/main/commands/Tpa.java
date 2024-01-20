package main.commands;

import main.utils.Constants;
import main.utils.Instances.TpaRequest;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static main.utils.RequestManager.addTPArequest;
import static main.utils.RequestManager.getTPArequest;
import static main.utils.Utils.tabCompleteFilter;

public class Tpa implements CommandExecutor, TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(Constants.WHO_TPA);
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
                sender.sendMessage(Constants.GLOBAL_EXCEPTION_ALREADY_REQ);
                return true;
            }
        }

        if (!Constants.tpa.contains(ren)) {
            sender.sendMessage("§7You can't request this player since they've locked their tp requests.");
            return true;
        }

        addTPArequest((Player) sender, recipient, false);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        return args.length < 1 ?
                tabCompleteFilter(Constants.tpa) :
                tabCompleteFilter(Constants.tpa, args[0].toLowerCase());
    }
}