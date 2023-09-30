package commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import main.utils.Initializer;
import main.utils.Utils;

import static main.utils.Utils.getRequest;
import static main.utils.Utils.translateo;

public class Tpahere implements CommandExecutor {
    public Tpahere() {
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
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
        Utils.addRequest(user, recipient, true, true);
        return true;
    }
}