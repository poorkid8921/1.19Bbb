package main.managers.instances;

import it.unimi.dsi.fastutil.Pair;
import lombok.Getter;
import lombok.Setter;
import main.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import static main.Economy.teamManager;
import static main.utils.Initializer.*;

@Getter
@Setter
public class PlayerDataHolder {
    private final int hashCode;
    private int mtoggle;
    private int tptoggle;
    private int deaths;
    private int kills;
    private int lastPacket;
    private int lastItemPacket;
    private boolean ignoreAnim = false;
    private int runnableid;
    private int currentTagTime = 10;
    private boolean tagged;
    private String lastReceived;
    private Pair<Integer, String> inventoryInfo;
    private long lastRTPed;
    private int rank;
    private long lastChatMS;
    private int flags;
    private boolean fastCrystals;
    private long lastTagged = 0L;
    private int preLastPacket;

    public PlayerDataHolder(final int rank,
                            final int m,
                            final int t,
                            final int deaths,
                            final int kills) {
        this.rank = rank;
        this.mtoggle = m;
        this.tptoggle = t;
        this.deaths = deaths;
        this.kills = kills;
        this.lastChatMS = 0L;
        this.hashCode = customPlayerDataHashCode++;
    }

    public PlayerDataHolder(final int m,
                            final int t,
                            final int deaths,
                            final int kills) {
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
            case 1 -> teamManager.CATTO_LOVES + name;
            case 2 -> teamManager.CATTO_HATES + name;
            case 3 -> teamManager.GAY + name;
            case 4 -> teamManager.VIP + name;
            case 5 -> teamManager.BOOSTER + name;
            case 6 -> teamManager.MEDIA + name;
            case 7 -> teamManager.T_HELPER + name;
            case 8 -> teamManager.HELPER + name;
            case 9 -> teamManager.JRMOD + name;
            case 10 -> teamManager.MOD + name;
            case 11 -> teamManager.ADMIN + name;
            case 12 -> teamManager.MANAGER + name;
            case 13 -> teamManager.EXECUTIVE + name;
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
        }.runTaskTimer(Economy.INSTANCE, 0L, 20L).getTaskId();
        this.tagged = true;
    }

    public void untag() {
        this.tagged = false;
        Bukkit.getScheduler().cancelTask(this.runnableid);
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