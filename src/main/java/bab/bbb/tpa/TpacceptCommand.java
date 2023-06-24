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
        tempuser.getWorld().strikeLightningEffect(tempuser.getLocation());
        assert temprecipient != null;
        PaperLib.teleportAsync(tempuser, temprecipient.getLocation()).thenAccept(result -> {
            if (result) {
                temprecipient.getWorld().strikeLightningEffect(tempuser.getLocation());
            } else
                temprecipient.sendMessage("&7Something went wrong.");
        });

        tpmsg(temprecipient, tempuser.getName(), 8);
        tpmsg(tempuser, temprecipient.getName(), 9);

        removeRequest(user);
        return true;
    }
}