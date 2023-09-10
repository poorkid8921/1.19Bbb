package org.yuri.aestheticnetwork.commands.tpa;

import io.papermc.lib.PaperLib;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.yuri.aestheticnetwork.utils.Type;

import java.util.UUID;

import static org.yuri.aestheticnetwork.utils.RequestManager.*;
import static org.yuri.aestheticnetwork.utils.Utils.translate;
import static org.yuri.aestheticnetwork.utils.Utils.translateo;

public class TpacceptCommand implements CommandExecutor {
    public TpacceptCommand() {
    }

    public boolean onCommand(final @NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player user)) return true;

        String msg = translateo("&7You got no active teleport request.");
        TpaRequest request;

        if (args.length == 0) {
            request = getTPArequest(user.getName());
        } else {
            String n = args[0];
            Player p = Bukkit.getPlayer(n);
            if (p == null) {
                user.sendMessage(translate("&7Couldn't find anyone online named #fc282f" + args[0]) + ".");
                return true;
            }
            else
                n = p.getName();
            request = getTPArequest(user.getName(), n);
            msg = translate("&7You got no active teleport request from #fc282f" + n + ".");
        }

        if (request == null) {
            user.sendMessage(msg);
            return true;
        }

        tpaccept(request, user);
        return true;
    }
}