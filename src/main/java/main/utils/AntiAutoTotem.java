package main.utils;

import com.github.retrooper.packetevents.event.SimplePacketListenerAbstract;
import com.github.retrooper.packetevents.event.simple.PacketPlayReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClickWindow;
import main.utils.Instances.CustomPlayerDataHolder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

import static main.utils.Initializer.*;
import static main.utils.storage.DB.connection;

public class AntiAutoTotem extends SimplePacketListenerAbstract {
    private void createModeration(Player p, String name) {
        p.kickPlayer(EXPLOITING_KICK);
        try (PreparedStatement statement = connection.prepareStatement("INSERT INTO bans (ip, bantime, banner) VALUES (?, ?, ?)")) {
            statement.setBytes(1, p.getAddress().getAddress().getAddress());
            statement.setTimestamp(2, new Timestamp(System.currentTimeMillis() + 432000000L));
            statement.setString(3, "Anticheat");
            statement.executeUpdate();
        } catch (SQLException ignored) {
        }
        Bukkit.getScheduler().runTaskAsynchronously(Initializer.p, () -> {
            try {
                final HttpsURLConnection connection = (HttpsURLConnection) CACHED_MODERATION_WEBHOOK.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; U; Linux i686) Gecko/20071127 Firefox/2.0.0.11");
                connection.setDoOutput(true);
                try (final OutputStream outputStream = connection.getOutputStream()) {
                    outputStream.write(("{\"tts\":false,\"username\":\"Moderation\",\"avatar_url\":\"https://mc-heads.net/avatar/" + name + "/100\",\"embeds\":[{\"color\":16762880,\"fields\":[{\"value\":\"Economy\",\"name\":\"Server\",\"inline\":true},{\"value\":\"" + name + "\",\"name\":\"Name\",\"inline\":true},{\"value\":\"Auto Totem\",\"name\":\"Reason\",\"inline\":true}],\"title\":\"Moderation\",\"thumbnail\":{\"url\":\"https://mc-heads.net/avatar/" + name + "/100\"}}]}").getBytes(StandardCharsets.UTF_8));
                }
                connection.getInputStream();
            } catch (IOException ignored) {
            }
        });
    }

    private boolean flag(Player p) {
        String name = p.getName();
        CustomPlayerDataHolder D0 = playerData.get(name);
        D0.incrementFlags();
        if (D0.getFlags() == 9) {
            D0.setFlags(0);
            createModeration(p, name);
            Bukkit.broadcastMessage(MAIN_COLOR + name + " §7has been punished for " + MAIN_COLOR + "Auto Totem");
        } else {
            Bukkit.broadcastMessage(MAIN_COLOR + name + " §7has been flagged for " + MAIN_COLOR + "Auto Totem");
            return true;
        }
        return false;
    }

    @Override
    public void onPacketPlayReceive(PacketPlayReceiveEvent event) {
        if (event.getPacketType() != PacketType.Play.Client.CLICK_WINDOW) return;
        Player player = (Player) event.getPlayer();
        WrapperPlayClientClickWindow packet = new WrapperPlayClientClickWindow(event);
        ItemStack clickedItem = player.getInventory().getItem(packet.getSlot());
        if (packet.getWindowClickType() == WrapperPlayClientClickWindow.WindowClickType.PICKUP &&
                clickedItem != null &&
                clickedItem.getType() == Material.TOTEM_OF_UNDYING) {
            PlayerInventory inv = player.getInventory();
            if (inv.getItemInOffHand().getType() == Material.AIR)
                Bukkit.getScheduler().runTaskLater(Initializer.p, () -> {
                    ItemStack offhandItem = inv.getItemInOffHand();
                    if (offhandItem.getType() == Material.TOTEM_OF_UNDYING && flag(player))
                        offhandItem.setAmount(0);
                }, 1L);
        }
    }
}