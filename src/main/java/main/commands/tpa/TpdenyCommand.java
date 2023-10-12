package main.commands.tpa;

import main.utils.Instances.TpaRequest;
import main.utils.Languages;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static main.utils.RequestManager.getTPArequest;
import static main.utils.RequestManager.tpa;
import static main.utils.Utils.translate;

@SuppressWarnings("deprecation")
public class TpdenyCommand implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player))
            return true;

        TpaRequest request = getTPArequest(player.getName());
        if (request == null) {
            player.sendMessage(Languages.EXCEPTION_NO_ACTIVE_TPAREQ);
            return true;
        }

        Player recipient = Bukkit.getPlayer(request.getSender().getUniqueId());
        if (recipient != null) {
            recipient.sendMessage(translate("#fc282f" + player.getDisplayName() + " &7denied your teleportation request."));
            player.sendMessage(translate("&7You have successfully deny #fc282f" + recipient.getDisplayName() + "&7's &7request."));
        }
        tpa.remove(request);
        return true;
    }
}