package expansions.duels.commands;

import expansions.guis.Utils;
import main.utils.Constants;
import main.utils.Instances.DuelHolder;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

import static main.utils.DuelUtils.getDuelsAvailable;
import static main.utils.RequestManager.addDUELrequest;
import static main.utils.RequestManager.getDUELrequest;

public class Duel implements CommandExecutor, TabExecutor {
    List<String> lg = List.of("Field", "Flat", "Tank");
    List<String> lgsel = List.of("field", "flat", "tank");

    public int getGM(String i) {
        return switch (i.toLowerCase()) {
            case "flat" -> 1;
            case "tank" -> 2;
            default -> 0;
        };
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        int i = 1;
        String gm = "field";

        if (!sender.hasPermission("has.staff"))
            return true;

        if (args.length == 0) {
            Utils.openDuels0((Player) sender);
            return true;
        }

        if (args.length > 1) {
            gm = String.valueOf(args[1]).toLowerCase();

            if (!lgsel.contains(gm)) gm = "field";
        }

        if (args.length > 2) {
            try {
                i = Integer.parseInt(args[2]);

                if (i < 1) i = 1;
                else if (i > 5) i = 5;
            } catch (Exception ignored) {
            }
        }

        int d = getGM(gm);
        int check = getDuelsAvailable(d);

        if (check == 32) {
            sender.sendMessage("§7There are no open arenas yet.");
            return true;
        }

        Player recipient = Bukkit.getPlayer(args[0]);

        if (recipient == null) {
            sender.sendMessage("§7You can't send duel requests to offline players.");
            return true;
        }

        if (recipient.getName().equalsIgnoreCase(sender.getName())) {
            sender.sendMessage("§7You can't duel yourself.");
            return true;
        }

        DuelHolder tpr = getDUELrequest(recipient.getName());

        if (Constants.teams.containsKey(recipient.getName())) {
            sender.sendMessage("§7You can't duel yourself.");
            return true;
        }

        if (tpr != null) {
            if (tpr.getSender().equals(sender)) {
                sender.sendMessage("§7You already have an ongoing request to this player.");
                return true;
            }
        }

        addDUELrequest((Player) sender, recipient, d, i, 0, 0, check + 1, 1);
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return args.length < 2 ? Bukkit.getOnlinePlayers().stream().map(Player::getName).filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase())).sorted(String::compareToIgnoreCase).collect(Collectors.toList()) : args.length < 3 ? lg : args.length < 4 ? List.of("1", "2", "3", "4", "5") : null;
    }
}