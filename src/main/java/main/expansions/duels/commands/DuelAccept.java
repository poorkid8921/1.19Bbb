package main.expansions.duels.commands;

import main.utils.Initializer;
import main.utils.Instances.DuelHolder;
import main.utils.Languages;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

import static main.expansions.guis.Utils.updateDuels;
import static main.expansions.guis.Utils.updateSpectate;
import static main.utils.DuelUtils.duelsavailable;
import static main.utils.DuelUtils.start;
import static main.utils.Initializer.teams;
import static main.utils.Languages.MAIN_COLOR;
import static main.utils.RequestManager.getDUELrequest;

public class DuelAccept implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String msg = Languages.EXCEPTION_NO_DUEL_REQ;
        DuelHolder request;

        String sn = sender.getName();
        if (args.length == 0) {
            request = getDUELrequest(sn);
        } else {
            request = getDUELrequest(sn, args[0].toLowerCase());
            msg = Languages.EXCEPTION_NO_ACTIVE_DUELREQ + MAIN_COLOR + args[0] + ".";
        }

        if (request == null) {
            sender.sendMessage(msg);
            return true;
        }

        teams.putAll(Map.of(request.getSenderF(), 0,
                sn, 1));

        int check = duelsavailable(request.getType());
        if (check == 32) {
            Initializer.duel.remove(request);
            sender.sendMessage(Languages.EXCEPTION_NO_ARENAS_OPEN);
            return true;
        }

        Initializer.duel.remove(request);
        Initializer.inDuel.add(request);
        start((Player) sender, request.getSender(), request.getType(), 1, request.getMaxrounds(), check + 1);
        updateDuels();
        updateSpectate();
        return true;
    }
}