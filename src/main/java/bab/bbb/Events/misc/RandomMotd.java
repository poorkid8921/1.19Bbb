package bab.bbb.Events.misc;

import bab.bbb.Bbb;
import bab.bbb.utils.Utils;
import com.destroystokyo.paper.event.server.PaperServerListPingEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;
import java.util.Random;

public class RandomMotd implements Listener {
    private final Bbb plugin = Bbb.getInstance();

    @EventHandler
    public void serverList(PaperServerListPingEvent e) {
        List<String> list = plugin.getConfig().getStringList("motd");

        if (Bukkit.getServer().getOnlinePlayers().size() == 1)
            e.setMotd(Utils.parseText(list.get(new Random().nextInt(list.size()))).replace("players", "player"));
        else
            e.setMotd(Utils.parseText(list.get(new Random().nextInt(list.size()))));
    }
}
