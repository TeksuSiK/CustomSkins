package pl.teksusik.customskins.storage.impl;

import com.zaxxer.hikari.HikariDataSource;
import pl.teksusik.customskins.storage.Storage;

import java.io.File;
import java.sql.SQLException;

public class SQLiteStorage implements Storage {
    private final HikariDataSource hikariDataSource;

    public SQLiteStorage(File file) {
        this.hikariDataSource = new HikariDataSource();
        this.hikariDataSource.setJdbcUrl("jdbc:sqlite:" + file.getAbsolutePath());

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
