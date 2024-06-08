package main.utils.Instances;

import lombok.Getter;
import lombok.Setter;
import main.utils.Initializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import static main.utils.Initializer.*;

public class CustomPlayerDataHolder {
    private final int hashCode;
    @Getter
    @Setter
    private int killeffect;
    @Getter
    @Setter
    private int mtoggle;
    @Getter
    @Setter
    private int tptoggle;
    @Getter
    @Setter
    private int money;
    @Getter
    private int deaths;
    @Getter
    private int kills;
    @Getter
    @Setter
    private Location back;
    @Getter
    @Setter
    private int lastPacket;
    @Getter
    @Setter
    private int lastItemPacket;
    @Getter
    @Setter
    private boolean ignoreAnim = false;
    @Setter
    private int runnableid;
    private int currentTagTime = 10;
    @Getter
    private boolean tagged;
    @Getter
    @Setter
    private String lastReceived, lastTaggedBy;
    @Getter
    @Setter
    private int rank;
    @Getter
    @Setter
    private long lastChatMS = 0L;
    private int flags;
    @Setter
    private int flatFlags;
    @Getter
    @Setter
    private boolean fastCrystals = true;
    @Getter
    @Setter
    private long lastTimeKitWasUsed;
    @Getter
    @Setter
    private long lastTagged;
    @Getter
    @Setter
    private int preLastPacket;

    public CustomPlayerDataHolder(final int c, final int m, final int t, final int z, final int deaths, final int kills) {
        this.killeffect = c;
        this.mtoggle = m;
        this.tptoggle = t;
        this.money = z;
        this.deaths = deaths;
        this.kills = kills;
        this.lastTimeKitWasUsed = System.currentTimeMillis();
        this.hashCode = customPlayerDataHashCode++;
    }

    public CustomPlayerDataHolder(final int rank, final int c, final int m, final int t, final int z, final int deaths, final int kills) {
        this.rank = rank;
        this.killeffect = c;
        this.mtoggle = m;
        this.tptoggle = t;
        this.money = z;
        this.deaths = deaths;
        this.kills = kills;
        this.lastTimeKitWasUsed = System.currentTimeMillis();
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

    public int incrementFlatFlags() {
        return flatFlags++;
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

    public void incrementMoney(int money) {
        this.money += money;
    }

    public void decrementMoney(int money) {
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
