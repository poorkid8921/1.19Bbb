package main.commands;

import main.utils.Initializer;
import main.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Reply implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;
        if (args.length == 0) {
            sender.sendMessage("§7You must specify a message to send to the player.");
            return true;
        }

        String pn = player.getName();
        if (!Initializer.lastReceived.containsKey(pn)) {
            sender.sendMessage("§7You have no one to reply to.");
            return true;
        }

        Player target = Bukkit.getPlayer(Initializer.lastReceived.get(pn));
        if (target == null) {
            Initializer.lastReceived.remove(pn);
            sender.sendMessage("§7You have no one to reply to.");
            return true;
        }

        StringBuilder msgargs = new StringBuilder();
        for (String arg : args) msgargs.append(arg).append(" ");

        sender.sendMessage("§6[§cme §6-> §c" + Utils.translate(target.getDisplayName()) + "§6] §r" + msgargs);
        target.sendMessage("§6[§c" + Utils.translate(player.getDisplayName()) + " §6-> §cme§6] §r" + msgargs);
        String tn = target.getName();
        Initializer.lastReceived.put(tn, pn);
        return true;
    }
}