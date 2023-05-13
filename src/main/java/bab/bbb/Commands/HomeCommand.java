package bab.bbb.Commands;

import bab.bbb.Bbb;
import bab.bbb.utils.Home;
import bab.bbb.utils.Utils;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static bab.bbb.utils.Utils.combattag;

@RequiredArgsConstructor
public class HomeCommand implements TabExecutor {
    private final Bbb plugin = Bbb.getInstance();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (sender instanceof Player player) {
            List<Home> homes = Utils.getHomes().getOrDefault(player.getUniqueId(), null);
            if (homes == null) {
                Utils.errormsgs(player, 16, "");
                return true;
            }

            if (combattag.contains(player.getName())) {
                Utils.errormsgs(player, 17, "");
                return true;
            }

            String homestr = "home";

            if (args.length > 0)
                homestr = args[0];

            try {
                for (Home home : homes) {
                    if (home.getName().equalsIgnoreCase(homestr)) {
                        Utils.infomsg(player, "&7Teleporting to home &e" + home.getName() + "&7...");

                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                player.getWorld().strikeLightningEffect(player.getLocation());
                                player.playSound(player.getEyeLocation(), Sound.ENTITY_LIGHTNING_BOLT_IMPACT, 1.f, 1.f);
                                player.teleport(home.getLocation());
                                player.getWorld().strikeLightningEffect(player.getLocation());
                                player.playSound(player.getEyeLocation(), Sound.ENTITY_LIGHTNING_BOLT_IMPACT, 1.f, 1.f);
                            }
                        }.runTaskLater(Bbb.getInstance(), 100);
                        return true;
                    }
                }
            } catch (RuntimeException e) {
                e.printStackTrace();
            }

            Utils.errormsgs(player, 18, args[0]);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, Command cmd, @NotNull String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("home")) {
            Player player = (Player) sender;
            List<Home> homes = Utils.getHomes().getOrDefault(player.getUniqueId(), null);
            if (homes == null) return Collections.emptyList();
            if (args.length < 1)
                return homes.stream().map(Home::getName).sorted(String::compareToIgnoreCase).collect(Collectors.toList());
            else
                return homes.stream().map(Home::getName).filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase())).sorted(String::compareToIgnoreCase).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }
}