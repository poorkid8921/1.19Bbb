package main.commands.essentials;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static main.utils.Initializer.MAIN_COLOR;
import static main.utils.Initializer.playerData;
import static main.utils.storage.DB.setRank;

public class SetRank implements CommandExecutor, TabExecutor {
    String[] ranks = new String[]{
            "lub",
            "nigger",
            "gay",
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
    };

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

        String name = args[0];
        try {
            name = Bukkit.getPlayer(args[0]).getName();
        } catch (Exception ignored) {
        }
        int transformedArg = switch (args[1].toLowerCase()) {
            case "lub" -> 1;
            case "nigger" -> 2;
            case "gay" -> 3;
            case "vip" -> 4;
            case "booster" -> 5;
            case "media" -> 6;
            case "trial-helper" -> 7;
            case "helper" -> 8;
            case "jrmod" -> 9;
            case "mod" -> 10;
            case "admin" -> 11;
            case "manager" -> 12;
            case "executive" -> 13;
            default -> Integer.parseInt(args[1]);
        };
        try {
            playerData.get(name).setRank(transformedArg);
        } catch (Exception ignored) {
            sender.sendMessage("§7Couldn't find the specified player.");
            return true;
        }
        setRank(name, transformedArg);
        sender.sendMessage(MAIN_COLOR + name + "§7's rank is now " + MAIN_COLOR + switch (transformedArg) {
            case 1 -> "Catto Loves";
            case 2 -> "Catto Hates";
            case 3 -> "Gay";
            case 4 -> "Vip";
            case 5 -> "Booster";
            case 6 -> "Media";
            case 7 -> "Trial Helper";
            case 8 -> "Helper";
            case 9 -> "Junior Mod";
            case 10 -> "Mod";
            case 11 -> "Admin";
            case 12 -> "Manager";
            case 13 -> "Executive";
            default -> "Default";
        } + "!");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        return args.length < 2 ? null : args.length == 2 ? Arrays.stream(ranks).filter(s -> s.toLowerCase().startsWith(args[1].toLowerCase()))
                .sorted(String::compareToIgnoreCase)
                .collect(Collectors.toList()) : Collections.emptyList();
    }
}
