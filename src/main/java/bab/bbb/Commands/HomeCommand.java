package bab.bbb.Commands;

import bab.bbb.Bbb;
import bab.bbb.Events.misc.MiscEvents;
import bab.bbb.utils.Home;
import bab.bbb.utils.HomeIO;
import bab.bbb.utils.Methods;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class HomeCommand implements TabExecutor {
    private final Bbb plugin = Bbb.getInstance();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {
            List<Home> homes = HomeIO.getHomes().getOrDefault(player.getUniqueId(), null);
            if (homes == null) {
                Methods.errormsg(player, "you have no home to teleport to");
                return true;
            }
            if (args.length < 1) {
                Methods.errormsg(player, "invalid arguments");
                return true;
            }
            if (MiscEvents.antilog.contains(player.getName())) {
                Methods.errormsg(player, "can't teleport whilst being combat tagged");
                return true;
            }

            for (Home home : homes) {
                if (home.getName().equalsIgnoreCase(args[0])) {
                    player.sendMessage(Methods.infostring("teleporting to home &e" + home.getName() + "&7..."));

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            player.getWorld().strikeLightning(player.getLocation());
                            if (player.getVehicle() != null) {
                                player.getVehicle().teleport(home.getLocation());
                                player.getVehicle().eject();
                            }

                            for (Player players : Bukkit.getOnlinePlayers())
                                players.hidePlayer(plugin, player);

                            player.teleport(home.getLocation());
                            for (Player players : Bukkit.getOnlinePlayers())
                                players.showPlayer(plugin, player);
                            player.getWorld().strikeLightning(player.getLocation());
                        }
                    }.runTaskLater(Bbb.getInstance(), 100);
                    return true;
                } else
                    Methods.errormsg(player, "couldn't find the home &e" + args[0]);
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, Command cmd, @NotNull String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("home")) {
            Player player = (Player) sender;
            List<Home> homes = HomeIO.getHomes().getOrDefault(player.getUniqueId(), null);
            if (homes == null) return Collections.emptyList();
            if (args.length < 1)
                return homes.stream().map(Home::getName).sorted(String::compareToIgnoreCase).collect(Collectors.toList());
            else
                return homes.stream().map(Home::getName).filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase())).sorted(String::compareToIgnoreCase).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }
}