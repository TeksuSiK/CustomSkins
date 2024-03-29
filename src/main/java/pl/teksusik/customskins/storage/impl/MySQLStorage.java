package pl.teksusik.customskins.storage.impl;

import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public class MySQLStorage extends SQLStorage {
    public MySQLStorage(String host, int port, String database, String username, String password) {
        this.hikariDataSource = new HikariDataSource();

        this.hikariDataSource.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database);
        this.hikariDataSource.setUsername(username);
        this.hikariDataSource.setPassword(password);
        this.hikariDataSource.setAutoCommit(true);

        this.hikariDataSource.addDataSourceProperty("cachePrepStmts", true);
        this.hikariDataSource.addDataSourceProperty("prepStmtCacheSize", 250);
        this.hikariDataSource.addDataSourceProperty("prepStmtCacheSqlLimit", 2048);
        this.hikariDataSource.addDataSourceProperty("useServerPrepStmts", true);
        this.hikariDataSource.addDataSourceProperty("useLocalSessionState", true);
        this.hikariDataSource.addDataSourceProperty("rewriteBatchedStatements", true);
        this.hikariDataSource.addDataSourceProperty("cacheResultSetMetadata", true);
        this.hikariDataSource.addDataSourceProperty("cacheServerConfiguration", true);
        this.hikariDataSource.addDataSourceProperty("elideSetAutoCommits", true);
        this.hikariDataSource.addDataSourceProperty("maintainTimeStats", false);
        this.hikariDataSource.addDataSourceProperty("autoClosePStmtStreams", true);
        this.hikariDataSource.addDataSourceProperty("useSSL", false);
        this.hikariDataSource.addDataSourceProperty("serverTimezone", "UTC");

        try {
            hikariDataSource.getConnection();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }

        this.createTableIfNotExists();
    }

    @Override
    public String setLocale(UUID owner, String locale) {
        try (Connection connection = this.hikariDataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO customskins_i18n VALUES (?, ?) ON DUPLICATE KEY UPDATE locale = ?")) {
            preparedStatement.executeUpdate();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }

        return locale;
    }
}
