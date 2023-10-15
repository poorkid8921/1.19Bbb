package main.commands.tpa;

import main.utils.Instances.TpaRequest;
import main.utils.Languages;
import main.utils.RequestManager;
import main.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static main.utils.Languages.MAIN_COLOR;
import static main.utils.RequestManager.tpa;

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
            } else
                n = p.getName();
            request = RequestManager.getTPArequest(user.getName(), n);
            msg = Languages.EXCEPTION_NO_ACTIVE_TPAREQ1 +
                    Utils.translateA("#fc282f") + args[0] + ".";
        }

        if (request == null) {
            user.sendMessage(msg);
            return true;
        }

        Player tempuser;
        Player temprecipient;

        if (request.isHere()) {
            tempuser = user;
            temprecipient = request.getSender();
            temprecipient.sendMessage("§7You have accepted " + MAIN_COLOR + tempuser.getDisplayName() + "§7's teleport request",
                    "§7Teleporting...");
            tempuser.sendMessage(MAIN_COLOR + temprecipient.getDisplayName() + " §7has accepted your teleport request");
        } else {
            tempuser = request.getSender();
            temprecipient = user;
            temprecipient.sendMessage("§7You have accepted " + MAIN_COLOR + temprecipient.getDisplayName() + "§7's teleport request",
                    "§7Teleporting...");
            tempuser.sendMessage(MAIN_COLOR + temprecipient.getDisplayName() + " §7has accepted your teleport request");
        }

        // DEBUG
        Bukkit.getLogger().warning("HERE: " + (request.isHere() ? "y" : "n") + " USER: " + tempuser.getName() + " | " +
                "RECIPIENT:" + temprecipient.getName());

        tempuser.teleportAsync(temprecipient.getLocation()).thenAccept(reason -> tpa.remove(request));
        return true;
    }
}