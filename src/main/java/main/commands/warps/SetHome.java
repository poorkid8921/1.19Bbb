package main.commands.warps;

import main.utils.instances.CustomPlayerDataHolder;
import main.utils.instances.HomeHolder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Collections;

import static main.utils.Initializer.MAIN_COLOR;
import static main.utils.Initializer.playerData;
import static main.utils.Utils.getHome;

public class SetHome implements CommandExecutor, TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("§7You must specify a name for your home!");
            return true;
        }
        CustomPlayerDataHolder D0 = playerData.get(sender.getName());
        int maxHomes = (playerData.get(sender.getName()).getRank() > 0 ? 5 : 3);
        HomeHolder home = getHome(args[0], D0.getHomes());
        if (home != null) {
            sender.sendMessage("§7You already have a home called " + MAIN_COLOR + args[0] + "!");
            return true;
        } else if ((D0.getHomes().length + 1) > maxHomes) {
            sender.sendMessage("§7You can't have more than " + MAIN_COLOR + maxHomes + " §7homes.");
            return true;
        }

        int i = -1;
        boolean result = false;
        HomeHolder[] array = D0.getHomes();
        for (HomeHolder k : array) {
            if (k == null) {
                D0.getHomes()[i++] = new HomeHolder(args[0], ((Player) sender).getLocation());
                result = true;
            }
        }
        if (!result) {
            int newLength = array.length + 1;
            System.arraycopy(array, 0, new HomeHolder[newLength], 0, array.length);
            D0.getHomes()[array.length] = new HomeHolder(args[0], ((Player) sender).getLocation());
        }
        sender.sendMessage("§7You have successfully set the home " + MAIN_COLOR + args[0] + " §7at your current location.");
        return true;
    }

    @Override
    public java.util.List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return Collections.emptyList();
    }
}
