package commands.tpa;

import main.utils.Initializer;
import main.utils.TpaRequest;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static main.utils.Languages.MAIN_COLOR;
import static main.utils.Utils.getRequest;
import static main.utils.Utils.translate;

@SuppressWarnings("deprecation")
public class Tpaccept implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player user)) return true;

        String msg = "§7You got no active teleport request.";
        TpaRequest request;

        if (args.length == 0) {
            request = getRequest(user.getName());
        } else {
            String n = args[0];
            Player p = Bukkit.getPlayer(n);
            if (p == null) {
                user.sendMessage("§7Couldn't find anyone online named " + MAIN_COLOR + args[0] + ".");
                return true;
            } else
                n = p.getName();
            request = getRequest(user.getName(), n);
            msg = "§7You got no active teleport request from " + MAIN_COLOR + n + ".";
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
            tempuser.sendMessage("§7You have accepted " + MAIN_COLOR + translate(temprecipient.getDisplayName()) + "§7's teleport request",
                    "§7Teleporting...");
            if (request.getTpaAll())
                temprecipient.sendMessage(MAIN_COLOR + translate(tempuser.getDisplayName()) + " §7has accepted your teleport request");
        } else {
            tempuser = request.getSender();
            temprecipient = user;
            temprecipient.sendMessage("§7You have accepted " + MAIN_COLOR + translate(tempuser.getDisplayName()) + "§7's teleport request",
                    "§7Teleporting...");
            if (request.getTpaAll())
                tempuser.sendMessage(MAIN_COLOR + translate(temprecipient.getDisplayName()) + " §7has accepted your teleport request");
        }

        tempuser.teleportAsync(temprecipient.getLocation()).thenAccept(reason -> Initializer.requests.remove(request));

        return true;
    }
}