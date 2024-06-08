package main.utils;

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Map;

import static main.utils.Initializer.SECOND_COLOR;
import static main.utils.Initializer.playerData;
import static main.utils.Utils.createItemStack;
import static main.utils.Utils.getHead;

public class Gui {
    public static final Map<String, Pair<Integer, String>> inInventory = new Object2ObjectOpenHashMap<>();
    private static ItemStack[] report = {};
    private static ItemStack[] killeffect = {};

    public static void init() {
        final Inventory inventory = Bukkit.createInventory(null, 27);
        final ItemStack glass = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        final ItemMeta meta = glass.getItemMeta();
        meta.setDisplayName("");
        glass.setItemMeta(meta);
        for (int i = 0; i < inventory.getSize(); i++) {
            inventory.setItem(i, glass);
        }

        inventory.setItem(10, createItemStack(Material.END_CRYSTAL, "Cheating", ImmutableList.of("§aUse of a hacked client", "§aUse of an illegal client modification")));
        inventory.setItem(11, createItemStack(Material.PAPER, "Doxxing", ImmutableList.of("§aSaying private info of a player in the chat")));
        inventory.setItem(13, createItemStack(Material.RED_BED, "Spamming", ImmutableList.of("§aSaying more than 5 messages on the same topic")));
        inventory.setItem(14, createItemStack(Material.DIAMOND_SWORD, "Interrupting", ImmutableList.of("§aInterrupting fights in flat")));
        inventory.setItem(15, createItemStack(Material.RESPAWN_ANCHOR, "Anchor Spamming", ImmutableList.of("§aUsing too many anchors in flat")));
        report = inventory.getContents();
        inventory.clear();

        inventory.setItem(12, createItemStack(Material.GUNPOWDER, "None", ImmutableList.of("§7▪ " + SECOND_COLOR + "ᴄʟɪᴄᴋ: §fʀᴇᴍᴏᴠᴇ")));
        inventory.setItem(13, createItemStack(Material.BONE, "Lightning Bolt", ImmutableList.of("§7▪ " + SECOND_COLOR + "ᴄʟɪᴄᴋ: §fʙᴜʏ", "§a$150")));
        inventory.setItem(14, createItemStack(Material.TNT, "Explosion", ImmutableList.of("§7▪ " + SECOND_COLOR + "ᴄʟɪᴄᴋ: §fʙᴜʏ", "§a$250")));
        inventory.setItem(15, createItemStack(Material.FIREWORK_STAR, "Firework Explosion", ImmutableList.of("§7▪ " + SECOND_COLOR + "ᴄʟɪᴄᴋ: §fʙᴜʏ", "§a$450")));

        killeffect = inventory.getContents();
    }

    public static void openReport(Player player, String arg) {
        inInventory.put(player.getName(), Pair.of(1, arg));
        final Inventory inventory = Bukkit.createInventory(player, 27, "ʀᴇᴘᴏʀᴛ");
        inventory.setContents(report);
        inventory.setItem(12, createItemStack(getHead(arg), "Ban Evading", ImmutableList.of("§aUsing an alt to play after being banned")));
        player.openInventory(inventory);
    }

    public static void openKilleffect(Player player) {
        final String name = player.getName();
        inInventory.put(name, Pair.of(0, null));
        final Inventory inventory = Bukkit.createInventory(player, 27, "ꜱʜᴏᴘ");
        inventory.setContents(killeffect);
        inventory.setItem(10, createItemStack(Material.GREEN_DYE, "§aBalance", ImmutableList.of("§a$" + playerData.get(name).getMoney())));
        player.openInventory(inventory);
    }
}