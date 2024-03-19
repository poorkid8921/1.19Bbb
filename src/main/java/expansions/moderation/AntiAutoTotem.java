package expansions.moderation;

import com.github.retrooper.packetevents.event.SimplePacketListenerAbstract;
import com.github.retrooper.packetevents.event.simple.PacketPlayReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClickWindow;
import main.utils.Constants;
import main.utils.Instances.CustomPlayerDataHolder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import static main.utils.Constants.MAIN_COLOR;
import static main.utils.Constants.playerData;

public class AntiAutoTotem extends SimplePacketListenerAbstract {
    void flag(Player p) {
        String pn = p.getName();
        Bukkit.getOnlinePlayers().stream().filter(result -> result.hasPermission("has.staff")).forEach(result -> result.sendMessage(MAIN_COLOR + pn + "§7 has been flagged for auto totem."));
        Bukkit.getLogger().warning("AntiCheat: " + pn + " has been flagged.");
    }

    @Override
    public void onPacketPlayReceive(PacketPlayReceiveEvent event) {
        if (event.getPacketType() != PacketType.Play.Client.CLICK_WINDOW) return;
        Player player = (Player) event.getPlayer();
        WrapperPlayClientClickWindow packet = new WrapperPlayClientClickWindow(event);
        ItemStack clickedItem = player.getInventory().getItem(packet.getSlot());
        if (clickedItem != null &&
                clickedItem.getType() == Material.TOTEM_OF_UNDYING) {
            if (packet.getWindowClickType() == WrapperPlayClientClickWindow.WindowClickType.PICKUP) {
                PlayerInventory inv = player.getInventory();
                if (inv.getItemInOffHand().getType() == Material.AIR)
                    Bukkit.getScheduler().runTaskLater(Constants.p, () -> {
                        ItemStack offhandItem = inv.getItemInOffHand();
                        if (offhandItem.getType() == Material.TOTEM_OF_UNDYING) {
                            offhandItem.setAmount(0);
                            flag(player);
                        }
                    }, 1L);
            }
            else if (packet.getWindowClickType() == WrapperPlayClientClickWindow.WindowClickType.SWAP) {
                CustomPlayerDataHolder D = playerData.get(player.getName());
                if (D.windowOpenTime().size() < 2)
                    return;

                if (D.windowQuitTime().size() < 2)
                    return;

                int quitSize = D.windowQuitTime().size();
                long avgQuit = 0;
                for (int i = 0; i < quitSize; i++) {
                    avgQuit += D.windowQuitTime().get(i);
                }
                avgQuit/=quitSize;
                Bukkit.getLogger().warning("quit: " + avgQuit);

                int openSize = D.windowOpenTime().size();
                long avgOpen = 0;
                for (int i = 0; i < openSize; i++) {
                    avgOpen += D.windowOpenTime().get(i);
                }
                avgOpen/=openSize;
                Bukkit.getLogger().warning("open: " + avgOpen);
                long avg;
                if (openSize > quitSize) {
                    avg = avgOpen-avgQuit;
                } else
                    avg = avgQuit-avgOpen;
                Bukkit.getLogger().warning("avg: " + avg);
            }
        }
    }
}