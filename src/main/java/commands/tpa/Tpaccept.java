package commands.tpa;

import main.utils.Initializer;
import main.utils.Languages;
import main.utils.TpaRequest;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static main.utils.Initializer.bukkitTasks;
import static main.utils.Initializer.requests;
import static main.utils.Languages.MAIN_COLOR;
import static main.utils.Utils.getRequest;
import static main.utils.Utils.translate;

@SuppressWarnings("deprecation")
public class Tpaccept implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player user)) return true;

        String msg = Languages.EXCEPTION_NO_ACTIVE_TPAREQ;
        TpaRequest request;
        String n = "";
        String un = user.getName();

        if (args.length == 0) {
            request = getRequest(un);
        } else {
            n = args[0];
            Player p = Bukkit.getPlayer(n);
            if (p == null) {
                user.sendMessage(msg);
                return true;
            } else
                n = p.getName();
            request = getRequest(un, n);
            msg = Languages.EXCEPTION_NO_ACTIVE_TPAREQ1 +
                    MAIN_COLOR + args[0] + ".";
        }

        if (request == null) {
            user.sendMessage(msg);
            return true;
        } else if (un.equals(n)) {
            user.sendMessage(Languages.EXCEPTION_PLAYER_TPSELF);
            return true;
        }

        Player tempuser;
        Player temprecipient;

        if (!request.isHere()) {
            tempuser = request.getSender();
            temprecipient = user;
            temprecipient.sendMessage("§7You have accepted " + MAIN_COLOR + translate(tempuser.getDisplayName()) + "§7's teleport request",
                    "§7Teleporting...");
            if (request.getTpaAll())
                tempuser.sendMessage(MAIN_COLOR + translate(temprecipient.getDisplayName()) + " §7has accepted your teleport request");
        } else {
            tempuser = user;
            temprecipient = request.getSender();
            temprecipient.sendMessage("§7You have accepted " + MAIN_COLOR + translate(tempuser.getDisplayName()) + "§7's teleport request",
                    "§7Teleporting...");
            if (request.getTpaAll())
                tempuser.sendMessage(MAIN_COLOR + translate(temprecipient.getDisplayName()) + " §7has accepted your teleport request");
        }

        Bukkit.getScheduler().cancelTask(bukkitTasks.get(request.getSenderF()));
        tempuser.teleportAsync(temprecipient.getLocation()).thenAccept(reason -> Initializer.requests.remove(request));
        return true;
    }
}