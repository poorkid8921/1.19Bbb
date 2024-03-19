package main.utils.holos;

import lombok.Getter;
import org.bukkit.Bukkit;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

import static main.commands.essentials.PlayTime.getTime;

public class SortUtils {
    static LeaderBoardHolder[] sortedKills = new LeaderBoardHolder[10];
    static LeaderBoardHolder[] sortedDeaths = new LeaderBoardHolder[10];
    static StringLeaderBoardHolder[] sortedPlaytime = new StringLeaderBoardHolder[10];

    public static void sortKills(Map<String, Integer> returnValue) {
        Map<String, Integer> sortedMap = new TreeMap<>(Comparator.comparing(returnValue::get));
        sortedMap.putAll(returnValue);
        int iterations = 0;
        for (String key : sortedMap.keySet()) {
            sortedKills[iterations++] = new LeaderBoardHolder(key, sortedMap.get(key));
            Bukkit.getLogger().warning("#" + iterations++ + " | " + key + ": " + sortedMap.get(key));
        }
    }

    public static void sortDeaths(Map<String, Integer> returnValue) {
        Map<String, Integer> sortedMap = new TreeMap<>(Comparator.comparing(returnValue::get));
        sortedMap.putAll(returnValue);
        int iterations = 0;
        for (String key : sortedMap.keySet()) {
            sortedDeaths[iterations++] = new LeaderBoardHolder(key, sortedMap.get(key));
            Bukkit.getLogger().warning("#" + iterations++ + " | " + key + ": " + sortedMap.get(key));
        }
    }

    public static void sortPlaytime(Map<String, Long> returnValue) {
        Map<String, Long> sortedMap = new TreeMap<>(Comparator.comparing(returnValue::get));
        sortedMap.putAll(returnValue);
        int iterations = 0;
        for (String key : sortedMap.keySet()) {
            sortedPlaytime[iterations++] = new StringLeaderBoardHolder(key, getTime(sortedMap.get(key)));
            Bukkit.getLogger().warning("#" + iterations++ + " | " + key + ": " + sortedMap.get(key));
        }
    }

    @Getter
    static class StringLeaderBoardHolder {
        private final String key;
        private final String value;

        private StringLeaderBoardHolder(String key, String value) {
            this.key = key;
            this.value = value;
        }
    }

    @Getter
    static class LeaderBoardHolder {
        private final String key;
        private final int value;

        private LeaderBoardHolder(String key, int value) {
            this.key = key;
            this.value = value;
        }
    }
}
