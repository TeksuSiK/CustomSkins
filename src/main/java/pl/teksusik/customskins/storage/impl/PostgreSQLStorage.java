package pl.teksusik.customskins.storage.impl;

import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public class PostgreSQLStorage extends SQLStorage {
    public PostgreSQLStorage(String host, int port, String database, String username, String password) {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException exception) {
            exception.printStackTrace();
        }

        this.hikariDataSource = new HikariDataSource();

        this.hikariDataSource.setJdbcUrl("jdbc:postgresql://" + host + ":" + port + "/" + database);
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
    public void createTableIfNotExists() {
        try (final Connection connection = this.hikariDataSource.getConnection();
             final PreparedStatement createSkinsTable = connection.prepareStatement("CREATE TABLE IF NOT EXISTS customskins_skins (owner varchar(36) NOT NULL, name varchar(51) NOT NULL, texture text NOT NULL, signature text NOT NULL);");
             final PreparedStatement createI18nTable = connection.prepareStatement("CREATE TABLE IF NOT EXISTS customskins_i18n (owner varchar(36) NOT NULL PRIMARY KEY, locale VARCHAR(10));")) {
            createSkinsTable.executeUpdate();
            createI18nTable.executeUpdate();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }
}
