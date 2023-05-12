package bab.bbb.Commands;

import bab.bbb.Bbb;
import bab.bbb.Events.misc.MiscEvents;
import bab.bbb.utils.Home;
import bab.bbb.utils.Utils;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class HomeCommand implements TabExecutor {
    private final Bbb plugin = Bbb.getInstance();

    public ItemStack dupe(ItemStack todupe, int amount) {
        ItemStack duped = todupe.clone();
        duped.setAmount(amount);
        return duped;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {
            List<Home> homes = Utils.getHomes().getOrDefault(player.getUniqueId(), null);
            if (homes == null) {
                Utils.errormsg(player, "you have no home to teleport to");
                return true;
            }
            if (MiscEvents.combattag.contains(player.getName())) {
                Utils.errormsg(player, "you can't teleport whilst being combat tagged");
                return true;
            }

            String homestr;

            if (args.length >= 1)
                homestr = args[0];
            else
                homestr = "home";

            for (Home home : homes) {
                if (home.getName().equalsIgnoreCase(homestr)) {
                    Utils.infomsg(player, "&7teleporting to home &e" + home.getName() + "&7...");

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            player.getWorld().strikeLightningEffect(player.getLocation());
                            player.playSound(player.getEyeLocation(), Sound.ENTITY_LIGHTNING_BOLT_IMPACT, 1.f, 1.f);

                            if (player.getVehicle() != null) {
                                if (player.getVehicle() instanceof final AbstractHorse donkey) {
                                    if (((AbstractHorse) player.getVehicle()).getInventory().getViewers().size() > 0) {
                                        for (int i = 1; i <= 16; i++) {
                                            ItemStack item = donkey.getInventory().getItem(i);
                                            if (item == null)
                                                continue;
                                            donkey.getWorld().dropItem(donkey.getLocation(), dupe(Objects.requireNonNull(donkey.getInventory().getItem(i)), item.getAmount()));
                                        }
                                    }
                                }
                            }

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

            Utils.errormsg(player, "couldn't find the home &e" + args[0]);
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