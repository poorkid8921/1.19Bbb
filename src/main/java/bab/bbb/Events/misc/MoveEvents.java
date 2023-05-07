package bab.bbb.Events.misc;

import bab.bbb.Bbb;
import bab.bbb.utils.Methods;
import org.bukkit.*;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerVelocityEvent;

import java.util.logging.Level;

public class MoveEvents implements Listener {
    private static final int cfgtps = Bbb.getInstance().config.getInt("take-anti-lag-measures-if-tps");

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();

        if (p.isOp())
            return;

        // anti god
        /*if (p.isInsideVehicle() && !p.getVehicle().isValid())
            p.getVehicle().eject();

        if (!p.isValid() && !p.isDead())
            Methods.maskedkick(p);*/

        if (p.getWorld().getEnvironment() == World.Environment.NETHER) {
            if (p.getLocation().getY() > 128) {
                if (Bbb.getInstance().getConfig().getBoolean("anti-netherroof")) {
                    p.playSound(p.getEyeLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.f, 1.f);
                    p.teleport(new Location(p.getWorld(), p.getLocation().getX(), p.getLocation().getY() - 5, p.getLocation().getZ()));
                    if (p.isGliding())
                        Methods.errormsg(p, "nether roof is &cdisabled");
                    else
                        p.sendActionBar(Methods.parseText("&7Nether roof is &cdisabled"));
                }
            }
        }

        if (p.isGliding()) {
            if (Bbb.getTPSofLastSecond() <= cfgtps) {
                e.setCancelled(true);
                Methods.elytraflag(p, 1, 1, 0, null);
                return;
            }

            double speed = Methods.blocksPerTick(e.getFrom(), e.getTo());

            if (speed > 2.05)
                Methods.elytraflag(p, 2, 0, 0, null);
            else
                p.sendActionBar(Methods.parseText("&7%speed%&6/&72.00").replace("%speed%", Methods.speed(speed)));
        }
        else if (p.isFlying()) {
            p.teleport(e.getFrom());
            e.setCancelled(true);
        }
    }
}