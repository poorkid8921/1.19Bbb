package main.utils;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import main.Economy;
import main.Events;
import main.managers.instances.*;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class Initializer {
    public static final AbstractRegionHolder.YDeficientRegionHolder spawnRegionHolder = new AbstractRegionHolder.YDeficientRegionHolder(-24, 23, 22, -23);
    public static final Color[] color = new Color[]{org.bukkit.Color.LIME, org.bukkit.Color.ORANGE, org.bukkit.Color.RED, org.bukkit.Color.BLUE, org.bukkit.Color.OLIVE, org.bukkit.Color.PURPLE, org.bukkit.Color.WHITE, org.bukkit.Color.AQUA, org.bukkit.Color.BLACK, org.bukkit.Color.FUCHSIA, org.bukkit.Color.GRAY, org.bukkit.Color.GREEN, org.bukkit.Color.MAROON, org.bukkit.Color.NAVY, org.bukkit.Color.SILVER, org.bukkit.Color.TEAL, org.bukkit.Color.YELLOW};
    public static final Cache<String, Integer> leverFlickCount = Caffeine.newBuilder().expireAfterWrite(Duration.ofMillis(500L)).build();
    public static final Map<Integer, Location> crystalsToBeOptimized = new Int2ObjectOpenHashMap<>();
    public static final Map<String, PlayerDataHolder> playerData = new Object2ObjectOpenHashMap<>();
    public static final Location[] overworldRTP = new Location[100];
    public static final Location[] netherRTP = new Location[100];
    public static final Location[] endRTP = new Location[100];
    public static final ObjectArrayList<TpaRequest> requests = ObjectArrayList.of();
    public static final ObjectArrayList<String> tpa = ObjectArrayList.of();
    public static final ObjectArrayList<String> msg = ObjectArrayList.of();

    public static AbstractRegionHolder[] regions = new AbstractRegionHolder[]{
            spawnRegionHolder,
            new AbstractRegionHolder.RegionHolder(-46, 133, -45, 44, 133, 45),// flat
            new AbstractRegionHolder.RegionHolder(-128, 137, -127, 126, 198, 127),// arena
            new AbstractRegionHolder.YDeficientRegionHolder(112, -112, -113, -112),// wall1
            new AbstractRegionHolder.YDeficientRegionHolder(111, -111, 111, 112),// wall2
            new AbstractRegionHolder.YDeficientRegionHolder(-113, 112, 110, 112),// wall3
            new AbstractRegionHolder.YDeficientRegionHolder(-113, -111, -113, 111),// wall4
            new AbstractRegionHolder.RegionHolder(110, 2, 111, -112, 2, -111) // underarena
    };
    public static Location spawn;
    public static ThreadLocalRandom RANDOM = ThreadLocalRandom.current();
    public static URL CACHED_WEBHOOK;
    public static URL CACHED_MODERATION_WEBHOOK;

    public static int customPlayerDataHashCode;

    public static void init() {
        try {
            CACHED_WEBHOOK = new URL("https://discord.com/api/webhooks/1188919657088946186/ZV0kpZI_P6KLzz_d_LVbGmVgj94DLwOJBNQylbayYUJo0zz0L8xVZzG7tPP9BOlt4Bip");
            CACHED_MODERATION_WEBHOOK = new URL("https://discord.com/api/webhooks/1212078373132836994/7SoUq23VE2PnFN4AtTqI8nmJ0NHXEaqvvrJsL4eMVVx_IKvISjKfG878wCpqonO_BAxL");
        } catch (MalformedURLException ignored) {
        }
        DISCORD_LINK.setColor(ChatColor.of("#fc282f"));
        DISCORD_LINK.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://discord.gg/catsmp"));

        main.utils.modules.npcs.Utils.init();
        main.utils.modules.holos.Utils.init();
        main.utils.modules.tab.Utils.init();
    }
}
