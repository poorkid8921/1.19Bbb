package main.utils.storage;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.Bukkit;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DB {
    private static Connection rankConnection;
    private static Connection mainConnection;

    public static void init() {
        try {
            HikariConfig config = new HikariConfig();
            config.setDriverClassName("org.mariadb.jdbc.Driver");
            config.setJdbcUrl("jdbc:mariadb://localhost:3306/GLOBAL");
            config.setMaximumPoolSize(20);
            config.setMaxLifetime(250);
            config.setUsername("root");
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
            rankConnection = new HikariDataSource(config).getConnection();
            Statement statement = rankConnection.createStatement();
            statement.execute("CREATE TABLE IF NOT EXISTS data (name varchar(36) primary key, rank int)");
            statement.close();

            /*config.setJdbcUrl("jdbc:mariadb://localhost:3306/PRAC?user=root");
            mainConnection = new HikariDataSource(config).getConnection();
            statement = mainConnection.createStatement();
            statement.execute("CREATE TABLE IF NOT EXISTS data (name varchar(36) primary key, rank int)");
            statement.close();*/
        } catch (SQLException ignored) {
            Bukkit.getLogger().warning("Failed to run the database.");
        }
    }
}
