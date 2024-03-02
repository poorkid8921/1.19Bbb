package main.commands.economy;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import main.utils.Initializer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static main.utils.Utils.leaderBoardMoney;
import static main.utils.Utils.tabCompleteFilter;

public class Baltop implements CommandExecutor, TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        ObjectArrayList<Map.Entry<String, Integer>> top5 = new ObjectArrayList<>(leaderBoardMoney.entrySet());
        top5.sort(Map.Entry.comparingByValue());
        top5.subList(0, 9);
        Collections.reverse(top5);
        Bukkit.getServer().getLogger().warning(top5.toString());
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        return args.length == 0 ?
                Initializer.msg :
                tabCompleteFilter(Initializer.msg, args[0].toLowerCase());
    }
}
