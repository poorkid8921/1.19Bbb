package main.commands.essentials;

import com.google.common.collect.ImmutableList;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static main.utils.Initializer.MAIN_COLOR;
import static main.utils.Initializer.playerData;

public class SetRank implements CommandExecutor, TabExecutor {
    ImmutableList<String> ranks = ImmutableList.of(
            "lub",
            "nigger",
            "gay",
            "quack",
            "clapclap",
            "vip",
            "booster",
            "media",
            "trial-helper",
            "helper",
            "jrmod",
            "mod",
            "admin",
            "manager",
            "executive"
    );

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.isOp()) {
            return true;
        } else if (args.length == 0) {
            sender.sendMessage("§7You must specify a player you want to rank!");
            return true;
        } else if (args.length == 1) {
            sender.sendMessage("§7You must specify a rank you want to give to the desired player!");
            return true;
        }

        String name = Bukkit.getOfflinePlayer(args[0]).getName();
        int transformedArg = switch (args[1].toLowerCase()) {
            case "lub" -> 1;
            case "nigger" -> 2;
            case "gay" -> 3;
            case "quack" -> 4;
            case "clapclap" -> 5;
            case "vip" -> 6;
            case "booster" -> 7;
            case "media" -> 8;
            case "trial-helper" -> 9;
            case "helper" -> 10;
            case "jrmod" -> 11;
            case "mod" -> 12;
            case "admin" -> 13;
            case "manager" -> 14;
            case "executive" -> 15;
            default -> Integer.parseInt(args[1]);
        };
        playerData.get(name).setRank(transformedArg);
        sender.sendMessage(MAIN_COLOR + name + "§7's rank is now " + MAIN_COLOR + switch (transformedArg) {
            case 1 -> "Catto Loves";
            case 2 -> "Catto Hates";
            case 3 -> "Gay";
            case 4 -> "Quack";
            case 5 -> "ClapClap";
            case 6 -> "Vip";
            case 7 -> "Booster";
            case 8 -> "Media";
            case 9 -> "Trial Helper";
            case 10 -> "Helper";
            case 11 -> "Junior Mod";
            case 12 -> "Mod";
            case 13 -> "Admin";
            case 14 -> "Manager";
            case 15 -> "Executive";
            default -> "Default";
        } + "!");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        return args.length == 0 ? null : args.length == 2 ? ranks : args.length == 3 ?
                ranks.stream().filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase()))
                        .sorted(String::compareToIgnoreCase)
                        .collect(Collectors.toList()) :
                Collections.emptyList();
    }
}
