package bab.bbb.Commands;

import bab.bbb.utils.Home;
import bab.bbb.utils.Utils;
import lombok.AllArgsConstructor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class SetHomeCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {
            String homestr = "home";

            if (args.length > 0)
                homestr = args[0];

            Home home = new Home(homestr, player.getUniqueId(), player.getLocation());
            List<Home> homes = Utils.getHomes().getOrDefault(player.getUniqueId(), null);
            if (homes == null)
                homes = new ArrayList<>();
            String finalHomestr = homestr;
            if (homes.stream().anyMatch(h -> h.getName().equals(finalHomestr))) {
                Utils.errormsg(player, "the home already exists");
                return true;
            }
            if (homes.size() >= 5 && !player.isOp()) {
                Utils.errormsg(player, "you have reached the home limit");
                return true;
            }
            File playerFolder = new File(Utils.getHomesFolder(), player.getUniqueId().toString());
            if (!playerFolder.exists())
                playerFolder.mkdir();
            Utils.save(playerFolder, home.getName() + ".map", home);
            Utils.infomsg(player, "successfully setted home &e" + homestr + " &7to your position");
        }
        return true;
    }
}