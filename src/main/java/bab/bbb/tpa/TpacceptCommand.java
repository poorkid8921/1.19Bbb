package bab.bbb.tpa;

import bab.bbb.Bbb;
import bab.bbb.tpa.TpaRequest;
import bab.bbb.utils.Type;
import io.papermc.lib.PaperLib;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import static bab.bbb.utils.Utils.*;

public class TpacceptCommand implements CommandExecutor {
    public boolean onCommand(final @NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player user))
            return true;

        TpaRequest request = getRequest(user);

        if (request == null) {
            user.sendMessage(translate("[&dTPA&r] Couldn't find any ongoing teleport requests."));
            return true;
        }

        String targetName = request.getSender().getName();
        Player recipient = Bukkit.getPlayer(targetName);
        Player tempuser;
        Player temprecipient;

        if (request.getType() == Type.TPA) {
            tempuser = recipient;
            temprecipient = user;
        } else {
            tempuser = user;
            temprecipient = recipient;
        }

        assert tempuser != null;
        assert temprecipient != null;
        tempuser.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 1));
        tempuser.getWorld().spawnParticle(Particle.TOTEM, tempuser.getLocation(), 50);

        temprecipient.sendMessage(translate("[&dTPA&r] Accepted &d" + tempuser.getName() + "'s &rteleport request."));
        temprecipient.sendMessage(translate("[&dTPA&r] Teleporting..."));
        tempuser.sendMessage(translate("[&dTPA&r] Teleporting..."));

        vanish(tempuser);
        PaperLib.teleportAsync(tempuser, temprecipient.getLocation()).thenAccept((result) -> {
            Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> unVanish(tempuser), 5L);
            tempuser.getWorld().spawnParticle(Particle.TOTEM, tempuser.getLocation(), 50);
        });

        removeRequest(tempuser);
        removeRequest(temprecipient);
        return true;
    }
}