package main.utils;

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import main.utils.Instances.CustomPlayerDataHolder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Map;

import static main.utils.Initializer.*;
import static main.utils.Utils.createItemStack;
import static main.utils.Utils.getHead;

public class Gui {
    public static Map<String, Pair<Integer, String>> inInventory = new Object2ObjectOpenHashMap<>();
    private static ItemStack[] report = {};
    private static ItemStack[] killeffect = {};

    public static void init() {
        Inventory inv = Bukkit.createInventory(null, 27);
        ItemStack glass = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta meta = glass.getItemMeta();
        meta.setDisplayName("");
        glass.setItemMeta(meta);
        for (int i = 0; i < inv.getSize(); i++) {
            inv.setItem(i, glass);
        }

        inv.setItem(10, createItemStack(Material.END_CRYSTAL, "Cheating", ImmutableList.of("§aUse of a hacked client", "§aUse of an illegal client modification")));
        inv.setItem(11, createItemStack(Material.PAPER, "Doxxing", ImmutableList.of("§aSaying private info of a player in the chat")));
        inv.setItem(13, createItemStack(Material.RED_BED, "Spamming", ImmutableList.of("§aSaying more than 5 messages on the same topic")));
        inv.setItem(14, createItemStack(Material.DIAMOND_SWORD, "Interrupting", ImmutableList.of("§aInterrupting fights in flat")));
        inv.setItem(15, createItemStack(Material.RESPAWN_ANCHOR, "Anchor Spamming", ImmutableList.of("§aUsing too many anchors in flat")));
        report = inv.getContents();
        inv.clear();

        inv.setItem(12, createItemStack(Material.GUNPOWDER, "None", ImmutableList.of("§7▪ " + SECOND_COLOR + "ᴄʟɪᴄᴋ: §fʀᴇᴍᴏᴠᴇ")));
        inv.setItem(13, createItemStack(Material.BONE, "Lightning Bolt", ImmutableList.of("§7▪ " + SECOND_COLOR + "ᴄʟɪᴄᴋ: §fʙᴜʏ", "§a$150")));
        inv.setItem(14, createItemStack(Material.TNT, "Explosion", ImmutableList.of("§7▪ " + SECOND_COLOR + "ᴄʟɪᴄᴋ: §fʙᴜʏ", "§a$250")));
        inv.setItem(15, createItemStack(Material.FIREWORK_STAR, "Firework Explosion", ImmutableList.of("§7▪ " + SECOND_COLOR + "ᴄʟɪᴄᴋ: §fʙᴜʏ", "§a$450")));

        killeffect = inv.getContents();
    }

    public static void openReport(Player p, String arg) {
        inInventory.put(p.getName(), Pair.of(1, arg));
        Inventory inv = Bukkit.createInventory(p, 27, "ʀᴇᴘᴏʀᴛ");
        inv.setContents(report);
        inv.setItem(12, createItemStack(getHead(arg), "Ban Evading", ImmutableList.of("§aUsing an alt to play after being banned")));
        p.openInventory(inv);
    }

    public static void openKilleffect(Player p) {
        String name = p.getName();
        inInventory.put(name, Pair.of(0, null));
        Inventory inv = Bukkit.createInventory(p, 27, "ꜱʜᴏᴘ");
        inv.setContents(killeffect);
        inv.setItem(10, createItemStack(Material.GREEN_DYE, "§aBalance", ImmutableList.of("§a$" + playerData.get(name).getMoney())));
        p.openInventory(inv);
    }

    public static void openSettings(Player p) {
        String name = p.getName();
        inInventory.put(name, Pair.of(6, null));
        Inventory inv = Bukkit.createInventory(p, 27, "sᴇᴛᴛɪɴɢs");
        CustomPlayerDataHolder D0 = playerData.get(name);
        ItemStack glass = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta meta = glass.getItemMeta();
        meta.setDisplayName("");
        glass.setItemMeta(meta);
        for (int i = 0; i < inv.getSize(); i++) {
            inv.setItem(i, glass);
        }
        inv.setItem(10, getHead(D0.getFRank(name), name, ImmutableList.of(
                "§7ᴋɪʟʟs " + MAIN_COLOR + "» " + p.getStatistic(Statistic.PLAYER_KILLS),
                "§7ᴅᴇᴀᴛʜs " + MAIN_COLOR + "» " + p.getStatistic(Statistic.DEATHS)
        )));
        inv.setItem(12, createItemStack(D0.isFastCrystals() ? Material.LIME_STAINED_GLASS : Material.RED_STAINED_GLASS, "§7Makes your crystals go fast", ImmutableList.of("§7sᴛᴀᴛᴜs: " + (D0.isFastCrystals() ? "§aᴇɴᴀʙʟᴇᴅ" : "§cᴅɪsᴀʙʟᴇᴅ"))));
        inv.setItem(13, createItemStack(D0.getMtoggle() == 0 ? Material.RED_STAINED_GLASS : Material.LIME_STAINED_GLASS, "§7Allow messages from players", ImmutableList.of("§7sᴛᴀᴛᴜs: " + (D0.getMtoggle() == 0 ? "§aᴇɴᴀʙʟᴇᴅ" : "§cᴅɪsᴀʙʟᴇᴅ"))));
        inv.setItem(14, createItemStack(D0.getTptoggle() == 0 ? Material.RED_STAINED_GLASS : Material.LIME_STAINED_GLASS, "§7Allow teleport requests from players", ImmutableList.of("§7sᴛᴀᴛᴜs: " + (D0.getTptoggle() == 0 ? "§aᴇɴᴀʙʟᴇᴅ" : "§cᴅɪsᴀʙʟᴇᴅ"))));
        p.openInventory(inv);
    }
}