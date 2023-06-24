package bab.bbb.Commands;

import bab.bbb.utils.Home;
import bab.bbb.utils.Utils;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static bab.bbb.utils.Utils.translate;

@RequiredArgsConstructor
public class DelHomeCommand implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (sender instanceof Player player) {
            List<Home> homes = Utils.getHomes().getOrDefault(player.getUniqueId(), null);
            if (homes == null) {
                Utils.errormsgs(player, 13, "");
                return true;
            }
            if (args.length < 1) {
                Utils.errormsgs(player, 1, "");
                return true;
            }
            Home home = homes.stream().filter(h -> h.getName().equals(args[0])).findFirst().orElse(null);
            if (home == null) {
                Utils.errormsgs(player, 18, args[0]);
                return true;
            }
            if (Utils.deleteHome(home))
                player.sendMessage(translate(player, "#bc5ae8Successfully deleted home #d6a7eb" + home.getName()));
            else
                Utils.errormsgs(player, 15, home.getName());
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, Command cmd, @NotNull String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("delhome")) {
            Player player = (Player) sender;
            List<Home> homes = Utils.getHomes().getOrDefault(player.getUniqueId(), null);
            if (homes == null)
                return Collections.emptyList();
            if (args.length < 1)
                return homes.stream().map(Home::getName).sorted(String::compareToIgnoreCase).collect(Collectors.toList());
            else
                return homes.stream().map(Home::getName).filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase())).sorted(String::compareToIgnoreCase).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }
}