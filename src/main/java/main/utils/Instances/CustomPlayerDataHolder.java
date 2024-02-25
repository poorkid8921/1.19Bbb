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
    private int lastPacket = 5;
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
    private String lastReceived;
    @Getter
    @Setter
    private boolean multipleGUIs = false;
    @Getter
    @Setter
    private int rank;
    @Getter
    @Setter
    private long lastChatMS;
    @Getter
    @Setter
    private int flags;

    public CustomPlayerDataHolder(int c, int m, int t, int z, int deaths, int kills, int rank) {
        this.killeffect = c;
        this.mtoggle = m;
        this.tptoggle = t;
        this.money = z;
        this.deaths = deaths;
        this.kills = kills;
        this.rank = rank;
        this.lastChatMS = 0L;
    }

    public String getFRank(String pn) {
        return switch (rank) {
            case 0 -> pn;
            case 1 -> CATTO_LOVES + pn;
            case 2 -> CATTO_HATES + pn;
            case 3 -> GAY + pn;
            case 4 -> QUACK + pn;
            case 5 -> CLAPCLAP + pn;
            case 6 -> VIP + pn;
            case 7 -> BOOSTER + pn;
            case 8 -> MEDIA + pn;
            case 9 -> T_HELPER + pn;
            case 10 -> HELPER + pn;
            case 11 -> JRMOD + pn;
            case 12 -> MOD + pn;
            case 13 -> ADMIN + pn;
            case 14 -> MANAGER + pn;
            case 15 -> EXECUTIVE + pn;
            default -> "";
        };
    }

    public void incrementFlags() {
        flags++;
    }

    public void setupCombatRunnable(Player player) {
        this.currentTagTime = 10;
        BukkitRunnable runnable = new BukkitRunnable() {
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
        };
        runnable.runTaskTimer(Initializer.p, 0L, 20L);
        this.runnableid = runnable.getTaskId();
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
}
