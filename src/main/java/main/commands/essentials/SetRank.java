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

import static main.Economy.databaseManager;
import static main.utils.Initializer.MAIN_COLOR;
import static main.utils.Initializer.playerData;

public class SetRank implements CommandExecutor, TabExecutor {
    private final String[] ranks = {"lub", "nigger", "gay", "vip", "booster", "media", "trial-helper", "helper", "jrmod", "mod", "admin", "manager", "executive"};

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.isOp() && playerData.get(sender.getName()).getRank() < 8) return true;

        if (args.length < 2) {
            sender.sendMessage(args.length == 1 ? "§7You must specify a player you want to rank!" : "§7You must specify a rank you want to give to the desired player!");
            return true;
        }

        String name = args[0];
        try {
            name = Bukkit.getPlayer(name).getName();
        } catch (Exception ignored) {}

        int rankIndex = Arrays.asList(ranks).indexOf(args[1].toLowerCase());
        if (rankIndex == -1) {
            try {
                rankIndex = Integer.parseInt(args[1]) - 1;
            } catch (NumberFormatException e) {
                sender.sendMessage("§7Invalid rank!");
                return true;
            }
        }

        if (rankIndex > 2 && !sender.isOp()) {
            sender.sendMessage("§7You can't rank other players that rank!");
            return true;
        }

        try {
            playerData.get(name).setRank(rankIndex + 1);
        } catch (Exception e) {
            sender.sendMessage("§7Couldn't find the specified player.");
            return true;
        }

        databaseManager.setRank(name, rankIndex + 1);
        sender.sendMessage(MAIN_COLOR + name + "§7's rank is now " + MAIN_COLOR + getRankName(rankIndex + 1) + "!");
        return true;
    }

    private String getRankName(int rank) {
        return switch (rank) {
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
        };
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        return args.length < 2 ? null : args.length == 2 ? Arrays.stream(ranks)
                .filter(s -> s.toLowerCase().startsWith(args[1].toLowerCase()))
                .sorted(String::compareToIgnoreCase)
                .collect(Collectors.toList()) : Collections.emptyList();
    }
}