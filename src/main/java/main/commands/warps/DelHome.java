package main.commands.warps;

import main.utils.Instances.CustomPlayerDataHolder;
import main.utils.Instances.HomeHolder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import static main.utils.Initializer.MAIN_COLOR;
import static main.utils.Initializer.playerData;
import static main.utils.Utils.getHome;

public class DelHome implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        CustomPlayerDataHolder D0 = playerData.get(sender.getName());
        HomeHolder home = getHome(args[0], D0.getHomes());
        if (args.length == 0) {
            sender.sendMessage("§7You must specify the home you want to delete!");
            return true;
        } else if (home == null) {
            sender.sendMessage("§7You have no home called " + MAIN_COLOR + args[0] + ".");
            return true;
        }

        D0.getHomes().remove(home);
        sender.sendMessage("§7You have successfully delete " + MAIN_COLOR + args[0] + ".");
        return true;
    }
}
