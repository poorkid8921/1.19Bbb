package bab.bbb.utils;

import bab.bbb.Bbb;
import org.bukkit.entity.Player;
import java.util.HashMap;

public class Cooldown {
	private final HashMap<Player, Double> cooldowns = new HashMap<Player, Double>();
	private final long deley = Bbb.getInstance().getConfig().getInt("better-chat-cooldown");

	public void setCooldown(Player player) {
		double delay = System.currentTimeMillis() + (deley * 1000L);
		cooldowns.put(player, delay);
	}

	public boolean checkCooldown(Player player) {
		return !cooldowns.containsKey(player) || cooldowns.get(player) <= System.currentTimeMillis();
	}
}