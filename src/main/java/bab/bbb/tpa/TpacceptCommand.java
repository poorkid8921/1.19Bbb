package bab.bbb.tpa;

import bab.bbb.Bbb;
import bab.bbb.utils.Type;
import io.papermc.lib.PaperLib;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static bab.bbb.utils.Utils.*;

public class TpacceptCommand implements CommandExecutor {
    public TpacceptCommand() {
    }

    public boolean onCommand(final @NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player user))
            return true;

        TpaRequest request = getRequest(user);

        if (request == null) {
            tpmsg(user, null, 15);
            return true;
        }

        String targetName = request.getSender().getName();
        Player recipient = Bukkit.getPlayer(targetName);

        if (recipient == null) {
            errormsgs(user, 2, targetName);
            return true;
        }

        if (combattag.containsKey(user.getUniqueId())) {
            tpmsg(user, recipient, 16);
            return true;
        }

        World a = user.getWorld();
        World b = recipient.getWorld();

        if (request.getType() == Type.TPA) {
            tpmsg(((Player) sender).getPlayer(), recipient, 10);
            tpmsg(recipient, null, 7);

            b.strikeLightningEffect(recipient.getLocation());

            PaperLib.teleportAsync(recipient, user.getLocation()).thenAccept(result -> {
                if (result) {
                    b.strikeLightningEffect(recipient.getLocation());
                    b.playSound(recipient.getEyeLocation(), Sound.ENTITY_LIGHTNING_BOLT_IMPACT, 1.f, 1.f);
                    a.playSound(user.getEyeLocation(), Sound.ENTITY_LIGHTNING_BOLT_IMPACT, 1.f, 1.f);
                } else
                    errormsgs(recipient, 30, targetName);
            });
        } else if (request.getType() == Type.TPAHERE) {
            tpmsg(((Player) sender).getPlayer(), recipient, 7);
            tpmsg(recipient, ((Player) sender).getPlayer(), 10);

            a.strikeLightningEffect(user.getLocation());
            PaperLib.teleportAsync(user, recipient.getLocation()).thenAccept(result -> {
                if (result) {
                    a.strikeLightningEffect(user.getLocation());
                    a.playSound(user.getEyeLocation(), Sound.ENTITY_LIGHTNING_BOLT_IMPACT, 1.f, 1.f);
                    b.playSound(recipient.getEyeLocation(), Sound.ENTITY_LIGHTNING_BOLT_IMPACT, 1.f, 1.f);
                } else
                    errormsgs(user, 30, targetName);
            });
        }

        removeRequest(user);
        return true;
    }
}