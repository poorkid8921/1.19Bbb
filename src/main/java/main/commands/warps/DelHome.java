package main.commands.warps;

import main.utils.instances.CustomPlayerDataHolder;
import main.utils.instances.HomeHolder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import static main.utils.Initializer.MAIN_COLOR;
import static main.utils.Initializer.playerData;
import static main.utils.Utils.getHome;

public class DelHome implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("§7You must specify the home you want to delete!");
            return true;
        }
        CustomPlayerDataHolder D0 = playerData.get(sender.getName());
        HomeHolder home = getHome(args[0], D0.getHomes());
        if (home == null) {
            sender.sendMessage("§7You have no home called " + MAIN_COLOR + args[0] + ".");
            return true;
        }

        int i = -1;
        for (HomeHolder k : D0.getHomes()) {
            if (k == home)
                D0.getHomes()[i++] = null;
        }
        sender.sendMessage("§7You have successfully delete " + MAIN_COLOR + args[0] + ".");
        return true;
    }
}
