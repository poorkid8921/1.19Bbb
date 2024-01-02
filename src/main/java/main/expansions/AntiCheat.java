package main.expansions;

import com.github.retrooper.packetevents.event.SimplePacketListenerAbstract;
import com.github.retrooper.packetevents.event.simple.PacketPlayReceiveEvent;
import com.github.retrooper.packetevents.event.simple.PacketPlaySendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClickWindow;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerPosition;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityVelocity;
import com.google.common.collect.ImmutableList;
import main.utils.Constants;
import main.utils.Instances.CustomPlayerDataHolder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import static main.utils.Constants.MAIN_COLOR;
import static main.utils.Constants.playerData;

public class AntiCheat extends SimplePacketListenerAbstract {
    void flag(Player p,
              String reason) {
        Bukkit.getOnlinePlayers().stream().filter(result -> result.hasPermission("has.staff")).forEach(result -> result.sendMessage(MAIN_COLOR + p.getDisplayName() + " §7has been flagged for " + MAIN_COLOR + reason));
        Bukkit.getLogger().warning("AntiCheat: " + p.getName() + " has been flagged for " + reason);
    }

    @Override
    public void onPacketPlayReceive(PacketPlayReceiveEvent event) {
        if (event.getPacketType() != PacketType.Play.Client.CLICK_WINDOW) return;

        Player player = (Player) event.getPlayer();
        WrapperPlayClientClickWindow packet = new WrapperPlayClientClickWindow(event);
        ItemStack clickedItem = player.getOpenInventory().getItem(packet.getSlot());
        if (clickedItem != null &&
                clickedItem.getType() == Material.TOTEM_OF_UNDYING &&
                packet.getWindowClickType() == WrapperPlayClientClickWindow.WindowClickType.PICKUP) {
            PlayerInventory inv = player.getInventory();
            if (inv.getItemInOffHand().getType() == Material.AIR)
                Bukkit.getScheduler().runTaskLater(Constants.p, () -> {
                    ItemStack offhandItem = inv.getItemInOffHand();
                    if (offhandItem.getType() == Material.TOTEM_OF_UNDYING) {
                        offhandItem.setAmount(0);
                        flag(player, "Auto Totem");
                    }
                }, 2L);
        }
    }
}