package common.commands.tpa;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import static org.yuri.eco.utils.Utils.*;

public class TpdenyCommand implements CommandExecutor {
    public boolean onCommand(final @NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player))
            return true;

        TpaRequest request = getRequest(player);
        if (request == null) {
            player.sendMessage(translateo("&7You got no active teleport request."));
            return true;
        }

        Player recipient = Bukkit.getPlayer(request.getSender().getUniqueId());
        if (recipient != null) {
            recipient.sendMessage(translate("&c" + player.getDisplayName() + " &7denied your teleportation request"));
            removeRequest(recipient);
            player.sendMessage(translate("&7You have successfully deny &c" + recipient.getDisplayName() + "&7's &7request."));
        }
        removeRequest(player);

        return true;
    }
}