package main.commands.essentials;

import main.utils.instances.CustomPlayerDataHolder;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static main.utils.Initializer.playerData;

public class Reply implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("§7You must specify a message to send to the player!");
            return true;
        }

        final String name = sender.getName();
        final CustomPlayerDataHolder D0 = playerData.get(name);
        if (D0.getLastReceived() == null) {
            sender.sendMessage("§7You have no one to reply to.");
            return true;
        }

        final Player target = Bukkit.getPlayer(D0.getLastReceived());
        if (target == null) {
            sender.sendMessage("§7You have no one to reply to.");
            return true;
        }

        final StringBuilder msg = new StringBuilder();
        for (final String arg : args) msg.append(arg).append(" ");
        final CustomPlayerDataHolder D1 = playerData.get(D0.getLastReceived());
        D1.setLastReceived(name);
        sender.sendMessage("§6[§cme §6-> §c" + D1.getFRank(D0.getLastReceived()) + "§6] §r" + msg);
        target.sendMessage("§6[§c" + D0.getFRank(name) + " §6-> §cme§6] §r" + msg);
        return true;
    }
}