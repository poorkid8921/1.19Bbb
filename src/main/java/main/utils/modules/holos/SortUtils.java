package main.utils.modules.holos;

import lombok.Getter;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

import static main.utils.Utils.getTime;
import static main.utils.modules.storage.DB.connection;

public class SortUtils {
    static LeaderBoardHolder[] sortedKills = new LeaderBoardHolder[10];
    static LeaderBoardHolder[] sortedDeaths = new LeaderBoardHolder[10];
    static StringLeaderBoardHolder[] sortedPlaytime = new StringLeaderBoardHolder[10];

    public static void sortKills() {
        int i = -1;
        try (final PreparedStatement statement = connection.prepareStatement("SELECT ROW_NUMBER() OVER ( ORDER BY pd DESC ) name, pk FROM data LIMIT 10;")) {
            try (final ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    sortedKills[i++] = new LeaderBoardHolder(resultSet.getString(1), resultSet.getInt(2));
                }
            }
        } catch (SQLException ignored) {
        }
    }

    public static void sortDeaths() {
        int i = -1;
        try (final PreparedStatement statement = connection.prepareStatement("SELECT ROW_NUMBER() OVER ( ORDER BY pd DESC ) name, pd FROM data LIMIT 10;")) {
            try (final ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    sortedDeaths[i++] = new LeaderBoardHolder(resultSet.getString(1), resultSet.getInt(2));
                }
            }
        } catch (SQLException ignored) {
        }
    }

    public static void sortPlaytime(Map<String, Long> returnValue) {
        Map<String, Long> sortedMap = new TreeMap<>(Comparator.comparing(returnValue::get));
        sortedMap.putAll(returnValue);
        int i = 0;
        for (String key : sortedMap.keySet()) {
            sortedPlaytime[i++] = new StringLeaderBoardHolder(key, getTime(sortedMap.get(key)));
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