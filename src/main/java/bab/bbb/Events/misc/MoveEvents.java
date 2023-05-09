package bab.bbb.Events.misc;

import bab.bbb.Bbb;
import bab.bbb.utils.Methods;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

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
                    if (p.isGliding()) {
                        Methods.errormsg(p, "nether roof is &cdisabled");
                        p.setGliding(false);
                    }
                    else
                        p.sendActionBar(Methods.parseText("&7Nether roof is &cdisabled"));

                    Methods.sendOpMessage("&7[&4ALERT&7]&e " + p.getDisplayName() + " &7tried to get above nether roof");
                }
            }
        }

        if (p.isGliding()) {
            if (Bbb.getTPSofLastSecond() <= cfgtps) {
                e.setCancelled(true);
                Methods.elytraflag(p, 1, 1, 1, e.getFrom());
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
            World rworld = Bukkit.getWorld(p.getWorld().getName());

            int y = rworld.getHighestBlockYAt((int) p.getLocation().getX(), (int) p.getLocation().getZ());
            p.teleport(new Location(rworld, p.getLocation().getX(), y, p.getLocation().getZ()));
            Methods.sendOpMessage("&7[&4ALERT&7]&e " + p.getDisplayName() + " &7tried to fly");
        }
    }
}