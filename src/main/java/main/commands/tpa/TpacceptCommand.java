package main.commands.tpa;

import main.utils.Messages.Languages;
import main.utils.RequestManager;
import main.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TpacceptCommand implements CommandExecutor {
    public TpacceptCommand() {
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player user)) return true;

        String msg = Languages.EXCEPTION_NO_ACTIVE_TPAREQ;
        TpaRequest request;

        if (args.length == 0) {
            request = RequestManager.getTPArequest(user.getName());
        } else {
            String n = args[0];
            Player p = Bukkit.getPlayer(n);
            if (p == null) {
                user.sendMessage(msg);
                return true;
            }
            else
                n = p.getName();
            request = RequestManager.getTPArequest(user.getName(), n);
            msg = Languages.EXCEPTION_NO_ACTIVE_TPAREQ1 +
                    Utils.translateA("#fc282f") + args[0] + ".";
        }

        if (request == null) {
            user.sendMessage(msg);
            return true;
        }

        RequestManager.tpaccept(request, user);
        return true;
    }
}