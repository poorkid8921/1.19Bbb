package org.yuri.aestheticnetwork.commands.duel;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yuri.aestheticnetwork.AestheticNetwork;
import org.yuri.aestheticnetwork.utils.RequestManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.yuri.aestheticnetwork.events.teams;
import static org.yuri.aestheticnetwork.utils.RequestManager.*;
import static org.yuri.aestheticnetwork.utils.Utils.translate;

public class Duel implements CommandExecutor, TabExecutor {
    List<String> lg = List.of("Field");
    List<String> lgsel = List.of("field");

    @Override
    public boolean onCommand(final @NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player user))
            return true;

        if (!user.hasPermission("has.staff")) {
            user.sendMessage(translate("&7This feature has been disabled and will be reenabled soon."));
            return true;
        }

        int i = 1;
        String gm = "field";

        if (args.length == 0) {
            user.sendMessage(translate("&7You must specify who you want to duel."));
            return true;
        }

        if (args.length == 2) {
            gm = String.valueOf(args[1]);

            if (!lgsel.contains(gm.toLowerCase()))
                gm = "field";
        }

        if (args.length == 3) {
            try {
                i = Integer.parseInt(args[2]);

                if (i < 1)
                    i = 1;
                else if (i > 3)
                    i = 3;
            } catch (Exception ignored) {
            }
        }

        int check = getAvailable(gm);

        if (check >= 6) {
            user.sendMessage(translate("&7There are no open arenas yet."));
            return true;
        }

        Player recipient = Bukkit.getPlayer(args[0]);

        if (recipient == null) {
            user.sendMessage(translate("&7You can't send duel requests to offline people!"));
            return true;
        }

        if (recipient.getName().equalsIgnoreCase(sender.getName())) {
            user.sendMessage(translate("&7You can't duel yourself!"));
            return true;
        }

        DuelRequest tpr = getDUELrequest(recipient);

        if (teams.containsKey(recipient.getUniqueId())) {
            user.sendMessage(translate("&7This player is already in a duel."));
            return true;
        }

        if (tpr != null) {
            user.sendMessage(translate("&7This player already has an active request."));
            return true;
        }

        boolean legacy = false;
        if (args.length == 4 & Objects.equals(args[3].toLowerCase(), "legacy")) {
            user.setMetadata("1.19.2", new FixedMetadataValue(AestheticNetwork.getInstance(), 0));
            recipient.setMetadata("1.19.2", new FixedMetadataValue(AestheticNetwork.getInstance(), 0));
            legacy = true;
        }

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
        ArrayList<String> str = Bukkit.getOnlinePlayers()
                .stream()
                .map(Player::getName)
                .sorted(String::compareToIgnoreCase).collect(Collectors.toCollection(ArrayList::new));
        str.addAll(parties_tabcomplete);
        return args.length < 1 ? str :
                args.length < 2 ? Bukkit.getOnlinePlayers()
                        .stream()
                        .map(Player::getName)
                        .filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase()))
                        .sorted(String::compareToIgnoreCase)
                        .collect(Collectors.toList()) :
                        args.length < 3 ? lg :
                                args.length < 4 ? List.of("1", "2", "3") :
                                        args.length < 5 ? List.of("Legacy") : null;
    }
}