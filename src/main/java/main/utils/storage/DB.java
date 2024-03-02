package main.utils.storage;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

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

    public static int parseRank(String name) {
        try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM data WHERE name = ?")) {
            statement.setString(1, name);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? resultSet.getInt(3) : 0;
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
