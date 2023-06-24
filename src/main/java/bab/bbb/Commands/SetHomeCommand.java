package bab.bbb.Commands;

import bab.bbb.utils.Home;
import bab.bbb.utils.Utils;
import lombok.AllArgsConstructor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static bab.bbb.utils.Utils.translate;

@AllArgsConstructor
public class SetHomeCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (sender instanceof Player player) {
            String homestr;

            if (args.length > 0)
                homestr = args[0];
            else
                homestr = "home";

            Home home = new Home(homestr, player.getUniqueId(), player.getLocation());
            List<Home> homes = Utils.getHomes().getOrDefault(player.getUniqueId(), null);
            if (homes == null)
                homes = new ArrayList<>();
            String finalHomestr = homestr;
            if (homes.stream().anyMatch(h -> h.getName().equals(finalHomestr))) {
                Utils.errormsgs(player, 19, home.getName());
                return true;
            }
            if (homes.size() >= 5 && !player.isOp()) {
                Utils.errormsgs(player, 20, home.getName());
                return true;
            }
            File playerFolder = new File(Utils.getHomesFolder(), player.getUniqueId().toString());
            if (!playerFolder.exists())
                playerFolder.mkdir();
            Utils.save(playerFolder, home.getName() + ".map", home);
            player.sendMessage(translate(player, "#bc5ae8Successfully set home #d6a7eb" + homestr));
        }
        return true;
    }
}