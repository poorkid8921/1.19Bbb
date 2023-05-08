package bab.bbb.tpa;

import bab.bbb.Bbb;
import bab.bbb.Events.misc.MiscEvents;
import bab.bbb.utils.Methods;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class TpacceptCommand implements CommandExecutor {
    private final Bbb plugin;

    public TpacceptCommand(Bbb plugin) {
        this.plugin = plugin;
    }

    public ItemStack dupe(ItemStack todupe, int amount) {
        ItemStack duped = todupe.clone();
        duped.setAmount(amount);
        return duped;
    }

    public void dupe(Player user) {
        if (user.getVehicle() != null) {
            if (user.getVehicle() instanceof final AbstractHorse donkey) {
                if (((AbstractHorse) user.getVehicle()).getInventory().getViewers().size() > 0) {
                    for (int i = 1; i <= 16; i++) {
                        ItemStack item = donkey.getInventory().getItem(i);
                        if (item == null)
                            continue;
                        donkey.getWorld().dropItem(donkey.getLocation(), dupe(Objects.requireNonNull(donkey.getInventory().getItem(i)), item.getAmount()));
                    }
                }
            }
        }
    }

    public boolean onCommand(final @NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {

        if (!(sender instanceof Player user))
            return true;

        if (plugin.getRequest(user) == null) {
            Methods.errormsg(user, "you don't have any active request");
            return true;
        }

        TpaRequest request = plugin.getRequest(user);
        String targetName = request.getSender().getName();

        Player recipient = Bukkit.getPlayer(targetName);

        if (recipient == null) {
            Methods.errormsg(user, "player &e" + request.getSender().getDisplayName() + " isn't online anymore");
            return true;
        }

        if (MiscEvents.antilog.contains(user.getName())) {
            Methods.errormsg(user, "can't teleport whilst being combat tagged");
            return true;
        }

        dupe(recipient);
        dupe(user);

        if (request.getType() == Type.TPA) {
            Methods.tpmsg(((Player) sender).getPlayer(), recipient, 10);
            Methods.tpmsg(recipient, null, 7);

            new BukkitRunnable() {
                @Override
                public void run() {
                    if (request == null)
                        return;

                    for (Player players : Bukkit.getOnlinePlayers())
                        players.hidePlayer(plugin, recipient);

                    recipient.getWorld().strikeLightningEffect(recipient.getLocation());

                    recipient.teleport(user);
                    for (Player players : Bukkit.getOnlinePlayers())
                        players.showPlayer(plugin, recipient);
                    recipient.getWorld().strikeLightningEffect(recipient.getLocation());
                    recipient.playSound(recipient.getEyeLocation(), Sound.ENTITY_LIGHTNING_BOLT_IMPACT, 1.f, 1.f);
                    user.playSound(user.getEyeLocation(), Sound.ENTITY_LIGHTNING_BOLT_IMPACT, 1.f, 1.f);
                }
            }.runTaskLater(plugin, 100);
        } else if (request.getType() == Type.TPAHERE) {
            Methods.tpmsg(((Player) sender).getPlayer(), recipient, 7);
            Methods.tpmsg(recipient, ((Player) sender).getPlayer(), 10);

            new BukkitRunnable() {
                @Override
                public void run() {
                    if (request == null)
                        return;

                    for (Player players : Bukkit.getOnlinePlayers())
                        players.hidePlayer(plugin, user);

                    user.getWorld().strikeLightningEffect(user.getLocation());

                    user.teleport(recipient);
                    for (Player players : Bukkit.getOnlinePlayers())
                        players.showPlayer(plugin, user);
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