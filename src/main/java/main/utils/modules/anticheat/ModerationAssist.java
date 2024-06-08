package main.utils.modules.anticheat;

import main.utils.Initializer;
import main.utils.Instances.CustomPlayerDataHolder;
import main.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import static main.utils.Initializer.*;
import static main.utils.Utils.banEffect;

public class ModerationAssist {
    public static boolean checkFlat(final Player damager, final String name, final CustomPlayerDataHolder D0,
                                    final CustomPlayerDataHolder D1) {
        if (inFlat.contains(name) &&
                !D0.getLastTaggedBy().equals(name) &&
                D1.incrementFlatFlags() == 3) {
            D0.setFlatFlags(0);
            Initializer.bannedFromflat.add(name);
            banEffect(damager);
            damager.teleportAsync(spawn, PlayerTeleportEvent.TeleportCause.COMMAND).thenAccept(result -> {
                damager.sendMessage("§7You are now banned from flat for " + SECOND_COLOR + "Interrupting");
                Utils.rotateNPCs(spawn, ((CraftPlayer) damager).getHandle().connection);
                atSpawn.add(name);
            });
            final String msg = SECOND_COLOR + name + " §7has been banned from " + SECOND_COLOR + "flat §7by ModerationAssist";
            for (final Player k : Bukkit.getOnlinePlayers()) {
                if (playerData.get(k.getName()).getRank() > 6) {
                    k.sendMessage(msg);
                }
            }
            return false;
        }
        return true;
    }
}
