package org.yuri.aestheticnetwork.commands.duel;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.yuri.aestheticnetwork.utils.Initializer.teams;
import static org.yuri.aestheticnetwork.utils.Utils.translate;
import static org.yuri.aestheticnetwork.utils.Utils.translateo;
import static org.yuri.aestheticnetwork.utils.duels.DuelManager.*;

public class Duel implements CommandExecutor, TabExecutor {
    List<String> lg = List.of("Field", "Flat");
    List<String> lgsel = List.of("field", "flat");

    @Override
    public boolean onCommand(final @NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player user))
            return true;

        if (!user.hasPermission("has.staff")) {
            user.sendMessage(translateo("&7This feature has been disabled and will be reenabled soon"));
            return true;
        }

        int i = 1;
        String gm = "field";

        if (args.length == 0) {
            user.sendMessage(translateo("&7You must specify who you want to duel"));
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
            user.sendMessage(translateo("&7There are no open arenas yet"));
            return true;
        }

        Player recipient = Bukkit.getPlayer(args[0]);

        if (recipient == null) {
            user.sendMessage(translateo("&7You can't send duel requests to offline people"));
            return true;
        }

        if (recipient.getName().equalsIgnoreCase(sender.getName())) {
            user.sendMessage(translateo("&7You can't duel yourself"));
            return true;
        }

        DuelRequest tpr = getDUELrequest(recipient);

        if (teams.containsKey(recipient.getUniqueId())) {
            user.sendMessage(translateo("&7This player is already in a duel"));
            return true;
        }

        if (tpr != null) {
            user.sendMessage(translateo("&7This player already has an active request"));
            return true;
        }

        boolean legacy = args.length > 3 &&
                Objects.equals(args[3].toLowerCase(), "legacy");

        addDUELrequest(user,
                recipient,
                gm,
                i,
                0,
                0,
                check + 1,
                legacy);
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
                                args.length < 4 ? List.of("1", "2", "3", "4", "5") :
                                        args.length < 5 ? List.of("Legacy") : null;
    }
}