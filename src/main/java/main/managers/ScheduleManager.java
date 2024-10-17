package main.managers;

import main.Economy;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class ScheduleManager {
    public void later(Runnable runnable, long time) {
        Bukkit.getScheduler().runTaskLater(Economy.INSTANCE, runnable, time);
    }

    public void now(Runnable runnable) {
        Bukkit.getScheduler().runTask(Economy.INSTANCE, runnable);
    }
}
