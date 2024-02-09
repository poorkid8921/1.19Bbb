package main.utils.instances;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Getter;
import lombok.Setter;
import main.utils.Constants;
import main.utils.optimizer.AnimPackets;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;

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
    private AnimPackets lastPacket = AnimPackets.MISC;
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
    private boolean RTPing;
    @Getter
    @Setter
    private Map<String, Location> homes;
    @Getter
    @Setter
    private ObjectArrayList<MailHolder> mails;

    public CustomPlayerDataHolder(int m,
                                  int t,
                                  double z,
                                  int deaths,
                                  int kills,
                                  Map<String, Location> homes,
                                  ObjectArrayList<MailHolder> mails) {
        this.money = z;
        this.mtoggle = m;
        this.tptoggle = t;
        this.deaths = deaths;
        this.kills = kills;
        this.homes = homes;
        this.mails = mails;
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
        runnable.runTaskTimer(Constants.p, 0L, 20L);
        this.runnableid = runnable.getTaskId();
        this.tagged = true;
    }

    public void untag() {
        this.currentTagTime = 0;
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