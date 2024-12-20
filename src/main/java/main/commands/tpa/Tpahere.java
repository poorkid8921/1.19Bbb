package main.commands.tpa;

import main.utils.Instances.TpaRequest;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static main.utils.Initializer.playerData;
import static main.utils.Utils.*;

public class Tpahere implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("§7You must specify who you want to teleport to!");
            return true;
        }

        Player recipient = Bukkit.getPlayer(args[0]);
        if (recipient == null) {
            sender.sendMessage("§7You can't teleport to offline players.");
            return true;
        }

        String ren = recipient.getName();
        String sn = sender.getName();
        if (ren == sn) {
            sender.sendMessage("§7You can't teleport to yourself.");
            return true;
        }

        TpaRequest tpr = getRequest(ren);
        if (tpr != null && hasRequestedThePlayer(sn, ren)) {
            sender.sendMessage("§7You already have an ongoing request to this player.");
            return true;
        }

        if (playerData.get(ren).getTptoggle() == 1) {
            sender.sendMessage("§7You can't request this player since they've locked their tp requests.");
            return true;
        }

        addRequest((Player) sender, recipient, true);
        return true;
    }
}