package bab.bbb.Events.misc;

import bab.bbb.Bbb;
import bab.bbb.utils.Utils;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

import static bab.bbb.utils.Utils.combattag;

@SuppressWarnings("deprecation")
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
                        Utils.errormsgs(p, 27, "");
                        p.setGliding(false);
                    }
                    else
                        p.sendActionBar(Utils.translate("&7Nether roof is &cdisabled"));

                    Utils.sendOpMessage("&7[&4INFO&7]&e " + p.getDisplayName() + " &7tried to get above nether roof");
                }
            }
        }

        if (p.isGliding()) {
            if (Bbb.getTPSofLastSecond() <= cfgtps) {
                Utils.elytraflag(p, 1, 1, 1, e.getFrom());
                e.setCancelled(true);
                return;
            }

            if (combattag.contains(p.getName()))
            {
                p.playSound(p.getEyeLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
                int y = p.getWorld().getHighestBlockYAt((int) e.getFrom().getX(), (int) e.getFrom().getZ()) + 2;
                p.teleport(new Location(e.getFrom().getWorld(), e.getFrom().getX(), y, e.getFrom().getX()));
                Utils.errormsgs(p, 28, "");
                e.setCancelled(true);
                p.setGliding(false);
                return;
            }

            double speed = Utils.blocksPerTick(e.getFrom(), e.getTo());

            if (speed > 2.05)
                Utils.elytraflag(p, 2, 0, 0, null);
            else
                p.sendActionBar(Utils.translate("&7%speed% / 2.00").replace("%speed%", Utils.speed(speed)));
        }
    }
}