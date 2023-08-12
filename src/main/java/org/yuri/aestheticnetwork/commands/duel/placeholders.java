package org.yuri.aestheticnetwork.commands.duel;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.yuri.aestheticnetwork.AestheticNetwork;
import org.yuri.aestheticnetwork.utils.Utils;

public class placeholders extends PlaceholderExpansion {

    AestheticNetwork pl;
    public placeholders(AestheticNetwork ad)
    {
        pl = ad;
    }

    @Override
    public String getAuthor() {
        return "Catto69420";
    }

    @Override
    public String getIdentifier() {
        return "duels";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public boolean persist() {
        return true; // This is required or else PlaceholderAPI will unregister the Expansion on reload
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {
        if (player == null)
            return String.valueOf(0);

        if (params.equals("losses"))
            return String.valueOf(Utils.manager().getInt("r." + player.getUniqueId() + ".losses"));

        if (params.equals("wins"))
            return String.valueOf(Utils.manager().getInt("r." + player.getUniqueId() + ".wins"));

        return String.valueOf(0);
    }
}