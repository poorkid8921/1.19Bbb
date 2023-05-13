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
    private final Bbb plugin;

    public TpdenyCommand(Bbb plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(final @NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player))
            return true;

        Player player = ((Player) sender).getPlayer();
        if (player == null)
            return true;

        TpaRequest request = plugin.getRequest(player);
        if (request == null) {
            tpmsg(player, null, 15);
            return true;
        }

        Player recipient = Bukkit.getPlayer(request.getSender().getName());
        Utils.tpmsg(player, recipient, 6);
        Utils.tpmsg(recipient, player, 5);
        plugin.removeRequest(player);

        return true;
    }
}