package main.commands.essentials;

import main.utils.Initializer;
import main.utils.Instances.CustomPlayerDataHolder;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.List;

import static main.utils.Initializer.playerData;
import static main.utils.Utils.tabCompleteFilter;

public class Msg implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(args.length == 0 ? "§7You must specify who you want to message!" : "§7You must specify a message to send to the player!");
            return true;
        }

        final Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage("§7You can't message offline players.");
            return true;
        }

        final String name = sender.getName();
        final String targetName = target.getName();
        final CustomPlayerDataHolder D0 = playerData.get(name);
        final CustomPlayerDataHolder D1 = playerData.get(targetName);
        if (D1.getMtoggle() == 1 && D0.getRank() < 9) {
            sender.sendMessage("§7You can't message this player since they've locked their messages.");
            return true;
        }

        StringBuilder msg = new StringBuilder();
        for (int i = 1; i < args.length; i++)
            msg.append(args[i]).append(" ");
        sender.sendMessage("§6[§cme §6-> §c" + D1.getFRank(targetName) + "§6] §r" + msg);
        target.sendMessage("§6[§c" + D0.getFRank(name) + " §6-> §cme§6] §r" + msg);
        D0.setLastReceived(targetName);
        D1.setLastReceived(name);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        return args.length == 0 ?
                Initializer.msg : args.length == 1 ?
                tabCompleteFilter(Initializer.msg, args[0].toLowerCase()) : null;
    }
}