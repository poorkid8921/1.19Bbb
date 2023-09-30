package main.expansions.kits;

import main.AestheticNetwork;
import main.utils.Messages.Initializer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.Server;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

public class Utils {
    public static Map<String, Map<String, Object>> kitMap = new WeakHashMap<>();
    public static Map<Integer, ItemStack[]> kitRoomMap = new WeakHashMap<>();
    public static Map<String, Integer> publicChecker = new WeakHashMap<>();
    public static Map<String, Integer> editorChecker = new WeakHashMap<>();
    public static Map<String, Integer> checker = new WeakHashMap<>();

    public static NamespacedKey itemKey = new NamespacedKey(Initializer.p, "key");
    public static Inventory[] kitsInventory = {Bukkit.createInventory(null, 54)};
    public static Server s = Bukkit.getServer();

    public static File kitsFile;
    public static FileConfiguration kitsConf;

    public static File kitRoomFile;
    public static FileConfiguration kitRoomConf;

    public static void save(final Player player, final int i, final boolean sendMsg) {
        String n = player.getName();
        final String key = n + "-" + i;
        String kitName = "Kit " + i;
        final ItemStack[] itemsArray = Arrays.copyOfRange(player.getOpenInventory().getTopInventory().getContents(), 0, 41);
        final ItemStack[] equipmentArray = new ItemStack[5];
        boolean arrayIsEmpty = true;
        for (int j = 0; j <= 40; ++j) {
            if (itemsArray[j] != null) {
                arrayIsEmpty = false;
                break;
            }
        }
        if (!arrayIsEmpty) {
            for (int j = 36; j <= 40; ++j) {
                equipmentArray[j - 36] = itemsArray[j];
                itemsArray[j] = null;
            }
            for (int j = 0; j <= 4; ++j) {
                if (equipmentArray[j] != null) {
                    if (equipmentArray[j].getType().toString().contains("BOOTS")) {
                        itemsArray[36] = equipmentArray[j];
                    } else if (equipmentArray[j].getType().toString().contains("LEGGINGS")) {
                        itemsArray[37] = equipmentArray[j];
                    } else if (equipmentArray[j].getType().toString().contains("CHESTPLATE") || equipmentArray[j].getType().toString().contains("ELYTRA")) {
                        itemsArray[38] = equipmentArray[j];
                    } else if (equipmentArray[j].getType().toString().contains("HELMET")) {
                        itemsArray[39] = equipmentArray[j];
                    } else {
                        itemsArray[40] = equipmentArray[j];
                    }
                }
            }
            Utils.kitMap.get(key).putIfAbsent("player", n);
            Utils.kitMap.get(key).put("items", itemsArray);
            if (sendMsg) {
                if (!kitName.equals("Kit " + i)) {
                    player.sendMessage("§dSaved §b" + kitName + "§d! Type §b/k" + i + "§d, §b/kit" + i + "§d, or §b/kit " + kitName + " §dto load!");
                } else {
                    player.sendMessage(ChatColor.LIGHT_PURPLE + "Saved " + ChatColor.AQUA + kitName + ChatColor.LIGHT_PURPLE + "! Type" + ChatColor.AQUA + " /k" + i + ChatColor.LIGHT_PURPLE + " or" + ChatColor.AQUA + " /kit" + i + ChatColor.LIGHT_PURPLE + " to load!");
                }
            }
        } else {
            Utils.kitMap.get(key).remove("items");
            if (Utils.kitMap.get(key).containsKey("public")) {
                Utils.kitMap.get(key).remove("public");
                player.sendMessage("§6" + kitName + " §cwas empty, so it was removed from public kits.");
            }
        }
    }

    public static void setupKitsFile(AestheticNetwork a) {
        kitsFile = new File(a.getDataFolder(), "kits.yml");
        if (!kitsFile.exists()) {
            try {
                kitsFile.createNewFile();
            } catch (IOException ignored) {
            }
        }
        kitsConf = YamlConfiguration.loadConfiguration(kitsFile);
    }

