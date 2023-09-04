package common.commands.tpa;

import io.papermc.lib.PaperLib;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.yuri.eco.utils.Initializer;

import java.util.UUID;

import static org.yuri.eco.utils.Utils.*;

@SuppressWarnings("deprecation")
public class TpacceptCommand implements CommandExecutor {
    public TpacceptCommand() {
    }

    public boolean onCommand(final @NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player user)) return true;

        String msg = translateo("&7You got no active teleport request");
        TpaRequest request;

        if (args.length == 0) {
            request = getRequest(user.getName());
        } else {
            request = getRequest(user.getName(), args[0].toLowerCase());
            msg = translate("&7You got no active teleport request from #fc282f" + args[0]);
        }

        if (request == null) {
            user.sendMessage(msg);
            return true;
        }

        UUID targetName = request.getSender().getUniqueId();
        Player recipient = Bukkit.getPlayer(targetName);
        Player tempuser;
        Player temprecipient;

        if (request.getType() == Type.TPA) {
            tempuser = recipient;
            temprecipient = user;

            temprecipient.sendMessage(translate("&7You have accepted #fc282f" + tempuser.getDisplayName() + "&7's teleport request"));
            temprecipient.sendMessage(translateo("&7Teleporting..."));
            if (request.getTpaAll())
                tempuser.sendMessage(translate("#fc282f" + temprecipient.getDisplayName() + " &7has accepted your teleport request"));
        } else {
            tempuser = user;
            temprecipient = recipient;
            tempuser.sendMessage(translate("&7You have accepted #fc282f" + temprecipient.getDisplayName() + "&7's teleport request"));
            tempuser.sendMessage(translateo("&7Teleporting..."));
            if (request.getTpaAll())
                temprecipient.sendMessage(translate("#fc282f" + tempuser.getDisplayName() + " &7has accepted your teleport request"));
        }

        PaperLib.teleportAsync(tempuser, temprecipient.getLocation()).thenAccept(reason -> Initializer.requests.remove(request));
        return true;
    }
}