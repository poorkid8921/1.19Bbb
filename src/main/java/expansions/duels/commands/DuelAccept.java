package expansions.duels.commands;

import main.utils.Constants;
import main.utils.Instances.DuelHolder;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

import static expansions.guis.Utils.updateDuels;
import static expansions.guis.Utils.updateSpectate;
import static main.utils.Constants.MAIN_COLOR;
import static main.utils.Constants.teams;
import static main.utils.DuelUtils.getDuelsAvailable;
import static main.utils.DuelUtils.start;
import static main.utils.RequestManager.getDUELrequest;

public class DuelAccept implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String msg = Constants.EXCEPTION_NO_DUEL_REQ;
        DuelHolder request;
        String n = "";
        String un = sender.getName();

        if (args.length == 0) {
            request = getDUELrequest(un);
        } else {
            n = args[0];
            Player p = Bukkit.getPlayer(n);
            if (p == null) {
                sender.sendMessage(msg);
                return true;
            } else
                n = p.getName();
            request = getDUELrequest(un, n);
            msg = Constants.EXCEPTION_NO_ACTIVE_DUELREQ +
                    MAIN_COLOR +
                    args[0] +
                    ".";
        }

        if (request == null) {
            sender.sendMessage(msg);
            return true;
        } else if (un.equals(n)) {
            sender.sendMessage(Constants.EXCEPTION_PLAYER_DUELSELF);
            return true;
        }

        teams.putAll(Map.of(n, 0,
                un, 1));

        int check = getDuelsAvailable(request.getType());
        if (check == 32) {
            Constants.duel.remove(request);
            sender.sendMessage(Constants.EXCEPTION_NO_ARENAS_OPEN);
            return true;
        }

        Constants.duel.remove(request);
        Constants.inDuel.add(request);
        start((Player) sender, request.getSender(), request.getType(), 1, request.getMaxrounds(), check + 1);
        updateDuels();
        updateSpectate();
        return true;
    }
}