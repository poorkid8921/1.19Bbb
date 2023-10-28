package commands.tpa;

import main.Economy;
import main.utils.Initializer;
import main.utils.TpaRequest;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static main.utils.Utils.addRequest;
import static main.utils.Utils.getRequest;

public class Tpa implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player user)) return true;

        if (args.length < 1) {
            user.sendMessage("§7You must specify who you want to teleport to.");
            return true;
        }

        Player recipient = Bukkit.getPlayer(args[0]);

        if (recipient == null) {
            user.sendMessage("§7You can't send teleport requests to offline players.");
            return true;
        }

        String ren = recipient.getName();
        if (ren.equalsIgnoreCase(sender.getName())) {
            user.sendMessage("§7You can't teleport to yourself.");
            return true;
        }

        TpaRequest tpr = getRequest(ren);

        if (tpr != null) {
            if (tpr.getSender().equals(user)) {
                user.sendMessage("§7You already have an ongoing request to this player.");
                return true;
            }
        }

        if (Economy.cc.get("r." + ren + ".t") != null) {
            user.sendMessage("§7You can't request this player since they've locked their tp requests.");
            return true;
        }

        addRequest(user, recipient, false, true);
        return true;
    }
}