package org.yuri.aestheticnetwork.utils;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.yuri.aestheticnetwork.AestheticNetwork;
import org.yuri.aestheticnetwork.commands.duel.DuelRequest;
import org.yuri.aestheticnetwork.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import static org.yuri.aestheticnetwork.utils.duels.DuelManager.getDUELrequest;

public class placeholders extends PlaceholderExpansion {
    AestheticNetwork pl;

    public placeholders(AestheticNetwork ad) {
        pl = ad;
    }

    @Override
    public @NotNull String getAuthor() {
        return "Catto69420";
    }

    @Override
    public @NotNull String getIdentifier() {
        return "duels";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public boolean persist() {
        return true; // This is required or else PlaceholderAPI will unregister the Expansion on reload
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        if (player == null) return String.valueOf(0);

        String name = player.getName();

        if (params.equals("losses")) return String.valueOf(Utils.manager().getInt("r." + name + ".losses"));

        if (params.equals("wins")) return String.valueOf(Utils.manager().getInt("r." + name + ".wins"));

        DuelRequest req = getDUELrequest(((Player) player).getName());
        if (params.equals("score_r")) return String.valueOf(req.getRed());

        if (params.equals("score_b")) return String.valueOf(req.getBlue());

        if (params.equals("round")) return String.valueOf(req.getRounds());

        if (params.equals("duration")) {
            SimpleDateFormat sdfDate = new SimpleDateFormat("mm:ss");
            sdfDate.setTimeZone(TimeZone.getTimeZone("UTC"));
            return sdfDate.format(new Date(System.currentTimeMillis() - (6000L - req.getStart())));
        }

        return String.valueOf(0);
    }
}