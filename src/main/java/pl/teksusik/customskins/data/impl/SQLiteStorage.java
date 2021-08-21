package pl.teksusik.customskins.data.impl;

import com.zaxxer.hikari.HikariDataSource;
import pl.teksusik.customskins.data.Storage;

import java.sql.SQLException;

public class SQLiteStorage implements Storage {
    private final HikariDataSource hikariDataSource;

    public SQLiteStorage(String file) {
        this.hikariDataSource = new HikariDataSource();
        this.hikariDataSource.setJdbcUrl("jdbc:sqlite:" + file);

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
