package main.managers;

import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;

import static main.managers.EffectManager.TYPE.*;
import static main.utils.Initializer.RANDOM;
import static main.utils.Initializer.color;

public class EffectManager {
    public enum TYPE {
        FIREWORK,
        LIGHTNING,
        TOTEM_HAUL,
        TOTEM
    }
    
    public EffectManager() {
        
    }
    
    public void killEffect(TYPE type, Location location) {
        location.add(0, 1, 0);

        World world = location.getWorld();
        switch (type) {
            case FIREWORK -> {
                final Firework firework = (Firework) world.spawnEntity(location, EntityType.FIREWORK);
                FireworkMeta meta = firework.getFireworkMeta();
                meta.setPower(2);
                meta.addEffect(FireworkEffect.builder().withColor(color[RANDOM.nextInt(color.length)]).withColor(color[RANDOM.nextInt(color.length)]).with(FireworkEffect.Type.BALL_LARGE).flicker(true).build());
                firework.setFireworkMeta(meta);
            }
            case LIGHTNING -> {
                world.strikeLightningEffect(location);
            }
            case TOTEM -> {
                world.spawnParticle(Particle.TOTEM, location, 50, 3, 1, 3, 0.0);
            }
            case TOTEM_HAUL -> {
                for (double y = 0; y < 11; y += 0.05) {
                    world.spawnParticle(Particle.TOTEM, new Location(world, (float) (location.getX() + (2 * Math.cos(y))), (float) (location.getY() + (2 * Math.sin(y))), (float) (location.getZ() + 2 * Math.sin(y))), 2, 0, 0, 0, 1.0);
                }
            }
        }
    }

    public void randomKillEffect(Location location) {
        int random = RANDOM.nextInt(5);
        killEffect(random == 0 ? FIREWORK : random == 1 ? LIGHTNING : random == 2 ? TOTEM : random == 3 ? TOTEM_HAUL : LIGHTNING, location);
    }

    public void banEffect(Player player) {
        final Location location = player.getLocation();
        final World world = location.getWorld();
        teleportEffect(location, world);

        world.strikeLightningEffect(location);
    }

    public void teleportEffect(Location location, World world) {
        double p1, p2;
        for (short index = 1; index < 16; index++) {
            p1 = (index * Math.PI) / 8;
            p2 = (index - 1) * Math.PI / 8;
            world.spawnParticle(Particle.TOTEM, location.clone().add((Math.cos(p2) * 3) - (Math.cos(p1) * 3), 0, (Math.sin(p2) * 3) - (Math.sin(p1) * 3)), 50);
        }
    }
}
