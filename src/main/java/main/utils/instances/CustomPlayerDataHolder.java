package main.utils.instances;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Getter;
import lombok.Setter;
import main.utils.Initializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import static main.utils.Initializer.*;

public class CustomPlayerDataHolder {
    @Getter
    @Setter
    private int mtoggle;
    @Getter
    @Setter
    private int tptoggle;
    @Getter
    @Setter
    private double money;
    @Getter
    private int deaths;
    @Getter
    private int kills;
    @Getter
    @Setter
    private int lastPacket = 5;
    @Getter
    @Setter
    private boolean ignoreAnim = false;
    @Getter
    @Setter
    private int runnableid;
    private int currentTagTime = 10;
    @Getter
    private boolean tagged;
    @Getter
    @Setter
    private String lastReceived;
    @Getter
    @Setter
    private Pair<Integer, String> inventoryInfo;
    @Getter
    @Setter
    private long lastRTPed;
    @Getter
    @Setter
    private ObjectArrayList<HomeHolder> homes;
    @Getter
    @Setter
    private int rank;
    @Getter
    @Setter
    private long lastChatMS;
    @Getter
    @Setter
    private int flags;

    public CustomPlayerDataHolder(int m,
                                  int t,
                                  double z,
                                  int deaths,
                                  int kills,
                                  ObjectArrayList<HomeHolder> homes) {
        this.money = z;
        this.mtoggle = m;
        this.tptoggle = t;
        this.deaths = deaths;
        this.kills = kills;
        this.homes = homes;
        this.lastChatMS = 0L;
    }

    public String getFRank(String name) {
        return switch (rank) {
            case 0 -> name;
            case 1 -> ANGEL + name;
            case 2 -> CATTO_LOVES + name;
            case 3 -> CATTO_HATES + name;
            case 4 -> GAY + name;
            case 5 -> VIP + name;
            case 6 -> BOOSTER + name;
            case 7 -> MEDIA + name;
            case 8 -> T_HELPER + name;
            case 9 -> HELPER + name;
            case 10 -> JRMOD + name;
            case 11 -> MOD + name;
            case 12 -> ADMIN + name;
            case 13 -> MANAGER + name;
            case 14 -> EXECUTIVE + name;
            default -> "";
        };
    }

    public void incrementFlags() {
        flags++;
    }

    public void setupCombatRunnable(Player player) {
        this.currentTagTime = 10;
        this.runnableid = new BukkitRunnable() {
            @Override
            public void run() {
                if (currentTagTime-- < 1) {
                    currentTagTime = 0;
                    player.sendActionBar("§7ᴄᴏᴍʙᴀᴛ: §40");
                    tagged = false;
                    this.cancel();
                    return;
                }
                player.sendActionBar("§7ᴄᴏᴍʙᴀᴛ: §4" + currentTagTime);
            }
        }.runTaskTimer(Initializer.p, 0L, 20L).getTaskId();
        this.tagged = true;
    }

    public void untag() {
        this.tagged = false;
        Bukkit.getScheduler().cancelTask(this.runnableid);
    }

    public void incrementMoney(double money) {
        this.money += money;
    }

    public void decrementMoney(double money) {
        this.money -= money;
    }

    public void incrementDeaths() {
        this.deaths++;
    }

    public void incrementKills() {
        this.kills++;
    }

    public void setTagTime(Player player) {
        this.currentTagTime = 10;
        player.sendActionBar("§7ᴄᴏᴍʙᴀᴛ: §410");
    }
}