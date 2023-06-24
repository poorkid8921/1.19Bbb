package bab.bbb.tpa;

import bab.bbb.Bbb;
import bab.bbb.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static bab.bbb.utils.Utils.tpmsg;

public class TpdenyCommand implements CommandExecutor {
    public TpdenyCommand() {
    }

    public boolean onCommand(final @NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player))
            return true;

        Player player = ((Player) sender).getPlayer();
        if (player == null)
            return true;

        TpaRequest request = Utils.getRequest(player);
        if (request == null) {
            tpmsg(player, null, 15);
            return true;
        }

        String recipientstr = request.getSender().getName();
        Player recipient = Bukkit.getPlayer(recipientstr);
        Utils.tpmsg(player, recipientstr, 6);
        Utils.tpmsg(recipient, player.getName(), 5);
        Utils.removeRequest(player);
        Utils.removeRequest(recipient);

        return true;
    }
}