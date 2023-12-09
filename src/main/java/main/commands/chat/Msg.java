package main.commands.chat;

import main.Practice;
import main.utils.Initializer;
import main.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class Msg implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("§7You must specify who you want to message.");
            return true;
        } else if (args.length == 1) {
            sender.sendMessage("§7You must specify a message to send to the player.");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);

        if (target == null) {
            sender.sendMessage("§7You can't send messages to offline players.");
            return true;
        }

        String tn = target.getName();
        if (Practice.config.get("r." + tn + ".m") != null && !sender.hasPermission("has.staff")) {
            sender.sendMessage("§7You can't send messages to this player since they've locked their messages.");
            return true;
        }

        StringBuilder msgargs = new StringBuilder();

        for (int i = 1; i < args.length; i++)
            msgargs.append(args[i]).append(" ");

        sender.sendMessage("§6[§cme §6-> §c" + Utils.translate(target.getDisplayName()) + "§6] §r" + msgargs);
        target.sendMessage("§6[§c" + Utils.translate(((Player) sender).getDisplayName()) + " §6-> §cme§6] §r" + msgargs);
        String pn = sender.getName();
        Initializer.lastReceived.put(pn, tn);
        Initializer.lastReceived.put(tn, pn);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        return args.length < 1 ? Initializer.msg
                .stream()
                .sorted(String::compareToIgnoreCase)
                .collect(Collectors.toList()) :
                Initializer.msg
                        .stream()
                        .filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase()))
                        .sorted(String::compareToIgnoreCase)
                        .collect(Collectors.toList());
    }
}
