package bab.bbb.Commands;

import bab.bbb.utils.Home;
import bab.bbb.utils.Methods;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class DelHomeCommand implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (sender instanceof Player player) {
            List<Home> homes = Methods.getHomes().getOrDefault(player.getUniqueId(), null);
            if (homes == null) {
                Methods.errormsg(player, "you have no home to delete");
                return true;
            }
            if (args.length < 1) {
                Methods.errormsg(player, "invalid arguments");
                return true;
            }
            Home home = homes.stream().filter(h -> h.getName().equals(args[0])).findFirst().orElse(null);
            if (home == null) {
                Methods.errormsg(player, "the home specified is invalid");
                return true;
            }
            if (Methods.deleteHome(home))
                Methods.infomsg(player, "you have successfully deleted home &e" + home.getName());
            else
                Methods.errormsg(player, "home deletion has failed");
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, Command cmd, @NotNull String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("delhome")) {
            Player player = (Player) sender;
            List<Home> homes = Methods.getHomes().getOrDefault(player.getUniqueId(), null);
            if (homes == null) return Collections.emptyList();
            if (args.length < 1)
                return homes.stream().map(Home::getName).sorted(String::compareToIgnoreCase).collect(Collectors.toList());
            else
                return homes.stream().map(Home::getName).filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase())).sorted(String::compareToIgnoreCase).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }
}