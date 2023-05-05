package bab.bbb.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import bab.bbb.Bbb;
import org.bukkit.configuration.ConfigurationSection;

public class DataStore {
    private static Bbb plugin = Bbb.getInstance();
    private static Set<String> playerList = new HashSet<>();

    public DataStore(Bbb bbb) {
    }

    public static synchronized void generatePlayerList() {
        playerList.clear();

        ConfigurationSection ipConfSect = plugin.getCustomConfig().getConfigurationSection("ip");
        if (ipConfSect != null) {
            for (String ip : ipConfSect.getKeys(false)) {
                Set<String> uuidKeys = plugin.getCustomConfig().getConfigurationSection("ip." + ip).getKeys(false);

                for (String uuid : uuidKeys) {
                    String uuidData = plugin.getCustomConfig().getString("ip." + ip + "." + uuid);
                    String[] arg = uuidData.split(",");
                    playerList.add(arg[1].toLowerCase());
                }
            }
        }
    }

    public static synchronized void purge(String name) {
        List<String> removeList = new ArrayList<String>();
        Date oldestDate = new Date(System.currentTimeMillis() - 8640000);

        ConfigurationSection ipConfSect = plugin.getCustomConfig().getConfigurationSection("ip");
        if (ipConfSect != null) {
            for (String ip : ipConfSect.getKeys(false)) {
                Set<String> uuidKeys = plugin.getCustomConfig().getConfigurationSection("ip." + ip).getKeys(false);
                int remainingKeys = uuidKeys.size();

                for (String uuid : uuidKeys) {
                    String uuidData = plugin.getCustomConfig().getString("ip." + ip + "." + uuid);
                    String[] arg = uuidData.split(",");
                    Date date = new Date(Long.parseLong(arg[0]));

                    if ((name.equals("") && date.before(oldestDate)) ||
                            (name.equalsIgnoreCase(arg[1]))) {
                        removeList.add("ip." + ip + "." + uuid);
                        --remainingKeys;
                        playerList.remove(arg[1].toLowerCase());
                    }
                }

                if (remainingKeys <= 0) {
                    removeList.add("ip." + ip);
                }
            }
        }

        for (String key : removeList)
            plugin.getCustomConfig().set(key, null);

        plugin.saveCustomConfig();
    }

    public static synchronized void addUpdateIp(String ip, String uuid, String name) {
        Date date = new Date();
        plugin.getCustomConfig().set("ip." + ip.replace('.', '_') + "." + uuid, date.getTime() + "," + name);
        playerList.add(name.toLowerCase());
        plugin.saveCustomConfig();
    }

    private static synchronized List<String> getAltNames(String ip, String excludeUuid) {
        List<String> altList = new ArrayList<String>();

        Date oldestDate = new Date(System.currentTimeMillis() - 8640000);

        ConfigurationSection ipIpConfSect = plugin.getCustomConfig().getConfigurationSection("ip." + ip.replace('.', '_'));
        if (ipIpConfSect != null) {
            for (String uuid : ipIpConfSect.getKeys(false)) {
                String uuidData = plugin.getCustomConfig().getString("ip." + ip.replace('.', '_') + "." + uuid);
                String[] arg = uuidData.split(","); // arg[0]=date, arg[1]=name
                Date date = new Date(Long.parseLong(arg[0]));

                if (!uuid.equals(excludeUuid) && date.after(oldestDate))
                    altList.add(arg[1]);
            }
        }

        altList.sort(String.CASE_INSENSITIVE_ORDER);
        return altList;
    }

    public static String getFormattedAltString(String ip, String uuid) {
        List<String> altList = getAltNames(ip, uuid);

        if (!altList.isEmpty())
            return "true";
        return null;
    }
}