package main.utils.Instances;

import expansions.optimizer.AnimPackets;
import lombok.Getter;
import lombok.Setter;
import main.utils.Constants;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class CustomPlayerDataHolder {
    @Getter
    @Setter
    private int wins;
    @Getter
    @Setter
    private int losses;
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
    @Setter
    private int elo;
    @Getter
    private int deaths;
    @Getter
    private int kills;
    @Getter
    @Setter
    private WorldLocationHolder back;
    @Getter
    @Setter
    private AnimPackets lastPacket = AnimPackets.MISC;
    @Getter
    @Setter
    private boolean ignoreAnim = false;
    @Setter
    private int runnableid;
    private int currentTagTime = 5;
    @Getter
    @Setter
    private boolean tagged;
    private ArrayList<Long> windowQuitTime;
    private ArrayList<Long> windowOpenTime;

    public CustomPlayerDataHolder(int wins,
                                  int losses,
                                  int c,
                                  int m,
                                  int t,
                                  int z,
                                  int elo,
                                  int deaths,
                                  int kills) {
        this.wins = wins;
        this.losses = losses;
        this.money = z;
        this.killeffect = c;
        this.mtoggle = m;
        this.tptoggle = t;
        this.elo = elo;
        this.deaths = deaths;
        this.kills = kills;
    }

    public ArrayList<Long> windowQuitTime() {
        return windowQuitTime;
    }

    public ArrayList<Long> windowOpenTime() {
        return windowQuitTime;
    }

    public void setupCombatRunnable(Player player) {
        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                if (currentTagTime-- < 1) {
                    currentTagTime = 0;
                    player.sendActionBar("§7ᴄᴏᴍʙᴀᴛ: §40");
                    setTagged(false);
                    this.cancel();
                    return;
                }
                player.sendActionBar("§7ᴄᴏᴍʙᴀᴛ: §4" + currentTagTime);
            }
        };
        runnable.runTaskTimer(Constants.p, 0L, 20L);
        setRunnableid(runnable.getTaskId());
        tagged = true;
    }

    public void untag() {
        tagged = false;
        Bukkit.getScheduler().cancelTask(this.runnableid);
    }

    public void incrementElo(int elo) {
        this.elo += elo;
    }

    public void decrementElo(int elo) {
        this.elo -= elo;
    }

    public void incrementWins() {
        this.wins++;
    }

    public void incrementLosses() {
        this.losses++;
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
        currentTagTime = 5;
        player.sendActionBar("§7ᴄᴏᴍʙᴀᴛ: §45");
    }
}
