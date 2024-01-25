package main.utils.instances;

import expansions.optimizer.AnimPackets;
import lombok.Getter;
import lombok.Setter;
import main.utils.Constants;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class CustomPlayerDataHolder {
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
    private AnimPackets lastPacket = AnimPackets.MISC;
    @Getter
    @Setter
    private boolean ignoreAnim = false;
    @Getter
    @Setter
    private int runnableid;
    private int currentTagTime = 5;
    @Getter
    @Setter
    private boolean tagged;
    @Getter
    @Setter
    private String lastReceived;

    public CustomPlayerDataHolder(int m,
                                  int t,
                                  int z,
                                  int deaths,
                                  int kills) {
        this.money = z;
        this.mtoggle = m;
        this.tptoggle = t;
        this.deaths = deaths;
        this.kills = kills;
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