package main.commands.essentials;

import main.utils.Instances.CustomPlayerDataHolder;
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

        String name = sender.getName();
        CustomPlayerDataHolder D0 = playerData.get(name);
        if (D0.getLastReceived() == null) {
            sender.sendMessage("§7You have no one to reply to.");
            return true;
        }

        Player target = Bukkit.getPlayer(D0.getLastReceived());
        if (target == null) {
            sender.sendMessage("§7You have no one to reply to.");
            return true;
        }

        StringBuilder msgargs = new StringBuilder();
        for (String arg : args) msgargs.append(arg).append(" ");

        CustomPlayerDataHolder D1 = playerData.get(D0.getLastReceived());
        D1.setLastReceived(name);
        sender.sendMessage("§6[§cme §6-> §c" + D1.getFRank(D0.getLastReceived()) + "§6] §r" + msgargs);
        target.sendMessage("§6[§c" + D0.getFRank(name) + " §6-> §cme§6] §r" + msgargs);
        return true;
    }
}