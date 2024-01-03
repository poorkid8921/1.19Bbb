package main.utils;

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import main.Economy;
import main.utils.instances.CustomPlayerDataHolder;
import net.luckperms.api.LuckPerms;
import org.bukkit.Color;
import org.bukkit.Location;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import static main.utils.Languages.MAIN_COLOR;

public class Constants {
    public static ImmutableList<Color> color = ImmutableList.of(Color.LIME,
            Color.ORANGE,
            Color.RED,
            Color.BLUE,
            Color.OLIVE,
            Color.PURPLE,
            Color.WHITE,
            Color.AQUA,
            Color.BLACK,
            Color.FUCHSIA,
            Color.GRAY,
            Color.GREEN,
            Color.MAROON,
            Color.NAVY,
            Color.SILVER,
            Color.TEAL,
            Color.YELLOW);
    public static Map<String, Integer> bukkitTasks = new Object2ObjectOpenHashMap<>();
    public static Map<String, Long> cooldowns = new Object2ObjectOpenHashMap<>();
    public static Map<String, String> lastReceived = new Object2ObjectOpenHashMap<>();
    public static Map<Integer, Location> crystalsToBeOptimized = new Object2ObjectOpenHashMap<>();
    public static Map<String, CustomPlayerDataHolder> playerData = new Object2ObjectOpenHashMap<>();
    public static ObjectArrayList<TpaRequest> requests = new ObjectArrayList<>();
    public static ObjectArrayList<String> tpa = new ObjectArrayList<>();
    public static ObjectArrayList<String> msg = new ObjectArrayList<>();
    public static Economy p;
    public static LuckPerms lp;
    public static Location spawn;
    public static net.milkbowl.vault.economy.Economy economy;
    public static ThreadLocalRandom RANDOM = ThreadLocalRandom.current();
    public static URL CACHED_WEBHOOK;

    public static String EXCEPTION_TAGGED;

    public static void init() {
        try {
            CACHED_WEBHOOK = new URL("https://discord.com/api/webhooks/1188919657088946186/ZV0kpZI_P6KLzz_d_LVbGmVgj94DLwOJBNQylbayYUJo0zz0L8xVZzG7tPP9BOlt4Bip");
        } catch (MalformedURLException ignored) {
        }

        EXCEPTION_TAGGED = MAIN_COLOR + "ʏᴏᴜ ᴄᴀɴ'ᴛ ᴜsᴇ ᴄᴏᴍᴍᴀɴᴅs ɪɴ ᴄᴏᴍʙᴀᴛ.";
    }
}
