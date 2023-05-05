package bab.bbb.Commands;

import bab.bbb.utils.Home;
import bab.bbb.utils.HomeIO;
import bab.bbb.utils.Methods;
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
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (args.length < 1) {
                Methods.errormsg(player, "invalid home name");
                return true;
            }
            Home home = new Home(args[0], player.getUniqueId(), player.getLocation());
            List<Home> homes = HomeIO.getHomes().getOrDefault(player.getUniqueId(), null);;
            if (homes == null)
            homes = new ArrayList<>();
            if (homes.stream().anyMatch(h -> h.getName().equals(args[0]))) {
                Methods.errormsg(player, "the home already exists");
                return true;
            }
            if (homes.size() >= 5 && !player.isOp()) {
                Methods.errormsg(player, "you have reached the home limit");
                return true;
            }
            File playerFolder = new File(HomeIO.getHomesFolder(), player.getUniqueId().toString());
            if (!playerFolder.exists()) playerFolder.mkdir();
            HomeIO.save(playerFolder, home.getName() + ".map", home);
            player.sendMessage(Methods.infostring("successfully setted home &e" + args[0] + " &7to your position"));
        }
        return true;
    }
}