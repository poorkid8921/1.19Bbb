package main.utils.npcs;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.retrooper.packetevents.event.SimplePacketListenerAbstract;
import com.github.retrooper.packetevents.event.simple.PacketPlayReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;
import main.utils.instances.CustomPlayerDataHolder;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.time.Duration;

import static main.Economy.d;
import static main.utils.Initializer.*;
import static main.utils.Utils.getTime;
import static main.utils.Utils.teleportEffect;

public class InteractAtNPC extends SimplePacketListenerAbstract {
    private final Cache<String, Integer> playersUsingNPC = Caffeine.newBuilder().expireAfterWrite(Duration.ofMillis(1000L)).build();

    @Override
    public void onPacketPlayReceive(PacketPlayReceiveEvent event) {
        if (event.getPacketType() != PacketType.Play.Client.INTERACT_ENTITY) return;
        switch (new WrapperPlayClientInteractEntity(event).getEntityId()) {
            case 0 -> {
                Player p = (Player) event.getPlayer();
                String name = p.getName();
                if (playersUsingNPC.getIfPresent(name) != null)
                    return;
                playersUsingNPC.put(name, 0);
                p.sendMessage(D_USING, D_LINK);
            }
            case 1 -> {
                Player p = (Player) event.getPlayer();
                String name = p.getName();
                if (playersUsingNPC.getIfPresent(name) != null)
                    return;
                playersUsingNPC.put(name, 0);
                p.performCommand("ah");
            }
            case 2 -> {
                Player p = (Player) event.getPlayer();
                String name = p.getName();
                if (playersUsingNPC.getIfPresent(name) != null)
                    return;
                playersUsingNPC.put(name, 0);
                CustomPlayerDataHolder D0 = playerData.get(name);
                if (D0.getLastRTPed() > System.currentTimeMillis()) {
                    p.sendMessage("§7You are on a cooldown of " + SECOND_COLOR + getTime(D0.getLastRTPed() - System.currentTimeMillis()) + "!");
                    return;
                }
                D0.setLastRTPed(System.currentTimeMillis() + 180000L);

                Location locC = overworldRTP[RANDOM.nextInt(100)];
                p.teleportAsync(locC, PlayerTeleportEvent.TeleportCause.COMMAND).thenAccept(result -> {
                    p.playSound(p, Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
                    p.sendTitle(SECOND_COLOR + "ᴛᴇʟᴇᴘᴏʀᴛᴇᴅ", "§7" + locC.getBlockX() + " " + locC.getBlockY() + " " + locC.getBlockZ());
                    p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 1));
                    p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 60, 1));
                    teleportEffect(d, locC);
                });
            }
        }
    }
}