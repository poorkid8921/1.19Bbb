package main.expansions.guis;

import it.unimi.dsi.fastutil.Pair;
import main.Practice;
import main.utils.Initializer;
import main.utils.Instances.DuelHolder;
import main.utils.ItemCreator;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

import static main.expansions.duels.Utils.*;
import static main.utils.Languages.MAIN_COLOR;
import static main.utils.Languages.SECOND_COLOR;
import static main.utils.Utils.createItemStack;

public class Utils {
    public static Map<String, Pair<Integer, String>> inInventory = new HashMap<>();
    public static NamespacedKey itemKey = new NamespacedKey(Initializer.p, "key");
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

        List<String> s = List.of("§7▪ " + SECOND_COLOR + "ᴄʟɪᴄᴋ: §fsᴇᴛ");
        inv.setItem(10, createItemStack(Material.BARRIER, "None",
                s));

        inv.setItem(12, createItemStack(Material.BONE, "Lightning Bolt",
                s));

        inv.setItem(13, createItemStack(Material.TNT, "Explosion",
                s));

        inv.setItem(14, createItemStack(Material.FIREWORK_STAR, "Firework Explosion",
                s));

        inv.setItem(11, glass);
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

        inv.clear();
    }

    public static void openPublicKits(Player p,
                                      int pg) {
        inInventory.put(p.getName(), Pair.of(3, "-"));
        List<ItemStack> allItems = new ArrayList<>();
        for (Map.Entry<String, Map<String, Object>> entry : Practice.kitMap.entrySet()) {
            Map<String, Object> value = entry.getValue();
            if (value.containsKey("public")) {
                String name = value.containsKey("name") ? value.get("name").toString() : "Kit";

                String key = entry.getKey();
                ItemStack head = ItemCreator.getItem("§b§l" + name,
                        Material.CHEST,
                        List.of("§dby " + value.get("player")));
                ItemMeta meta = head.getItemMeta();
                meta.getPersistentDataContainer().set(itemKey, PersistentDataType.STRING, key);
                head.setItemMeta(meta);
                allItems.add(head);
            }
        }
        int numPages = (int) Math.ceil(allItems.size() / 28.0);
        int endIndex = pg * 28;
        if (pg == numPages) {
            ItemStack filler = ItemCreator.getItem(" ", Material.CRIMSON_BUTTON);
            while (allItems.size() < numPages * 28) {
                allItems.add(filler);
            }
        }
        ItemStack[] page = Arrays.copyOfRange(allItems.toArray(new ItemStack[28]),
                endIndex - 28,
                endIndex);
        Inventory inv = Bukkit.createInventory(p,
                54,
                "§5§lPublic Kits");
        inv.setItem(49, ItemCreator.getItem("§cEXIT", Material.OAK_DOOR, null));
        if (pg > 1) {
            inv.setItem(48, ItemCreator.getHead("§a← Back", "MHF_ArrowLeft", null));
        }
        if (pg < numPages) {
            inv.setItem(50, ItemCreator.getHead("§aNext →", "MHF_ArrowRight", null));
        }
        ItemStack glass = ItemCreator.getItem(" ", Material.PURPLE_STAINED_GLASS_PANE);
        for (int i = 0; i <= 8; ++i) {
            inv.setItem(i, glass);
        }
        for (int i = 45; i <= 53; ++i) {
            if (inv.getItem(i) == null) {
                inv.setItem(i, glass);
            }
        }
        for (int i = 9; i <= 36; i += 9) {
            inv.setItem(i, glass);
        }
        for (int i = 17; i <= 44; i += 9) {
            inv.setItem(i, glass);
        }
        for (ItemStack item : page) {
            inv.setItem(inv.firstEmpty(), item);
        }

        p.openInventory(inv);
    }

    public static void openKitRoom(Player p) {
        inInventory.put(p.getName(), Pair.of(3, "0"));
        Inventory inv = Bukkit.createInventory(p,
                54,
                "§5§lVirtual Kit Room");
        inv.setContents(Practice.kitRoomMap.get(1));

        ItemStack item = ItemCreator.getItem("§cEXIT", Material.OAK_DOOR);
        inv.setItem(45, item);
        item = ItemCreator.getItem("§a§lREFILL", Material.STRUCTURE_VOID);
        inv.setItem(53, item);
        ItemStack[] buttons = {ItemCreator.getItem("§aArmory", Material.NETHERITE_SWORD, null),
                ItemCreator.getItem("§aPotions", Material.SPLASH_POTION, null),
                ItemCreator.getItem("§aConsumables", Material.ENDER_PEARL, null),
                ItemCreator.getItem("§aArrows", Material.TIPPED_ARROW, null),
                ItemCreator.getItem("§aExplosives", Material.RESPAWN_ANCHOR, null)};
        int i = 46;
        for (ItemStack button : buttons) {
            ItemMeta meta = button.getItemMeta();
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
            button.setItemMeta(meta);
            inv.setItem(i++, button);
        }
        item = inv.getItem(47);
        ItemMeta meta2 = item.getItemMeta();
        meta2.addEnchant(Enchantment.DURABILITY, 1, true);
        item.setItemMeta(meta2);
        item = inv.getItem(50);
        PotionMeta potMeta = (PotionMeta) item.getItemMeta();
        potMeta.setColor(Color.WHITE);
        item.setItemMeta(potMeta);
        item = ItemCreator.getItem(" ", Material.BLACK_STAINED_GLASS_PANE);
        for (int j = 45; j <= 53; ++j) {
            if (inv.getItem(j) == null) {
                inv.setItem(j, item);
            }
        }

        p.openInventory(inv);
    }

    public static void openKitMenu(Player p) {
        String pn = p.getName();
        inInventory.put(pn, Pair.of(3, "0"));
        Inventory inv = Bukkit.createInventory(p, 54, "§9§b" + pn + "'s Kits");
        List<String> lore = new ArrayList<>();
        for (int i = 1; i <= 3; ++i) {
            lore.addAll(List.of("§dto edit:",
                    "§d• right click this chest",
                    "§7to load:",
                    "§7• /k" + i,
                    "§7• /kit" + i));
            String name = "§b§lKit " + i;
            String key = pn + "-" + i;
            if (Practice.kitMap.get(key).containsKey("name")) {
                name += ": §b§l" + Practice.kitMap.get(key).get("name");
                lore.add("§7• /kit " + Practice.kitMap.get(key).get("name"));
            }
            ItemStack item = ItemCreator.getItem(name, Material.CHEST, lore);
            inv.setItem(i + 9, item);
            lore.clear();
        }
        lore.add("§7COMING SOON");
        for (int i = 1; i <= 3; ++i) {
            ItemStack item2 = ItemCreator.getItem("§7§lEnder Chest " + i, Material.ENDER_CHEST, lore);
            inv.setItem(i + 18, item2);
        }
        lore.clear();
        ItemStack item3 = ItemCreator.getItem("§7§lLOCKED", Material.CHEST);
        inv.setItem(13, item3);
        inv.setItem(14, item3);
        inv.setItem(15, item3);
        inv.setItem(16, item3);
        item3 = ItemCreator.getItem("§7§lLOCKED", Material.ENDER_CHEST);
        inv.setItem(22, item3);
        inv.setItem(23, item3);
        inv.setItem(24, item3);
        inv.setItem(25, item3);
        item3 = ItemCreator.getItem("§7§lUNLOCK MORE KIT SLOTS (COMING SOON)", Material.EMERALD);
        inv.setItem(37, item3);
        item3 = ItemCreator.getItem("§a§lVIRTUAL KIT ROOM", Material.NETHER_STAR);
        inv.setItem(38, item3);
        item3 = ItemCreator.getItem("§7§lINFO (COMING SOON)", Material.OAK_SIGN);
        inv.setItem(40, item3);
        item3 = ItemCreator.getItem("§7§lPREMADE KITS (COMING SOON)", Material.END_CRYSTAL);
        inv.setItem(42, item3);
        item3 = ItemCreator.getHead("§a§lPUBLIC KITS",
                "Kevos",
                null);
        inv.setItem(43, item3);
        item3 = ItemCreator.getItem(" ", Material.LIGHT_BLUE_STAINED_GLASS_PANE);
        for (int j = 28; j <= 34; ++j) {
            inv.setItem(j, item3);
        }
        item3 = ItemCreator.getItem(" ", Material.BLACK_STAINED_GLASS_PANE);
        inv.setItem(39, item3);
        inv.setItem(41, item3);
        item3 = ItemCreator.getItem(" ", Material.BLUE_STAINED_GLASS_PANE);
        for (int j = 0; j < inv.getContents().length; ++j) {
            if (inv.getItem(j) == null)
                inv.setItem(j, item3);
        }

        p.openInventory(inv);
    }

    public static void openKitEditor(Player p, int i) {
        inInventory.put(p.getName(), Pair.of(3, null));
        String key = p.getName() + "-" + i;
        String n = "Kit " + i + (Practice.kitMap.get(key).containsKey("name") ?
                ": " + Practice.kitMap : "");
        Inventory inv = Bukkit.createInventory(p, 54, "§5§l" + n);

        try {
            inv.setContents((ItemStack[]) Practice.kitMap.get(key).get("items"));
        } catch (Exception ignored) {
        }

        ItemStack item = ItemCreator.getItem("§7§l← ARMOR + OFFHAND",
                Material.BLACK_STAINED_GLASS_PANE,
                List.of("§7Order does not matter!"));
        inv.setItem(41, item);
        inv.setItem(42, item);
        inv.setItem(43, item);
        inv.setItem(44, item);
        item = ItemCreator.getItem("§cEXIT", Material.OAK_DOOR);
        inv.setItem(45, item);
        item = ItemCreator.getItem(" ", Material.BLACK_STAINED_GLASS_PANE);
        inv.setItem(46, item);
        item = ItemCreator.getItem("§a§lIMPORT CURRENT INVENTORY", Material.CHEST);
        inv.setItem(47, item);
        item = ItemCreator.getItem("§7§lCOPY (COMING SOON)", Material.CYAN_TERRACOTTA);
        inv.setItem(48, item);
        item = ItemCreator.getItem("§7§lPASTE (COMING SOON)", Material.CYAN_TERRACOTTA);
        inv.setItem(49, item);
        item = ItemCreator.getItem("§a§lCLEAR", Material.STRUCTURE_VOID);
        inv.setItem(50, item);
        item = ItemCreator.getItem("§a§l" +
                (Practice.kitMap.get(key).containsKey("name") ?
                        "REMOVE NAME" :
                        "RENAME"), Material.NAME_TAG);
        inv.setItem(51, item);
        item = ItemCreator.getItem("§7§lSHARE (COMING SOON)", Material.CYAN_TERRACOTTA);
        inv.setItem(52, item);
        inv.setItem(53, Practice.kitMap.get(key).containsKey("public") ?
                ItemCreator.getItem("§a§lMAKE PRIVATE", Material.FIREWORK_STAR) :
                ItemCreator.getHead("§a§lMAKE PUBLIC", "Kevos", null));

        p.openInventory(inv);
    }

    public static void openDuels0(Player p) {
        inInventory.put(p.getName(), Pair.of(2, "-"));
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

    public static void openReport(Player p, String arg) {
        inInventory.put(p.getName(), Pair.of(1, arg));
        Inventory inv = Bukkit.createInventory(p, 27, "ʀᴇᴘᴏʀᴛ");
        inv.setContents(report);
        inv.setItem(12, createItemStack(ItemCreator.getHead(arg), "Ban Evading", List.of("§aUsing an alt to play after being banned")));
        p.openInventory(inv);
    }

    public static void openKilleffect(Player p) {
        inInventory.put(p.getName(), Pair.of(0, null));
        Inventory inv = Bukkit.createInventory(p, 27, "sᴇᴛᴛɪɴɢs | ᴋɪʟʟ ᴇꜰꜰᴇᴄᴛꜱ");
        inv.setContents(killeffect);
        p.openInventory(inv);
    }

    public static void updateSpectate() {
        Inventory sp = Bukkit.createInventory(null, 54);
        int added = 9;
        for (DuelHolder r : Initializer.duel.stream().filter(r -> r.getRounds() > 0).toList()) {
            if (added++ >= 44) break;
            ItemStack i = new ItemStack(Duel_Formatted_Type_Material(r.getType()));
            ItemMeta im = i.getItemMeta();
            im.setLore(List.of(getLengthofDuel(r.getMaxPlayers()), MAIN_COLOR + r.getSender().getName() + " §7ᴀɢᴀɪɴsᴛ " + MAIN_COLOR + r.getReceiver()));
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
