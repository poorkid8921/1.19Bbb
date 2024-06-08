package main.commands.essentials;

import main.utils.Instances.CustomPlayerDataHolder;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static main.utils.Initializer.*;
import static main.utils.modules.storage.DB.setRank;

public class SetRank implements CommandExecutor, TabExecutor {
    private final String[] ranks = new String[]{
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
        if (!sender.isOp() && playerData.get(sender.getName()).getRank() < 8)
            return true;
        if (args.length < 2) {
            sender.sendMessage(args.length == 1 ? "§7You must specify a player you want to rank!" : "§7You must specify a rank you want to give to the desired player!");
            return true;
        }

        String name = args[0];
        try {
            name = Bukkit.getPlayer(name).getName();
        } catch (Exception ignored) {
        }
        final int transformedArg = switch (args[1].toLowerCase()) {
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
        if (transformedArg > 3 && !sender.isOp()) {
            sender.sendMessage("§7You can't rank other players that rank!");
            return true;
        }
        final CustomPlayerDataHolder D0 = playerData.get(name);
        if (D0 == null) {
            sender.sendMessage("§7Couldn't find the specified player.");
            return true;
        }
        try {
            final Player player = Bukkit.getPlayer(name);
            switch (D0.getRank()) {
                case 1 -> cattoLovesTeam.removeEntity(player);
                case 2 -> cattoHatesTeam.removeEntity(player);
                case 3 -> gayTeam.removeEntity(player);
                case 4 -> vipTeam.removeEntity(player);
                case 5 -> boosterTeam.removeEntity(player);
                case 6 -> mediaTeam.removeEntity(player);
                case 7 -> trialHelperTeam.removeEntity(player);
                case 8 -> helperTeam.removeEntity(player);
                case 9 -> jrmodTeam.removeEntity(player);
                case 10 -> modTeam.removeEntity(player);
                case 11 -> adminTeam.removeEntity(player);
                case 12 -> managerTeam.removeEntity(player);
                case 13 -> ownerTeam.removeEntity(player);
            }
            switch (transformedArg) {
                case 1 -> cattoLovesTeam.addEntity(player);
                case 2 -> cattoHatesTeam.addEntity(player);
                case 3 -> gayTeam.addEntity(player);
                case 4 -> vipTeam.addEntity(player);
                case 5 -> boosterTeam.addEntity(player);
                case 6 -> mediaTeam.addEntity(player);
                case 7 -> trialHelperTeam.addEntity(player);
                case 8 -> helperTeam.addEntity(player);
                case 9 -> jrmodTeam.addEntity(player);
                case 10 -> modTeam.addEntity(player);
                case 11 -> adminTeam.addEntity(player);
                case 12 -> managerTeam.addEntity(player);
                case 13 -> ownerTeam.addEntity(player);
            }
        } catch (Exception ignored) {
        }
        D0.setRank(transformedArg);
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
