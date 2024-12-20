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
        if (args.length == 0) {
            sender.sendMessage("§7You must specify who you want to message!");
            return true;
        } else if (args.length == 1) {
            sender.sendMessage("§7You must specify a message to send to the player!");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage("§7You can't message offline players.");
            return true;
        }

        String name = sender.getName();
        String tn = target.getName();
        CustomPlayerDataHolder D0 = playerData.get(name);
        CustomPlayerDataHolder D1 = playerData.get(tn);
        if (D1.getMtoggle() == 1 && D0.getRank() < 7) {
            sender.sendMessage("§7You can't message this player since they've locked their messages.");
            return true;
        }

        StringBuilder msg = new StringBuilder();
        for (int i = 1; i < args.length; i++)
            msg.append(args[i]).append(" ");

        sender.sendMessage("§6[§cme §6-> §c" + D1.getFRank(tn) + "§6] §r" + msg);
        target.sendMessage("§6[§c" + D0.getFRank(name) + " §6-> §cme§6] §r" + msg);
        D0.setLastReceived(tn);
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