package main.utils;

import com.github.retrooper.packetevents.event.SimplePacketListenerAbstract;
import com.github.retrooper.packetevents.event.simple.PacketPlayReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClickWindow;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import static main.utils.Initializer.MAIN_COLOR;
import static main.utils.Initializer.playerData;

public class AntiAutoTotem extends SimplePacketListenerAbstract {
    void flag(Player p) {
        String alertStr = MAIN_COLOR + p.getName() + "§7 has been flagged for auto totem.";
        for (Player k : Bukkit.getOnlinePlayers()) {
            if (playerData.get(k.getName()).getRank() > 8)
                k.sendMessage(alertStr);
        }
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
                    if (offhandItem.getType() == Material.TOTEM_OF_UNDYING) {
                        offhandItem.setAmount(0);
                        flag(player);
                    }
                }, 1L);
        }
    }
}