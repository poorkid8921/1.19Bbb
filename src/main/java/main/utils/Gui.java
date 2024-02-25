package main.utils;

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static main.utils.Initializer.SECOND_COLOR;
import static main.utils.Initializer.playerData;
import static main.utils.Utils.createItemStack;
import static main.utils.Utils.getHead;

public class Gui {
    public static Map<String, Pair<Integer, String>> inInventory = new Object2ObjectOpenHashMap<>();
    static ItemStack[] report = {};
    static ItemStack[] killeffect = {};
    static ItemStack[] kitEditor = {};
    static ItemStack[] kitMenu = {};
    static ItemStack[] kitRoom = {};
    static ItemStack[] publicKits = {};

    public static void init() {
        Inventory inv = Bukkit.createInventory(null, 27);
        ItemStack glass = new ItemStack(Material.GRAY_STAINED_GLASS_PANE, 1);
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

        inv.setItem(12, createItemStack(Material.GUNPOWDER, "None", ImmutableList.of("§7▪ " + SECOND_COLOR + "ᴄʟɪᴄᴋ: §fʀᴇᴍᴏᴠᴇ")));
        inv.setItem(13, createItemStack(Material.BONE, "Lightning Bolt", ImmutableList.of("§7▪ " + SECOND_COLOR + "ᴄʟɪᴄᴋ: §fʙᴜʏ", "§a$250")));
        inv.setItem(14, createItemStack(Material.TNT, "Explosion", ImmutableList.of("§7▪ " + SECOND_COLOR + "ᴄʟɪᴄᴋ: §fʙᴜʏ", "§a$425")));
        inv.setItem(15, createItemStack(Material.FIREWORK_STAR, "Firework Explosion", ImmutableList.of("§7▪ " + SECOND_COLOR + "ᴄʟɪᴄᴋ: §fʙᴜʏ", "§a$750")));

        inv.setItem(11, glass);
        inv.setItem(15, glass);
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
        String pn = p.getName();
        inInventory.put(pn, Pair.of(0, null));
        Inventory inv = Bukkit.createInventory(p, 27, "ꜱʜᴏᴘ | ᴋɪʟʟ ᴇꜰꜰᴇᴄᴛꜱ");
        inv.setContents(killeffect);
        inv.setItem(10, createItemStack(Material.GREEN_DYE, "§aBalance", ImmutableList.of("§a$" + playerData.get(pn).getMoney())));
        p.openInventory(inv);
    }

    public static void openKitEditor(Player p, String kit) {
        inInventory.put(p.getName(), Pair.of(4, kit));
        String key = p.getUniqueId() + "-kit" + kit;
        Inventory inv = Bukkit.createInventory(p, 54, ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "Kit " + kit);
        try {
            inv.setContents((ItemStack[]) Initializer.kitMap.get(key).get("items"));
        } catch (Exception ignored) {
        }

        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "Order does not matter!");
        ItemStack item = createItemStack(Material.BLACK_STAINED_GLASS_PANE, ChatColor.GRAY + "" + ChatColor.BOLD + "← ARMOR + OFFHAND", lore);
        inv.setItem(41, item);
        inv.setItem(42, item);
        inv.setItem(43, item);
        inv.setItem(44, item);
        lore.clear();
        item = createItemStack(Material.OAK_DOOR, "§cEXIT");
        inv.setItem(45, item);
        item = createItemStack(Material.BLACK_STAINED_GLASS_PANE, " ");
        inv.setItem(46, item);
        item = createItemStack(Material.CHEST, ChatColor.GREEN + "" + ChatColor.BOLD + "IMPORT CURRENT INVENTORY");
        inv.setItem(47, item);
        item = createItemStack(Material.CYAN_TERRACOTTA, ChatColor.GRAY + "" + ChatColor.BOLD + "COPY (COMING SOON)");
        inv.setItem(48, item);
        item = createItemStack(Material.CYAN_TERRACOTTA, ChatColor.GRAY + "" + ChatColor.BOLD + "PASTE (COMING SOON)");
        inv.setItem(49, item);
        item = createItemStack(Material.STRUCTURE_VOID, ChatColor.GREEN + "" + ChatColor.BOLD + "CLEAR");
        inv.setItem(50, item);