    public static void setupKitroom(AestheticNetwork a) {
        kitRoomFile = new File(a.getDataFolder(), "kitroom.yml");
        if (!kitRoomFile.exists()) {
            try {
                kitRoomFile.createNewFile();
            } catch (IOException ignored) {
            }
        }
        kitRoomConf = YamlConfiguration.loadConfiguration(kitRoomFile);
    }

    public static void restoreKitMap() {
        try {
            for (String key : kitsConf.getConfigurationSection("data").getKeys(false)) {
                kitMap.put(key, new WeakHashMap<>());
                for (String key2 : kitsConf.getConfigurationSection("data." + key).getKeys(false)) {
                    switch (key2) {
                        case "items" -> {
                            ItemStack[] items = List.of(kitsConf.get("data." + key + "." + key2)).toArray(new ItemStack[0]);
                            kitMap.get(key).put(key2, items);
                        }
                        case "player" -> {
                            String player = kitsConf.getString("data." + key + "." + key2);
                            kitMap.get(key).put(key2, player);
                        }
                        case "UUID" -> {
                            String uUID = kitsConf.getString("data." + key + "." + key2);
                            kitMap.get(key).put(key2, uUID);
                        }
                        default -> {
                            if (!key2.equals("public")) {
                                continue;
                            }
                            String isPublic = kitsConf.getString("data." + key + "." + key2);
                            kitMap.get(key).put(key2, isPublic);
                        }
                    }
                }
            }
        } catch (Exception ignored) {
        }
    }

    public static void restoreKitRoom() {
        for (int i = 1; i <= 5; ++i) {
            try {
                ItemStack[] content = List.of(kitRoomConf.get("data." + i)).toArray(new ItemStack[0]);
                kitRoomMap.put(i, content);
            } catch (Exception ignored) {
                kitRoomMap.put(i, new ItemStack[45]);
            }
        }
    }

    public static void broadcast(String a) {
        s.broadcastMessage(a);
    }

    public static void claim(Player player, int kit, boolean fromCommand) {
        String a = player.getName();
        String key = a + "-" + kit;
        try {
            player.getInventory().setContents((ItemStack[]) kitMap.get(key).get("items"));
            player.sendMessage(ChatColor.LIGHT_PURPLE + "Loaded" + ChatColor.AQUA + " Kit " + kit + ChatColor.LIGHT_PURPLE + "!");
            broadcast(Languages.getWhoLoaded(player.getName()));
        } catch (Exception e) {
            String msg = ChatColor.GOLD + "Kit " + kit + ChatColor.RED + " has not been created! ";
            if (fromCommand) {
                msg = msg + "Type" + ChatColor.GOLD + " /kit" + ChatColor.RED + " or" + ChatColor.GOLD + " /k" + ChatColor.RED + " to get started!";
            } else {
                msg += "Right click the chest to customize!";
            }
            player.sendMessage(msg);
        }
    }

    public static void claimPublicKit(Player player, String key) {
        player.getInventory().setContents((ItemStack[]) kitMap.get(key).get("items"));
        //String name = "Kit";
        //player.sendMessage(ChatColor.LIGHT_PURPLE + "Loaded " + ChatColor.AQUA + name + ChatColor.LIGHT_PURPLE + "!");
        broadcast(Languages.getWhoLoadedPublic(player.getName(), kitMap.get(key).get("player").toString()));
    }

    public static void saveKits() throws IOException {
        kitsConf.set("data", null);
        if (!kitMap.isEmpty()) {
            for (Map.Entry<String, Map<String, Object>> kit : kitMap.entrySet()) {
                for (Map.Entry<String, Object> data : kit.getValue().entrySet()) {
                    kitsConf.set("data." + kit.getKey() + "." + data.getKey(), data.getValue());
                }
            }
        }
        kitsConf.save(kitsFile);

        if (!kitRoomMap.isEmpty()) {
            for (Map.Entry<Integer, ItemStack[]> entry : kitRoomMap.entrySet()) {
                kitRoomConf.set("data." + entry.getKey().toString(), entry.getValue());
            }
        }
        kitRoomConf.save(kitRoomFile);
    }
}