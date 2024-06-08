package main.utils.modules.npcs;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.retrooper.packetevents.event.SimplePacketListenerAbstract;
import com.github.retrooper.packetevents.event.simple.PacketPlayReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;
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
                final Player player = (Player) event.getPlayer();
                final String name = player.getName();
                if (playersUsingNPC.getIfPresent(name) != null)
                    return;
                playersUsingNPC.put(name, 0);
                player.teleportAsync(Initializer.ffa, PlayerTeleportEvent.TeleportCause.COMMAND).thenAccept(r -> inFFA.add(player));
            }
            case 1 -> {
                final Player player = (Player) event.getPlayer();
                final String name = player.getName();
                if (playersUsingNPC.getIfPresent(name) != null)
                    return;
                if (Initializer.bannedFromflat.contains(name)) {
                    player.sendMessage(MAIN_COLOR + "ʏᴏᴜ ᴀʀᴇ ʙᴀɴɴᴇᴅ ꜰʀᴏᴍ ᴛʜɪs ᴍᴏᴅᴇ.");
                    return;
                }
                playersUsingNPC.put(name, 0);
                player.teleportAsync(Initializer.flat, PlayerTeleportEvent.TeleportCause.COMMAND).thenAccept(r -> inFlat.add(name));
            }
            case 2 -> {
                final Player player = (Player) event.getPlayer();
                final String name = player.getName();
                if (playersUsingNPC.getIfPresent(name) != null)
                    return;
                playersUsingNPC.put(name, 0);
                final Location location = overworldRTP[RANDOM.nextInt(100)];
                player.teleportAsync(location, PlayerTeleportEvent.TeleportCause.COMMAND).thenAccept(result -> {
                    playersRTPing.remove(player.getName());
                    player.playSound(player, Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
                    player.sendTitle(SECOND_COLOR + "ᴛᴇʟᴇᴘᴏʀᴛᴇᴅ", "§7" + location.getBlockX() + " " + location.getBlockY() + " " + location.getBlockZ());
                    player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 1));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 60, 1));
                    teleportEffect(Practice.d, location);
                });
            }
        }
    }
}