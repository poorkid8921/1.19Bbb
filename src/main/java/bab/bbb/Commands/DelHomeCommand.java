package bab.bbb.Commands;

import bab.bbb.utils.Home;
import bab.bbb.utils.Utils;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@RequiredArgsConstructor
public class DelHomeCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (sender instanceof Player player) {
            List<Home> homes = Utils.getHomes().getOrDefault(player.getUniqueId(), null);
            if (homes == null) {
                Utils.errormsg(player, "You have no home to delete");
                return true;
            }
            if (args.length < 1) {
                Utils.errormsg(player, "Invalid arguments");
                return true;
            }
            Home home = homes.stream().filter(h -> h.getName().equals(args[0])).findFirst().orElse(null);
            if (home == null) {
                Utils.errormsg(player, "The home specified is invalid");
                return true;
            }
            if (Utils.deleteHome(home))
                Utils.infomsg(player, "You have successfully deleted home &e" + home.getName());
            else
                Utils.errormsg(player, "Home deletion has failed");
        }
        return true;
    }
}