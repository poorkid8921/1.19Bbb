package main.commands.essentials;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import static main.utils.Initializer.MAIN_COLOR;
import static main.utils.Initializer.playerData;

public class SetRank implements CommandExecutor {
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
            default -> Integer.parseInt(args[0]);
        };
        playerData.get(args[0]).setRank(transformedArg);
        sender.sendMessage(MAIN_COLOR + args[0] + "§7's rank is now " + MAIN_COLOR + switch (transformedArg) {
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
}
