package main.commands.essentials;

import com.google.common.collect.ImmutableList;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.List;
import java.util.stream.Collectors;

import static main.utils.Initializer.MAIN_COLOR;
import static main.utils.Initializer.playerData;
import static main.utils.storage.DB.setRank;

public class SetRank implements CommandExecutor, TabExecutor {
    ImmutableList<String> ranks = ImmutableList.of(
            "lub",
            "nigger",
            "gay",
            "angel",
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

        String name = args[0];
        try {
            name = Bukkit.getPlayer(args[0]).getName();
        } catch (Exception ignored) {
        }
        int transformedArg = switch (args[1].toLowerCase()) {
            case "angel" -> 1;
            case "lub" -> 2;
            case "nigger" -> 3;
            case "gay" -> 4;
            case "vip" -> 5;
            case "booster" -> 6;
            case "media" -> 7;
            case "trial-helper" -> 8;
            case "helper" -> 9;
            case "jrmod" -> 10;
            case "mod" -> 11;
            case "admin" -> 12;
            case "manager" -> 13;
            case "executive" -> 14;
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
            case 1 -> "Angel";
            case 2 -> "Catto Loves";
            case 3 -> "Catto Hates";
            case 4 -> "Gay";
            case 5 -> "Vip";
            case 6 -> "Booster";
            case 7 -> "Media";
            case 8 -> "Trial Helper";
            case 9 -> "Helper";
            case 10 -> "Junior Mod";
            case 11 -> "Mod";
            case 12 -> "Admin";
            case 13 -> "Manager";
            case 14 -> "Executive";
            default -> "Default";
        } + "!");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        return args.length == 1 ? null : ranks.stream().filter(s -> s.toLowerCase().startsWith(args[1].toLowerCase()))
                .sorted(String::compareToIgnoreCase)
                .collect(Collectors.toList());
    }
}
