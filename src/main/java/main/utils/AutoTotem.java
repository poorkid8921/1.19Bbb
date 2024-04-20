package main.utils;

import com.github.retrooper.packetevents.event.SimplePacketListenerAbstract;
import com.github.retrooper.packetevents.event.simple.PacketPlayReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClickWindow;
import jdk.jshell.execution.Util;
import main.utils.instances.CustomPlayerDataHolder;
import org.bukkit.*;
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
import static main.utils.Utils.banEffect;
import static main.utils.storage.DB.connection;

public class AutoTotem extends SimplePacketListenerAbstract {
    public static void tryBanning(Player player, String name, CustomPlayerDataHolder D0) {
        if (!name.startsWith(".") && player.getPing() < 300 && D0.incrementFlags() == 3) {
            banEffect(player);
            player.kickPlayer(EXPLOITING_KICK);
            byte[] ip = player.getAddress().getAddress().getAddress();
            try (PreparedStatement statement = connection.prepareStatement("INSERT INTO bans (ip, bantime, banner) VALUES (?, ?, ?)")) {
                statement.setBytes(1, ip);
                statement.setTimestamp(2, new Timestamp(System.currentTimeMillis() + 432000000L));
                statement.setString(3, "Anticheat");
                statement.executeUpdate();
            } catch (SQLException ignored) {
            }
            Utils.sendWebhook(
                    "{\"tts\":false,\"username\":\"Moderation\",\"avatar_url\":\"https://mc-heads.net/avatar/" + name + "/100\",\"embeds\":[{\"color\":16762880,\"fields\":[{\"value\":\"Economy\",\"name\":\"Server\",\"inline\":true},{\"value\":\"" + name + "\",\"name\":\"Name\",\"inline\":true},{\"value\":\"Auto Totem A\",\"name\":\"Reason\",\"inline\":true}],\"title\":\"Moderation\",\"thumbnail\":{\"url\":\"https://mc-heads.net/avatar/" + name + "/100\"}}]}",
                    CACHED_MODERATION_WEBHOOK
            );
            Bukkit.broadcastMessage(SECOND_COLOR + name + " §7has been punished for " + SECOND_COLOR + "Auto Totem");
        }
    }

    public static boolean tryBanningSync(Player player, String name, CustomPlayerDataHolder D0) {
        if (!name.startsWith(".") && player.getPing() < 300 && D0.incrementFlags() == 5) {
            Bukkit.getScheduler().runTask(p, () -> {
                banEffect(player);
                player.kickPlayer(EXPLOITING_KICK);
                byte[] ip = player.getAddress().getAddress().getAddress();
                try (PreparedStatement statement = connection.prepareStatement("INSERT INTO bans (ip, bantime, banner) VALUES (?, ?, ?)")) {
                    statement.setBytes(1, ip);
                    statement.setTimestamp(2, new Timestamp(System.currentTimeMillis() + 2592000000L));
                    statement.setString(3, "Anticheat");
                    statement.executeUpdate();
                } catch (SQLException ignored) {
                }
                Utils.sendWebhook(
                        "{\"tts\":false,\"username\":\"Moderation\",\"avatar_url\":\"https://mc-heads.net/avatar/" + name + "/100\",\"embeds\":[{\"color\":16762880,\"fields\":[{\"value\":\"Economy\",\"name\":\"Server\",\"inline\":true},{\"value\":\"" + name + "\",\"name\":\"Name\",\"inline\":true},{\"value\":\"Auto Totem B\",\"name\":\"Reason\",\"inline\":true}],\"title\":\"Moderation\",\"thumbnail\":{\"url\":\"https://mc-heads.net/avatar/" + name + "/100\"}}]}",
                        CACHED_MODERATION_WEBHOOK
                );
                Bukkit.broadcastMessage(SECOND_COLOR + name + " §7has been punished for " + SECOND_COLOR + "Auto Totem");
            });
            return true;
        }
        return false;
    }

    @Override
    public void onPacketPlayReceive(PacketPlayReceiveEvent event) {
        if (event.getPacketType() != PacketType.Play.Client.CLICK_WINDOW)
            return;
        Player player = (Player) event.getPlayer();
        WrapperPlayClientClickWindow packet = new WrapperPlayClientClickWindow(event);
        ItemStack clickedItem = player.getInventory().getItem(packet.getSlot());
        if (clickedItem == null ||
                clickedItem.getType() != Material.TOTEM_OF_UNDYING ||
                packet.getWindowClickType() != WrapperPlayClientClickWindow.WindowClickType.PICKUP)
            return;
        PlayerInventory inv = player.getInventory();
        if (inv.getItemInOffHand().getType() == Material.AIR)
            Bukkit.getScheduler().runTaskLater(Initializer.p, () -> {
                if (inv.getItemInOffHand().getType() == Material.TOTEM_OF_UNDYING) {
                    String name = player.getName();
                    tryBanning(player, name, playerData.get(name));
                }
            }, 1L);
    }
}