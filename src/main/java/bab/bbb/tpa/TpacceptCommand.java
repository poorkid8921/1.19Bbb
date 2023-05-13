package bab.bbb.tpa;

import bab.bbb.Bbb;
import bab.bbb.utils.Type;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import static bab.bbb.utils.Utils.*;

@SuppressWarnings("deprecation")
public class TpacceptCommand implements CommandExecutor {
    private final Bbb plugin;

    public TpacceptCommand(Bbb plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(final @NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player user))
            return true;

        if (plugin.getRequest(user) == null) {
            tpmsg(user, null, 15);
            return true;
        }

        TpaRequest request = plugin.getRequest(user);
        String targetName = request.getSender().getName();

        Player recipient = Bukkit.getPlayer(targetName);

        if (recipient == null) {
            errormsgs(user, 2, targetName);
            return true;
        }

        if (combattag.contains(user.getName())) {
            tpmsg(user, recipient, 16);

            return true;
        }

        if (request.getType() == Type.TPA) {
            tpmsg(((Player) sender).getPlayer(), recipient, 10);
            tpmsg(recipient, null, 7);

            new BukkitRunnable() {
                @Override
                public void run() {
                    //for (Player players : Bukkit.getOnlinePlayers())
                    //    players.hidePlayer(plugin, recipient);

                    recipient.getWorld().strikeLightningEffect(recipient.getLocation());

                    recipient.teleport(user);
                    //for (Player players : Bukkit.getOnlinePlayers())
                    //    players.showPlayer(plugin, recipient);
                    recipient.getWorld().strikeLightningEffect(recipient.getLocation());
                    recipient.playSound(recipient.getEyeLocation(), Sound.ENTITY_LIGHTNING_BOLT_IMPACT, 1.f, 1.f);
                    user.playSound(user.getEyeLocation(), Sound.ENTITY_LIGHTNING_BOLT_IMPACT, 1.f, 1.f);
                }
            }.runTaskLater(plugin, 100);
        } else if (request.getType() == Type.TPAHERE) {
            tpmsg(((Player) sender).getPlayer(), recipient, 7);
            tpmsg(recipient, ((Player) sender).getPlayer(), 10);

            new BukkitRunnable() {
                @Override
                public void run() {
                    //for (Player players : Bukkit.getOnlinePlayers())
                    //    players.hidePlayer(plugin, user);

                    user.getWorld().strikeLightningEffect(user.getLocation());

                    user.teleport(recipient);
                    //for (Player players : Bukkit.getOnlinePlayers())
                    //    players.showPlayer(plugin, user);
                    user.getWorld().strikeLightningEffect(user.getLocation());
                    user.playSound(user.getEyeLocation(), Sound.ENTITY_LIGHTNING_BOLT_IMPACT, 1.f, 1.f);
                    recipient.playSound(recipient.getEyeLocation(), Sound.ENTITY_LIGHTNING_BOLT_IMPACT, 1.f, 1.f);
                }
            }.runTaskLater(plugin, 100);
        }

        plugin.removeRequest(user);
        return true;
    }
}