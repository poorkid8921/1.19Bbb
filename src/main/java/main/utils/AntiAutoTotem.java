package main.utils;

import com.github.retrooper.packetevents.event.SimplePacketListenerAbstract;
import com.github.retrooper.packetevents.event.simple.PacketPlayReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClickWindow;
import main.utils.instances.CustomPlayerDataHolder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import static main.utils.Initializer.MAIN_COLOR;
import static main.utils.Initializer.playerData;

public class AntiAutoTotem extends SimplePacketListenerAbstract {
    private boolean flag(Player p) {
        String pn = p.getName();
        CustomPlayerDataHolder D0 = playerData.get(pn);
        D0.incrementFlags();
        if (D0.getFlags() == 3) {
            D0.setFlags(0);
            Bukkit.broadcastMessage(MAIN_COLOR + pn + " §7has been flagged for Auto Totem");
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