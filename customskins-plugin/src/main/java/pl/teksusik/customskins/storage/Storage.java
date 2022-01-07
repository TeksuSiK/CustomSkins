package pl.teksusik.customskins.storage;

import com.zaxxer.hikari.HikariDataSource;

public interface Storage {
    HikariDataSource getHikariDataSource();
}
