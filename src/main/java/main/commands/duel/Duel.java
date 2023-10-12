package main.commands.duel;

import main.expansions.guis.Utils;
import main.utils.Initializer;
import main.utils.Instances.DuelHolder;
import main.utils.Languages;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

import static main.expansions.duels.Utils.*;

public class Duel implements CommandExecutor, TabExecutor {
    List<String> lg = List.of("Field", "Flat");
    List<String> lgsel = List.of("field", "flat");

    public int getGM(String i) {
        return switch (i.toLowerCase()) {
            case "field" -> 0;
            case "flat" -> 1;
            case "tank" -> 2;
            default -> throw new IllegalStateException("Unexpected value: " + i);
        };
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player user))
            return true;

        int i = 1;
        String gm = "field";

        if (args.length == 0) {
            Utils.openDuels0(user);
            return true;
        }

        if (args.length > 1) {
            gm = String.valueOf(args[1]).toLowerCase();

            if (!lgsel.contains(gm.toLowerCase()))
                gm = "field";
        }

        if (args.length > 2) {
            try {
                i = Integer.parseInt(args[2]);

                if (i < 1)
                    i = 1;
                else if (i > 5)
                    i = 5;
            } catch (Exception ignored) {
            }
        }

        int d = getGM(gm);
        int check = Duel_GetDuelsAvailableForGM(d);

        if (check >= 32) {
            user.sendMessage(Languages.EXCEPTION_NO_ARENAS_OPEN);
            return true;
        }

        Player recipient = Bukkit.getPlayer(args[0]);

        if (recipient == null) {
            user.sendMessage(Languages.EXCEPTION_DUEL_TARGET_OFF);
            return true;
        }

        if (recipient.getName().equalsIgnoreCase(sender.getName())) {
            user.sendMessage(Languages.EXCEPTION_DUEL_SELF);
            return true;
        }

        DuelHolder tpr = getDUELrequest(recipient.getName());

        if (Initializer.teams.containsKey(recipient.getName())) {
            user.sendMessage(Languages.EXCEPTION_ALREADY_IN_DUEL);
            return true;
        }

        if (tpr != null) {
            if (tpr.getSender().equals(user)) {
                user.sendMessage(Languages.GLOBAL_EXCEPTION_ALREADY_REQ);
                return true;
            } else if (tpr.getReceiver().equals(user)) {
                Duel_Accept_Request(tpr, user);
                return true;
            }
        }

        addDUELrequest(user,
                recipient,
                d,
                i,
                0,
                0,
                check + 1,
                1);
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return args.length < 2 ? Bukkit.getOnlinePlayers()
                .stream()
                .map(Player::getName)
                .filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase()))
                .sorted(String::compareToIgnoreCase)
                .collect(Collectors.toList()) :
                args.length < 3 ? lg :
                        args.length < 4 ? List.of("1", "2", "3", "4", "5") : null;
    }
}