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
public class TpdenyCommand implements CommandExecutor {
    public boolean onCommand(final @NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) return true;

        TpaRequest request = getRequest(player.getName());
        if (request == null) {
            player.sendMessage(translateo("&7You got no active teleport request."));
            return true;
        }

        Player recipient = request.getSender();
        recipient.sendMessage(translate("#fc282f" + player.getDisplayName() + " &7denied your teleportation request."));
        player.sendMessage(translate("&7You have successfully deny #fc282f" + recipient.getDisplayName() + "&7's request."));
        Initializer.requests.remove(request);
        return true;
    }
}