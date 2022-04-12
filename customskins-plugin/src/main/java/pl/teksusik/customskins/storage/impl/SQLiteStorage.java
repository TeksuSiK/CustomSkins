package pl.teksusik.customskins.storage.impl;

import com.zaxxer.hikari.HikariDataSource;

import java.io.File;
import java.sql.SQLException;

public class SQLiteStorage extends SQLStorage {
    public SQLiteStorage(File file) {
        this.hikariDataSource = new HikariDataSource();
        this.hikariDataSource.setJdbcUrl("jdbc:sqlite:" + file.getAbsolutePath());

        try {
            hikariDataSource.getConnection();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }

        this.createTableIfNotExists();
    }
}
