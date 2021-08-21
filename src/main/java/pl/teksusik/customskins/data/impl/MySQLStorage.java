package pl.teksusik.customskins.data.impl;

import com.zaxxer.hikari.HikariDataSource;
import pl.teksusik.customskins.data.Storage;

import java.sql.SQLException;

public class MySQLStorage implements Storage {
    private final HikariDataSource hikariDataSource;

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
    }

    @Override
    public HikariDataSource getHikariDataSource() {
        return hikariDataSource;
    }
}
