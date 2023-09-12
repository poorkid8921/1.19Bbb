package org.yuri.aestheticnetwork.commands.duel;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.yuri.aestheticnetwork.utils.Languages;

import static org.yuri.aestheticnetwork.utils.Initializer.duel;
import static org.yuri.aestheticnetwork.utils.Utils.translate;
import static org.yuri.aestheticnetwork.utils.Utils.translateo;
import static org.yuri.aestheticnetwork.utils.duels.DuelManager.getDUELrequest;

@SuppressWarnings("deprecation")
public class DuelDeny implements CommandExecutor {
    public boolean onCommand(final @NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player user)) return true;

        String msg = Languages.EXCEPTION_NO_DUEL_REQ;
        DuelRequest request;

        if (args.length == 0) {
            request = getDUELrequest(user.getName());
        } else {
            request = getDUELrequest(user.getName(),
                    args[0].toLowerCase());
            msg = translate(Languages.EXCEPTION_NO_ACTIVE_DUELREQ + "#fc282f" + args[0] + ".");
        }

        if (request == null) {
            user.sendMessage(msg);
            return true;
        }

        Player recipient = Bukkit.getPlayer(request.getSender().getUniqueId());
        recipient.sendMessage(translate("#fc282f" + user.getDisplayName() + " &7denied your duel request"));
        user.sendMessage(translate("&7You have successfully deny #fc282f" + recipient.getDisplayName() + "&7's &7request"));
        duel.remove(request);

        return true;
    }
}