package bab.bbb.Commands;

import bab.bbb.Bbb;
import bab.bbb.utils.Home;
import bab.bbb.utils.Utils;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static bab.bbb.utils.Utils.combattag;

@RequiredArgsConstructor
public class HomeCommand implements CommandExecutor {
    private final Bbb plugin = Bbb.getInstance();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (sender instanceof Player player) {
            List<Home> homes = Utils.getHomes().getOrDefault(player.getUniqueId(), null);
            if (homes == null) {
                Utils.errormsg(player, "You have no home to teleport to");
                return true;
            }
            if (combattag.contains(player.getName())) {
                Utils.errormsg(player, "You can't teleport whilst being combat tagged");
                return true;
            }

            String homestr = "home";

            if (args.length > 0)
                homestr = args[0];

            for (Home home : homes) {
                if (home.getName().equalsIgnoreCase(homestr)) {
                    Utils.infomsg(player, "&7Teleporting to home &e" + home.getName() + "&7...");

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            player.getWorld().strikeLightningEffect(player.getLocation());
                            player.playSound(player.getEyeLocation(), Sound.ENTITY_LIGHTNING_BOLT_IMPACT, 1.f, 1.f);

                            for (Player players : Bukkit.getOnlinePlayers())
                                players.hidePlayer(plugin, player);

                            player.teleport(home.getLocation());
                            for (Player players : Bukkit.getOnlinePlayers())
                                players.showPlayer(plugin, player);
                            player.getWorld().strikeLightningEffect(player.getLocation());
                            player.playSound(player.getEyeLocation(), Sound.ENTITY_LIGHTNING_BOLT_IMPACT, 1.f, 1.f);
                        }
                    }.runTaskLater(Bbb.getInstance(), 100);
                    return true;
                }
            }

            Utils.errormsg(player, "Couldn't find the home &e" + args[0]);
        }
        return true;
    }
}