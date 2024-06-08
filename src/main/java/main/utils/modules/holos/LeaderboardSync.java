package main.utils.modules.holos;

import it.unimi.dsi.fastutil.Pair;
import main.utils.Instances.LeaderBoardPlayerHolder;
import org.bukkit.Bukkit;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static main.utils.Initializer.*;
import static main.utils.modules.storage.DB.connection;

public class LeaderboardSync {
    public static void sort() {
        try {
            syncLeaderboards();
        } catch (SQLException ignored) {
        }
    }

    private static void syncLeaderboards() throws SQLException {
        try (final PreparedStatement statement = connection.prepareStatement("SELECT name, ? FROM data ORDER BY ? DESC")) {
            statement.setString(1, "pd");
            statement.setString(2, "pd");
            try (final ResultSet resultSet = statement.executeQuery()) {
                syncDeaths(resultSet);
            }
            statement.setString(1, "pk");
            statement.setString(2, "pk");
            try (final ResultSet resultSet = statement.executeQuery()) {
                syncKills(resultSet);
            }
            statement.setString(1, "pt");
            statement.setString(2, "pt");
            try (final ResultSet resultSet = statement.executeQuery()) {
                syncPlaytime(resultSet);
            }
        }
    }

    private static void syncDeaths(ResultSet resultSet) throws SQLException {
        int i = 0;
        while (resultSet.next()) {
            final String name = resultSet.getString(1);
            final int deaths = resultSet.getInt(2);
            LeaderBoardPlayerHolder lb = leaderboardData.get(name);
            if (lb == null) {
                lb = new LeaderBoardPlayerHolder(0, 0, 0, 0, 0, 0);
            }
            lb.setDeaths(deaths);
            lb.setDeaths_place(i++);
            top_deaths.put(i, Pair.of(name, deaths));
            leaderboardData.put(name, lb);
        }
    }

    private static void syncKills(ResultSet resultSet) throws SQLException {
        int i = 0;
        while (resultSet.next()) {
            final String name = resultSet.getString(1);
            final int kills = resultSet.getInt(3);
            LeaderBoardPlayerHolder lb = leaderboardData.get(name);
            lb.setKills(kills);
            lb.setKills_place(i++);
            top_kills.put(i, Pair.of(name, kills));
        }
    }

    private static void syncPlaytime(ResultSet resultSet) throws SQLException {
        int i = 0;
        while (resultSet.next()) {
            final String name = resultSet.getString(1);
            final long playtime = resultSet.getLong(4);
            LeaderBoardPlayerHolder lb = leaderboardData.get(name);
            lb.setPlaytime(playtime);
            lb.setPlaytime_place(i++);
            top_playtime.put(i, Pair.of(name, playtime));
        }
    }

    public static void init() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(p, () -> {
            top_deaths.clear();
            top_kills.clear();
            top_playtime.clear();
            sort();
        }, 0L, 6000L);
    }
}
