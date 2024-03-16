package main.utils.storage;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import main.utils.instances.CustomPlayerDataHolder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DB {
    public static Connection connection;

    public static void init() {
        try {
            HikariConfig config = new HikariConfig();
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

    public static int setUsefulData(String name, CustomPlayerDataHolder D0) {
        try (PreparedStatement statement = connection.prepareStatement("SELECT rank,fc FROM data WHERE name = ?")) {
            statement.setString(1, name);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int rank = resultSet.getInt(1);
                    D0.setRank(rank);
                    D0.setFastCrystals(resultSet.getBoolean(2));
                    return rank;
                }
            }
        } catch (SQLException ignored) {
        }
        return 0;
    }

    public static void setRank(String name, int rank) {
        try (PreparedStatement statement = connection.prepareStatement("UPDATE data SET rank = ? WHERE name = ?")) {
            statement.setInt(1, rank);
            statement.setString(2, name);
            statement.executeUpdate();
        } catch (SQLException ignored) {
        }
    }
}
