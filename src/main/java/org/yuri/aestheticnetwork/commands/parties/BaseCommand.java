package org.yuri.aestheticnetwork.commands.parties;

import io.papermc.lib.PaperLib;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yuri.aestheticnetwork.commands.tpa.TpaRequest;
import org.yuri.aestheticnetwork.utils.RequestManager;
import org.yuri.aestheticnetwork.utils.Type;
import org.yuri.aestheticnetwork.utils.Utils;

import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

import static org.yuri.aestheticnetwork.utils.RequestManager.*;
import static org.yuri.aestheticnetwork.utils.Utils.translate;

public class BaseCommand implements CommandExecutor, TabExecutor {
    private static final Pattern INVALIDCHARS = Pattern.compile("[^\t\n\r\u0020-\u007E\u0085\u00A0-\uD7FF\uE000-\uFFFC]");

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            sender.sendMessage(Utils.translate("&7You must define a subcommand."));
            return true;
        }

        Player p = (Player) sender;
        UUID puid = p.getUniqueId();

        switch (args[0].toLowerCase()) {
            case "create" -> {
                if (RequestManager.getParty(puid).getOwner() == puid) {
                    p.sendMessage(Utils.translate("&7You already have a party."));
                    return true;
                }
                if (RequestManager.memberInParty(puid)) {
                    p.sendMessage(Utils.translate("&7You are already in a party."));
                    return true;
                }
                if (args.length < 2) {
                    p.sendMessage(Utils.translate("&7You must specify a name for your party."));
                    return true;
                }
                if (args[1].length() > 16) {
                    p.sendMessage(Utils.translate("&7Your team name must be less than 16 characters."));
                    return true;
                }
                String name = INVALIDCHARS.matcher(args[1]).replaceAll("");
                RequestManager.parties_tabcomplete.add(name);
                RequestManager.parties.add(new Party(puid, name));
                p.sendMessage(Utils.translate("&7Your party has been successfully created."));
            }
            case "disband" -> {
                Party party = RequestManager.getParty(puid);
                if (party == null) {
                    p.sendMessage(Utils.translate("&7You aren't the owner of any team."));
                    return true;
                }
                RequestManager.parties_tabcomplete.remove(party.getName());
                RequestManager.parties.remove(RequestManager.getParty(puid));
                p.sendMessage(Utils.translate("&7Successfully disbanded your team."));
            }
            case "kick" -> {
                if (RequestManager.getParty(puid).getOwner() != puid) {
                    p.sendMessage(Utils.translate("&7You aren't the owner of any team."));
                    return true;
                }
                if (args.length < 2) {
                    p.sendMessage(Utils.translate("&7You must specify who you want to kick."));
                    return true;
                }
                Player pp = Bukkit.getPlayer(args[0]);
                if (pp == null) {
                    p.sendMessage(Utils.translate("&7You must specify an online player."));
                    return true;
                }
                RequestManager.getParty(puid).kickMember(pp.getUniqueId(),
                        pp.getName());
                p.sendMessage(Utils.translate("&7Successfully kicked &c" + pp.getDisplayName() + " &7from your team!"));
            }
            case "invite" -> {
                // TODO: finish?

                if (RequestManager.getParty(puid) == null) {
                    p.sendMessage(Utils.translate("&7You aren't the owner of any team."));
                    return true;
                }
                if (args.length < 2) {
                    p.sendMessage(Utils.translate("&7You must specify who you want to invite."));
                    return true;
                }
                Player pp = Bukkit.getPlayer(args[0]);
                if (pp == null) {
                    p.sendMessage(Utils.translate("&7You must specify an online player."));
                    return true;
                }

                if (RequestManager.getPartyRequest(pp.getUniqueId()).getSender() == p) {
                    p.sendMessage(Utils.translate("&c" + pp.getDisplayName() + " &7already has an ongoing invite from your party."));
                    return true;
                }

                RequestManager.invitePlayertoParty(p, pp);
                p.sendMessage(Utils.translate("&c" + pp.getDisplayName() + " &7has been successfully invited to your team!"));
            }
            case "accept" -> {
                PartyRequest request = getPartyRequest(puid);

                if (request == null) {
                    p.sendMessage(translate("&7You got no active invite."));
                    return true;
                }

                UUID targetName = request.getSender().getUniqueId();
                Player recipient = Bukkit.getPlayer(targetName);

                removePartyRequest(request);
                getParty(recipient.getUniqueId()).addMember(p.getUniqueId(), p.getName());
                p.sendMessage(translate("&7You are now in &c" + p.getDisplayName() + "&7's party!"));
            }
            case "deny" -> {

            }
            default -> sender.sendMessage(Utils.translate("&7Couldn't find the subcommand you specified."));
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return args.length > 1 ? RequestManager.getParty(((Player) sender).getUniqueId()).getMembersStr() :
                List.of("create", "disband", "invite", "kick");
    }
}
