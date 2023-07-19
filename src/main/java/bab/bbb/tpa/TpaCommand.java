package bab.bbb.tpa;

import bab.bbb.utils.Type;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static bab.bbb.utils.Utils.*;

@SuppressWarnings("deprecation")
public class TpaCommand implements CommandExecutor {
    @Override
    public boolean onCommand(final @NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player user))
            return true;

        if (args.length < 1) {
            user.sendMessage(translate("[&dTPA&r] You must specify to who you want to teleport."));
            return true;
        }

        Player recipient = Bukkit.getPlayer(args[0]);

        if (recipient == null) {
            user.sendMessage(translate("[&dTPA&r] You must specify to who you want to teleport."));
            return true;
        }

        if (recipient.getName().equalsIgnoreCase(sender.getName())) {
            user.sendMessage(translate("[&dTPA&r] You must specify to who you want to teleport."));
            return true;
        }

        addRequest(user, recipient, Type.TPA);
        return true;
    }
}