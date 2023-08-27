package common.commands.tpa;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.yuri.eco.utils.Initializer;
import static org.yuri.eco.utils.Utils.*;

@SuppressWarnings("deprecation")
public class TpaCommand implements CommandExecutor {
    public TpaCommand() {
    }

    @Override
    public boolean onCommand(final @NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player user))
            return true;

        if (args.length < 1) {
            user.sendMessage(translateo("&7You must specify who you want to teleport to"));
            return true;
        }

        Player recipient = Bukkit.getPlayer(args[0]);

        if (recipient == null) {
            user.sendMessage(translateo("&7You can't send teleport requests to offline people"));
            return true;
        }

        if (recipient.getName().equalsIgnoreCase(sender.getName())) {
            user.sendMessage(translateo("&7You can't teleport to yourself"));
            return true;
        }

        TpaRequest tpr = getRequest(recipient);

        if (tpr != null && tpr.getSender().equals(sender))
        {
            user.sendMessage(translateo("&7You already have an ongoing request to this player"));
            return true;
        }

        if (manager().get(
                "r." + recipient.getUniqueId() + ".t") != null) {
            user.sendMessage(translateo("&7You can't request this player since they've locked their tp requests"));
            return true;
        }

        Initializer.requests.remove(tpr);
        addRequest(user, recipient, Type.TPA, true);
        return true;
    }
}