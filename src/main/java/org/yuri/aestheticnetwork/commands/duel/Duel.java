package org.yuri.aestheticnetwork.commands.duel;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yuri.aestheticnetwork.utils.Messages.Languages;

import java.util.List;
import java.util.stream.Collectors;

import static org.yuri.aestheticnetwork.utils.Messages.Initializer.teams;
import static org.yuri.aestheticnetwork.utils.duels.DuelManager.*;

public class Duel implements CommandExecutor, TabExecutor {
    List<String> lg = List.of("Field", "Flat");
    List<String> lgsel = List.of("field", "flat");

    @Override
    public boolean onCommand(final @NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player user))
            return true;

        int i = 1;
        String gm = "field";

        if (args.length == 0) {
            user.sendMessage(Languages.WHO_DUEL);
            //new DuelInventory(user).open();
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

        int check = getAvailable(gm);

        if (check >= 6) {
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

        DuelRequest tpr = getDUELrequest(recipient.getName());

        if (teams.containsKey(recipient.getName())) {
            user.sendMessage(Languages.EXCEPTION_ALREADY_IN_DUEL);
            return true;
        }

        if (tpr != null) {
            if (tpr.getSender().equals(user)) {
                user.sendMessage(Languages.GLOBAL_EXCEPTION_ALREADY_REQ);
                return true;
            }
            else if (tpr.getReciever().equals(user)) {
                duelaccept(tpr, user);
                return true;
            }
        }

        addDUELrequest(user,
                recipient,
                gm,
                i,
                0,
                0,
                check + 1);
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
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