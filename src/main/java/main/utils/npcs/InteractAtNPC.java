package main.utils.npcs;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.retrooper.packetevents.event.SimplePacketListenerAbstract;
import com.github.retrooper.packetevents.event.simple.PacketPlayReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import main.Practice;
import main.utils.Initializer;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.time.Duration;

import static main.utils.Initializer.*;
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
                p.teleportAsync(Initializer.ffa, PlayerTeleportEvent.TeleportCause.COMMAND).thenAccept(r -> inFFA.add(p));
            }
            case 1 -> {
                Player p = (Player) event.getPlayer();
                String name = p.getName();
                if (playersUsingNPC.getIfPresent(name) != null)
                    return;
                if (Initializer.bannedFromflat.contains(name)) {
                    p.sendMessage(MAIN_COLOR + "ʏᴏᴜ ᴀʀᴇ ʙᴀɴɴᴇᴅ ꜰʀᴏᴍ ᴛʜɪs ᴍᴏᴅᴇ.");
                    return;
                }
                playersUsingNPC.put(name, 0);
                p.teleportAsync(Initializer.flat, PlayerTeleportEvent.TeleportCause.COMMAND).thenAccept(r -> inFlat.add(name));
            }
            case 2 -> {
                Player p = (Player) event.getPlayer();
                String name = p.getName();
                if (playersUsingNPC.getIfPresent(name) != null)
                    return;
                playersUsingNPC.put(name, 0);
                Location locH = overworldRTP[RANDOM.nextInt(100)];
                p.teleportAsync(locH, PlayerTeleportEvent.TeleportCause.COMMAND).thenAccept(result -> {
                    playersRTPing.remove(p.getName());
                    p.playSound(p, Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
                    p.sendTitle(SECOND_COLOR + "ᴛᴇʟᴇᴘᴏʀᴛᴇᴅ", "§7" + locH.getBlockX() + " " + locH.getBlockY() + " " + locH.getBlockZ());
                    p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 1));
                    p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 60, 1));
                    teleportEffect(Practice.d, locH);
                });
            }
        }
    }
}