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

        if (request.getType() == Type.TPAHERE) {
            temprecipient = user;
            tempuser = recipient;
        } else {
            tempuser = user;
            temprecipient = recipient;
        }

        assert temprecipient != null;
        World recipientWorld = temprecipient.getWorld();
        assert tempuser != null;
        World userWorld = tempuser.getWorld();

        tpmsg(temprecipient, tempuser.getName(), 8);
        tpmsg(tempuser, temprecipient.getName(), 9);
        userWorld.strikeLightningEffect(tempuser.getLocation());
        PaperLib.teleportAsync(tempuser, temprecipient.getLocation()).thenAccept(result -> {
            if (result) {
                recipientWorld.strikeLightningEffect(tempuser.getLocation());
            } else
                errormsgs(tempuser, 30, targetName);
        });

        removeRequest(tempuser);
        removeRequest(temprecipient);
        return true;
    }
}