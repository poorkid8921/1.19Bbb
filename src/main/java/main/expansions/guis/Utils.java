package main.expansions.guis;

import it.unimi.dsi.fastutil.Pair;
import main.utils.Initializer;
import main.utils.Instances.DuelHolder;
import main.utils.ItemCreator;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static main.expansions.duels.Utils.*;
import static main.utils.Languages.MAIN_COLOR;
import static main.utils.Languages.SECOND_COLOR;
import static main.utils.Utils.createItemStack;

public class Utils {
    public static Map<String, Pair<Integer, String>> inInventory = new HashMap<>();
    static ItemStack[] report = {};
    static ItemStack[] killeffect = {};
    static ItemStack[] duel1 = {};
    static ItemStack[] duel2 = {};
    static ItemStack[] duel3 = {};

    public static void init() {
        Inventory inv = Bukkit.createInventory(null, 27);

        ItemStack glass = new ItemStack(Material.GRAY_STAINED_GLASS_PANE, 1);
        ItemMeta meta = glass.getItemMeta();
        meta.setDisplayName("");
        glass.setItemMeta(meta);

        for (int i = 0; i < inv.getSize(); i++) {
            inv.setItem(i, glass);
        }

        inv.setItem(10, createItemStack(Material.END_CRYSTAL, "Cheating", List.of("§aUse of a hacked client",
                "§aUse of an illegal client modification")));
        inv.setItem(11, createItemStack(Material.PAPER, "Doxxing", List.of("§aSaying private info of a player in the chat")));
        inv.setItem(13, createItemStack(Material.RED_BED, "Spamming", List.of("§aSaying more than 5 messages on the same topic")));
        inv.setItem(14, createItemStack(Material.DIAMOND_SWORD, "Interrupting", List.of("§aInterrupting fights in flat")));
        inv.setItem(15, createItemStack(Material.RESPAWN_ANCHOR, "Anchor Spamming", List.of("§aUsing too many anchors in flat")));
        report = inv.getContents();

        inv.setItem(10, createItemStack(Material.BONE, "Lightning Bolt",
                List.of("§a$100",
                        "§7▪ " + SECOND_COLOR + "ᴄʟɪᴄᴋ: §fᴘᴜʀᴄʜᴀꜱᴇ")));

        inv.setItem(11, createItemStack(Material.TNT, "Explosion",
                List.of("§a$200",
                        "§7▪ " + SECOND_COLOR + "ᴄʟɪᴄᴋ: &fᴘᴜʀᴄʜᴀꜱᴇ")));

        inv.setItem(12, createItemStack(Material.FIREWORK_STAR, "Firework Explosion",
                List.of("§a$250",
                        "§7▪ " + SECOND_COLOR + "ᴄʟɪᴄᴋ: &fᴘᴜʀᴄʜᴀꜱᴇ")));

        inv.setItem(13, glass);
        inv.setItem(14, glass);
        inv.setItem(15, glass);
        killeffect = inv.getContents();

        inv = Bukkit.createInventory(null, 54);

        ItemStack i = new ItemStack(Material.RESPAWN_ANCHOR, Math.min(Duel_GetDuelsAvailableForGM(0), 1));
        meta = i.getItemMeta();
        meta.setDisplayName(MAIN_COLOR + "ꜰɪᴇʟᴅ");
        meta.setLore(List.of("1V1"));
        i.setItemMeta(meta);
        inv.setItem(10, i);

        ItemStack i2 = new ItemStack(Material.FEATHER);
        meta = i2.getItemMeta();
        meta.setDisplayName("§7Spectate");
        meta.setLore(List.of("§7Spectate others!"));
        i2.setItemMeta(meta);
        inv.setItem(53, i2);
        duel1 = inv.getContents();
    }

    public static void openDuels0(Player p) {
        inInventory.put(p.getName(), Pair.of(2, null));
        Inventory inv = Bukkit.createInventory(p, 54, "ᴅᴜᴇʟs");
        inv.setContents(duel1);
        p.openInventory(inv);
    }

    public static void openDuelsSpectate(Player p) {
        inInventory.put(p.getName(), Pair.of(2, "0"));
        Inventory inv = Bukkit.createInventory(p, 54, "ᴅᴜᴇʟs | sᴘᴇᴄᴛᴀᴛᴇ");
        inv.setContents(duel2);
        p.openInventory(inv);
    }

    public static void openDuelsKitset(Player p) {
        inInventory.put(p.getName(), Pair.of(2, "1"));
        Inventory inv = Bukkit.createInventory(p, 54, "ᴅᴜᴇʟs | ᴋɪᴛ ᴏᴠᴇʀʀɪᴅᴇ");
        inv.setContents(duel3);
        p.openInventory(inv);
    }

    public static void openKilleffect(Player p) {
        inInventory.put(p.getName(), Pair.of(0, null));
        Inventory inv = Bukkit.createInventory(p, 27, "sᴇᴛᴛɪɴɢs | ᴋɪʟʟ ᴇꜰꜰᴇᴄᴛꜱ");
        inv.setContents(killeffect);
        p.openInventory(inv);
    }

    public static void openReport(Player p, String arg) {
        inInventory.put(p.getName(), Pair.of(1, arg));
        Inventory inv = Bukkit.createInventory(p, 27, "ʀᴇᴘᴏʀᴛ");
        inv.setContents(report);
        inv.setItem(12, createItemStack(ItemCreator.getHead(arg), "Ban Evading", List.of("§aUsing an alt to play after being banned")));
        p.openInventory(inv);
    }

    public static void updateSpectate() {
        Inventory sp = Bukkit.createInventory(null, 54);
        int added = 9;
        for (DuelHolder r : Initializer.duel.stream().filter(r -> r.getRounds() > 0).toList()) {
            if (added++ == 44) break;
            ItemStack i = new ItemStack(Duel_Formatted_Type_Material(r.getType()));
            ItemMeta im = i.getItemMeta();
            im.setLore(List.of(getLengthofDuel(r.getMaxPlayers()), MAIN_COLOR + r.getSender().getName() + " §7ᴀɢᴀɪɴsᴛ " + MAIN_COLOR + r.getReceiver().getName()));
            im.getPersistentDataContainer().set(spectateHead, PersistentDataType.STRING, r.getSender().getName());
            i.setItemMeta(im);
            sp.setItem(added, i);
        }

        ItemStack[] s = sp.getContents();
        inInventory.entrySet().stream().filter(r -> r.getValue().second().equals("0")).forEach(r -> Bukkit.getPlayer(r.getKey()).getInventory().setContents(s));
        duel2 = s;
    }

    public static void updateDuels() {
        Inventory sp = Bukkit.createInventory(null, 54);
        sp.setContents(duel1);
        ItemStack i = sp.getItem(10);
        i.setAmount(Math.min(Duel_GetDuelsAvailableForGM(0), 1));
        sp.setItem(10, i);
        ItemStack[] s = sp.getContents();
        inInventory.entrySet().stream().filter(r -> r.getValue().second() == null).forEach(r -> Bukkit.getPlayer(r.getKey()).getInventory().setContents(s));
    }
}
