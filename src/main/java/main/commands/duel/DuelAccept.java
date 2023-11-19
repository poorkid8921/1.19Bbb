package main.commands.duel;

import main.utils.Initializer;
import main.utils.Instances.DuelHolder;
import main.utils.Languages;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

import static main.expansions.duels.Utils.*;
import static main.expansions.guis.Utils.updateDuels;
import static main.expansions.guis.Utils.updateSpectate;
import static main.utils.Initializer.teams;
import static main.utils.Languages.MAIN_COLOR;

public class DuelAccept implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player user))
            return true;

        String msg = Languages.EXCEPTION_NO_DUEL_REQ;
        DuelHolder request;

        if (args.length == 0) {
            request = getDUELrequest(user.getName());
        } else {
            request = getDUELrequest(user.getName(),
                    args[0].toLowerCase());
            msg = Languages.EXCEPTION_NO_ACTIVE_DUELREQ + MAIN_COLOR + args[0] + ".";
        }

        if (request == null) {
            user.sendMessage(msg);
            return true;
        }

        String targetUID = request.getSender().getName();
        Player recipient = Bukkit.getPlayer(targetUID);
        teams.putAll(Map.of(targetUID, 0,
                user.getName(), 1));

        int check = Duel_GetDuelsAvailableForGM(request.getType());
        if (check == 32) {
            Initializer.duel.remove(request);
            user.sendMessage(Languages.EXCEPTION_NO_ARENAS_OPEN);
            return true;
        }

        Initializer.duel.remove(request);
        Initializer.inDuel.add(request);
        Duel_Start(user,
                recipient,
                request.getType(),
                1,
                request.getMaxrounds(),
                check + 1);
        updateDuels();
        updateSpectate();
        return true;
    }
}