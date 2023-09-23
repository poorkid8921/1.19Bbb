package common.commands.tpa;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.yuri.eco.utils.Initializer;
import org.yuri.eco.utils.Utils;

import static org.yuri.eco.utils.Utils.*;

public class TpahereCommand implements CommandExecutor {
    public TpahereCommand() {
    }

    @Override
    public boolean onCommand(final @NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player user)) return true;

        if (args.length < 1) {
            user.sendMessage(translateo("&7You must specify who you want to teleport to."));
            return true;
        }

        Player recipient = Bukkit.getPlayer(args[0]);

        if (recipient == null) {
            user.sendMessage(translateo("&7You can't send teleport requests to offline players."));
            return true;
        }

        String rn = recipient.getName();
        if (rn.equalsIgnoreCase(sender.getName())) {
            user.sendMessage(translateo("&7You can't teleport to yourself."));
            return true;
        }

        TpaRequest tpr = getRequest(rn);

        if (tpr != null && tpr.getSender().equals(sender)) {
            user.sendMessage(translateo("&7You already have an ongoing request to this player."));
            return true;
        }

        if (Utils.manager().get("r." + rn + ".t") != null) {
            user.sendMessage(translateo("&7You can't request this player since they've locked their tp requests."));
            return true;
        }

        Initializer.requests.remove(tpr);
        Utils.addRequest(user, recipient, Type.TPAHERE, true);
        return true;
    }
}