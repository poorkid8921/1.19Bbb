package bab.bbb.tpa;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import static bab.bbb.utils.Utils.*;

public class TpdenyCommand implements CommandExecutor {
    public boolean onCommand(final @NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player))
            return true;

        TpaRequest request = getRequest(player);
        if (request == null) {
            player.sendMessage(translate("[&dTPA&r] Couldn't find any ongoing teleport requests."));
            return true;
        }

        Player recipient = Bukkit.getPlayer(request.getSender().getName());
        assert recipient != null;
        player.sendMessage(translate("[&dTPA&r] You denied &d" + recipient.getDisplayName() + " &rteleport request."));
        recipient.sendMessage(translate("[&dTPA&r] &d" + player.getName() + " &rdenied your teleport request."));
        removeRequest(player);
        removeRequest(recipient);

        return true;
    }
}