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
public class TpaCommand implements CommandExecutor {
    public TpaCommand() {
    }

    @Override
    public boolean onCommand(final @NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player user))
            return true;

        if (args.length < 1) {
            errormsgs(user, 1, "");
            return true;
        }

        Player recipient = Bukkit.getPlayer(args[0]);

        if (recipient == null) {
            errormsgs(user, 2, args[0]);
            return true;
        }

        if (recipient.getName().equalsIgnoreCase(sender.getName())) {
            tpmsg(user, recipient.getName(), 14);
            return true;
        }

        if (getRequest(recipient) != null) {
            tpmsg(user, recipient.getName(), 13);
            return true;
        }
        if (getRequest(user) != null)
            removeRequest(user);

        addRequest(user, recipient, Type.TPA);
        recipient.playSound(recipient.getEyeLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.f, 1.f);
        tpmsg(recipient, user.getName(), 3);
        tpmsg(user, recipient.getName(), 1);
        new BukkitRunnable() {
            @Override
            public void run() {
                    removeRequest(recipient);
            }
        }.runTaskLater(Bbb.getInstance(), 30 * 20);
        return true;
    }
}