package pl.teksusik.customskins.data;

import com.zaxxer.hikari.HikariDataSource;

public interface Storage {
    HikariDataSource getHikariDataSource();
}
