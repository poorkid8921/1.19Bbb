package main.utils.instances;

import com.github.retrooper.packetevents.util.Vector3d;
import it.unimi.dsi.fastutil.Pair;
import lombok.Getter;
import lombok.Setter;
import main.utils.Initializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import static main.utils.Initializer.*;

public class CustomPlayerDataHolder {
    private final int hashCode;
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
    private int lastItemPacket = 0;
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
    private int rank;
    @Getter
    @Setter
    private long lastChatMS;
    private int flags;
    @Getter
    @Setter
    private boolean fastCrystals;
    @Getter
    @Setter
    private long lastTagged = 0L;
    @Getter
    @Setter
    private int preLastPacket = 0;

    public CustomPlayerDataHolder(int m,
                                  int t,
                                  double z,
                                  int deaths,
                                  int kills,
                                  int rank) {
        this.money = z;
        this.mtoggle = m;
        this.tptoggle = t;
        this.deaths = deaths;
        this.kills = kills;
        this.lastChatMS = 0L;
        this.rank = rank;
        this.hashCode = customPlayerDataHashCode++;
    }

    public CustomPlayerDataHolder(int m,
                                  int t,
                                  double z,
                                  int deaths,
                                  int kills) {
        this.money = z;
        this.mtoggle = m;
        this.tptoggle = t;
        this.deaths = deaths;
        this.kills = kills;
        this.lastChatMS = 0L;
        this.rank = 0;
        this.hashCode = customPlayerDataHashCode++;
    }

    public String getFRank(String name) {
        return switch (rank) {
            case 0 -> name;
            case 1 -> CATTO_LOVES + name;
            case 2 -> CATTO_HATES + name;
            case 3 -> GAY + name;
            case 4 -> VIP + name;
            case 5 -> BOOSTER + name;
            case 6 -> MEDIA + name;
            case 7 -> T_HELPER + name;
            case 8 -> HELPER + name;
            case 9 -> JRMOD + name;
            case 10 -> MOD + name;
            case 11 -> ADMIN + name;
            case 12 -> MANAGER + name;
            case 13 -> EXECUTIVE + name;
            default -> "";
        };
    }

    public int incrementFlags() {
        return flags++;
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

    @Override
    public boolean equals(Object o) {
        return this == o || o.hashCode() == hashCode;
    }

    @Override
    public int hashCode() {
        return hashCode;
    }
}