        item = createItemStack(Material.CYAN_TERRACOTTA, ChatColor.GRAY + "" + ChatColor.BOLD + "SHARE (COMING SOON)");
        inv.setItem(52, item);
        if (!Initializer.kitMap.get(key).containsKey("public")) {
            item = getHead(ChatColor.GREEN + "" + ChatColor.BOLD + "MAKE PUBLIC", "Kevos");
        } else
            item = createItemStack(Material.FIREWORK_STAR, ChatColor.GREEN + "" + ChatColor.BOLD + "MAKE PRIVATE");
        inv.setItem(53, item);
        p.openInventory(inv);
    }

    public static void openKitMenu(Player p) {
        inInventory.put(p.getName(), Pair.of(2, "0"));
        Inventory inv = Bukkit.createInventory(p, 54, ChatColor.BLUE + "" + ChatColor.BOLD + p.getName() + "'s Kits");
        List<String> lore = new ArrayList<>();

        int i;
        for (i = 1; i <= 3; ++i) {
            lore.add(ChatColor.LIGHT_PURPLE + "To edit:");
            lore.add(ChatColor.LIGHT_PURPLE + "• Right click this chest");
            lore.add(ChatColor.GRAY + "To load:");
            lore.add(ChatColor.GRAY + "• /k" + i);
            lore.add(ChatColor.GRAY + "• /kit" + i);
            String name = "§b§lKit " + i;
            ItemStack item = createItemStack(Material.CHEST, name, lore);
            inv.setItem(i + 9, item);
            lore.clear();
        }
        lore.add(ChatColor.GRAY + "COMING SOON");

        for (i = 1; i <= 3; ++i) {
            ItemStack item = createItemStack(Material.ENDER_CHEST, ChatColor.GRAY + "" + ChatColor.BOLD + "Ender Chest " + i, lore);
            inv.setItem(i + 18, item);
        }

        lore.clear();
        ItemStack item = createItemStack(Material.CHEST, ChatColor.GRAY + "" + ChatColor.BOLD + "LOCKED");
        inv.setItem(13, item);
        inv.setItem(14, item);
        inv.setItem(15, item);
        inv.setItem(16, item);
        item = createItemStack(Material.ENDER_CHEST, ChatColor.GRAY + "" + ChatColor.BOLD + "LOCKED");
        inv.setItem(22, item);
        inv.setItem(23, item);
        inv.setItem(24, item);
        inv.setItem(25, item);
        item = createItemStack(Material.EMERALD, ChatColor.GRAY + "" + ChatColor.BOLD + "UNLOCK MORE KIT SLOTS (COMING SOON)");
        inv.setItem(37, item);
        item = createItemStack(Material.NETHER_STAR, ChatColor.GREEN + "" + ChatColor.BOLD + "VIRTUAL KIT ROOM");
        inv.setItem(38, item);
        item = createItemStack(Material.OAK_SIGN, ChatColor.GRAY + "" + ChatColor.BOLD + "INFO (COMING SOON)");
        inv.setItem(40, item);
        item = createItemStack(Material.END_CRYSTAL, ChatColor.GRAY + "" + ChatColor.BOLD + "PREMADE KITS (COMING SOON)");
        inv.setItem(42, item);
        item = getHead(ChatColor.GREEN + "" + ChatColor.BOLD + "PUBLIC KITS", "Kevos");
        inv.setItem(43, item);
        item = createItemStack(Material.LIGHT_BLUE_STAINED_GLASS_PANE, " ");

        for (i = 28; i <= 34; ++i) {
            inv.setItem(i, item);
        }

        item = createItemStack(Material.BLACK_STAINED_GLASS_PANE, " ");
        inv.setItem(39, item);
        inv.setItem(41, item);
        item = createItemStack(Material.BLUE_STAINED_GLASS_PANE, " ");

        for (i = 0; i < inv.getContents().length; ++i) {
            if (inv.getItem(i) == null) {
                inv.setItem(i, item);
            }
        }
        p.openInventory(inv);
    }

    public static void openKitRoom(Player p) {
        inInventory.put(p.getName(), Pair.of(3, ""));
        Inventory inv = Bukkit.createInventory(p, 54, ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "Virtual Kit Room");
        inv.setContents(Initializer.kitRoomMap.get(1));
        ItemStack item;
        if (p.isOp()) {
            List<String> lore = new ArrayList<>();
            ItemStack saveButton = createItemStack(Material.GREEN_TERRACOTTA, ChatColor.GREEN + "" + ChatColor.BOLD + "SAVE CHANGES", lore);
            inv.setItem(45, saveButton);
        } else {
            item = createItemStack(Material.OAK_DOOR, "§cEXIT");
            inv.setItem(45, item);
        }

        item = createItemStack(Material.STRUCTURE_VOID, ChatColor.GREEN + "" + ChatColor.BOLD + "REFILL");
        inv.setItem(53, item);
        ItemStack[] buttons = new ItemStack[]{createItemStack(Material.NETHERITE_SWORD, ChatColor.GREEN + "Armory"), createItemStack(Material.SPLASH_POTION, ChatColor.GREEN + "Potions"), createItemStack(Material.ENDER_PEARL, ChatColor.GREEN + "Consumables"), createItemStack(Material.TIPPED_ARROW, ChatColor.GREEN + "Arrows"), createItemStack(Material.RESPAWN_ANCHOR, ChatColor.GREEN + "Explosives")};
        int i = 47;
        int j;
        for (j = 0; j < buttons.length; ++j) {
            ItemStack button = buttons[j];
            ItemMeta meta = button.getItemMeta();
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
            button.setItemMeta(meta);
            inv.setItem(i++, button);
        }

        item = inv.getItem(47);
        ItemMeta meta = item.getItemMeta();
        meta.addEnchant(Enchantment.DURABILITY, 1, true);
        item.setItemMeta(meta);
        item = inv.getItem(50);
        PotionMeta potMeta = (PotionMeta) item.getItemMeta();
        potMeta.setColor(Color.WHITE);
        item.setItemMeta(potMeta);
        item = createItemStack(Material.BLACK_STAINED_GLASS_PANE, " ");

        for (j = 45; j <= 53; ++j) {
            if (inv.getItem(j) == null) {
                inv.setItem(j, item);
            }
        }
        p.openInventory(inv);
    }

    public static void openPublicKits(Player p, int pg) {
        String pn = p.getName();
        inInventory.put(pn, Pair.of(5, String.valueOf(pg)));
        List<ItemStack> allItems = new ArrayList<>();
        List<String> lore = new ArrayList<>();
        for (Map.Entry<String, Map<String, Object>> stringHashMapEntry : Initializer.kitMap.entrySet()) {
            Map<String, Object> value = stringHashMapEntry.getValue();
            if (value.containsKey("public")) {
                lore.add("§dBy " + value.get("player"));
                ItemStack head = createItemStack(Material.CHEST, "§b§lKit", lore);
                ItemMeta meta = head.getItemMeta();
                NamespacedKey itemKey = new NamespacedKey(Initializer.p, "key");
                meta.getPersistentDataContainer().set(itemKey, PersistentDataType.STRING, stringHashMapEntry.getKey());
                head.setItemMeta(meta);
                allItems.add(head);
                lore.clear();
            }
        }

        int numPages = (int) Math.ceil((double) allItems.size() / 28.0D);
        int endIndex = pg * 28;
        if (pg == numPages) {
            ItemStack filler = createItemStack(Material.CRIMSON_BUTTON, " ");
            while (allItems.size() < numPages * 28) {
                allItems.add(filler);
            }
        }

        ItemStack[] page = Arrays.copyOfRange(allItems.toArray(new ItemStack[28]), endIndex - 28, endIndex);
        Inventory inv = Bukkit.createInventory(null, 54, ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "Public Kits");
        inv.setItem(49, createItemStack(Material.OAK_DOOR, ChatColor.RED + "EXIT"));
        if (pg > 1) inv.setItem(48, getHead(ChatColor.GREEN + "← Back", "MHF_ArrowLeft"));
        if (pg < numPages) inv.setItem(50, getHead(ChatColor.GREEN + "Next →", "MHF_ArrowRight"));
        ItemStack glass = createItemStack(Material.PURPLE_STAINED_GLASS_PANE, " ");

        int i;
        for (i = 0; i <= 8; ++i) {
            inv.setItem(i, glass);
        }

        for (i = 45; i <= 53; ++i) {
            if (inv.getItem(i) == null) {
                inv.setItem(i, glass);
            }
        }
        for (i = 9; i <= 36; i += 9) {
            inv.setItem(i, glass);
        }
        for (i = 17; i <= 44; i += 9) {
            inv.setItem(i, glass);
        }
        for (ItemStack item : page) {
            inv.setItem(inv.firstEmpty(), item);
        }
        p.openInventory(inv);
    }
}