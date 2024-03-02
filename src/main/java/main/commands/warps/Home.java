package main.commands.warps;

import main.utils.Instances.CustomPlayerDataHolder;
import main.utils.Instances.HomeHolder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import static main.utils.Initializer.MAIN_COLOR;
import static main.utils.Initializer.playerData;
import static main.utils.Utils.getHome;

public class Home implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        CustomPlayerDataHolder D0 = playerData.get(sender.getName());
        boolean zeroL = args.length == 0;
        String homeStr = zeroL ? (D0.getHomes().size() == 1 ? D0.getHomes().get(0).getName() : "home") : args[0];
        HomeHolder home = getHome(homeStr, D0.getHomes());
        if (zeroL && home == null) {
            sender.sendMessage(homeStr == "home" ? "§7You didn't set any home." : "§7You must specify the home you want to teleport to!");
            return true;
        } else if (home == null) {
            sender.sendMessage("§7You have no home called " + MAIN_COLOR + home + ".");
            return true;
        }

        ((Player) sender).teleportAsync(home.getLocation(), PlayerTeleportEvent.TeleportCause.COMMAND);
        sender.sendMessage("§7You have successfully teleport to " + MAIN_COLOR + homeStr + "!");
        return true;
    }
}