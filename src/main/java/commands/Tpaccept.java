package commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import main.utils.Utils;

import static main.utils.Utils.*;

@SuppressWarnings("deprecation")
public class Tpaccept implements CommandExecutor {
    public Tpaccept() {
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player user)) return true;

        String msg = translateo("&7You got no active teleport request.");
        TpaRequest request;

        if (args.length == 0) {
            request = getRequest(user.getName());
        } else {
            String n = args[0];
            Player p = Bukkit.getPlayer(n);
            if (p == null) {
                user.sendMessage(translate("&7Couldn't find anyone online named #fc282f" + args[0]) + ".");
                return true;
            } else
                n = p.getName();
            request = getRequest(user.getName(), n);
            msg = translate("&7You got no active teleport request from #fc282f" + n + ".");
        }

        if (request == null) {
            user.sendMessage(msg);
            return true;
        }

        Utils.tpaccept(request, user);
        return true;
    }
}