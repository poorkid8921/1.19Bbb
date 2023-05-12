package bab.bbb.tpa;

import bab.bbb.Bbb;
import bab.bbb.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TpdenyCommand implements CommandExecutor {
    private final Bbb plugin;

    public TpdenyCommand(Bbb plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(final CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player))
            return true;

        Player player = ((Player) sender).getPlayer();
        TpaRequest request = plugin.getRequest((Player) sender);
        if (request == null) {
            Utils.errormsg(player, "you don't have any active request");
            return true;
        }

        Player recipient = Bukkit.getPlayer(request.getSender().getName());

        Utils.tpmsg(player, recipient, 6);
        Utils.tpmsg(recipient, player, 5);
        plugin.removeRequest((Player) sender);

        return true;
    }
}