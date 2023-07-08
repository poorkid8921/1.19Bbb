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
            player.sendMessage(translate("&7You got no active teleport request."));
            return true;
        }

        Player recipient = Bukkit.getPlayer(request.getSender().getName());
        assert recipient != null;
        player.sendMessage(translate("&7You denied &c" + recipient.getDisplayName() + "'s &7request."));
        recipient.sendMessage(translate("&c" + player.getDisplayName() + " &7denied your teleport request"));
        removeRequest(player);
        removeRequest(recipient);

        return true;
    }
}