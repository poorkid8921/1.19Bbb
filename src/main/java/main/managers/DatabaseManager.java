package main.managers;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import main.managers.instances.PlayerDataHolder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseManager {
    private Connection connection;

    public DatabaseManager() {
        try {
            final HikariConfig config = new HikariConfig();
            config.setDriverClassName("org.mariadb.jdbc.Driver");
            config.setJdbcUrl("jdbc:mariadb://localhost:3306/CATSMP");
            config.setMaximumPoolSize(20);
            config.setUsername("root");
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
            connection = new HikariDataSource(config).getConnection();
        } catch (SQLException ignored) {
        }
    }

    public PreparedStatement prepareStatement(String sql) throws SQLException {
        return connection.prepareStatement(sql);
    }

    public int setData(String name, PlayerDataHolder D0) {
        try (final PreparedStatement statement = connection.prepareStatement("SELECT * FROM data WHERE name = ?")) {
            statement.setString(1, name);
            try (final ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    final int rank = resultSet.getInt(3);
                    D0.setRank(rank);
                    D0.setFastCrystals(resultSet.getBoolean(14));
                    return rank;
                }
            }
        } catch (SQLException ignored) {
        }
        return 0;
    }

    public void setRank(String name, int rank) {
        try (final PreparedStatement statement = connection.prepareStatement("UPDATE data SET rank = ? WHERE name = ?")) {
            statement.setInt(1, rank);
            statement.setString(2, name);
            statement.executeUpdate();
        } catch (SQLException ignored) {
        }
    }
}
