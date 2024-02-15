package main.commands;

import main.utils.Utils;
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

        String pn = sender.getName();
        CustomPlayerDataHolder D = playerData.get(pn);
        if (D.getLastReceived() == null) {
            sender.sendMessage("§7You have no one to reply to.");
            return true;
        }

        Player target = Bukkit.getPlayer(D.getLastReceived());
        if (target == null) {
            sender.sendMessage("§7You have no one to reply to.");
            return true;
        }

        StringBuilder msgargs = new StringBuilder();
        for (String arg : args) msgargs.append(arg).append(" ");

        playerData.get(D.getLastReceived()).setLastReceived(pn);
        sender.sendMessage("§6[§cme §6-> §c" + Utils.translate(target.getDisplayName()) + "§6] §r" + msgargs);
        target.sendMessage("§6[§c" + Utils.translate(((Player) sender).getDisplayName()) + " §6-> §cme§6] §r" + msgargs);
        return true;
    }
}