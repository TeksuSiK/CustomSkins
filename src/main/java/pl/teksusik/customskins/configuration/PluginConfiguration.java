package pl.teksusik.customskins.configuration;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.NameModifier;
import eu.okaeri.configs.annotation.NameStrategy;
import eu.okaeri.configs.annotation.Names;
import pl.teksusik.customskins.i18n.locale.LocaleProviderType;
import pl.teksusik.customskins.storage.StorageType;

@Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.TO_LOWER_CASE)
public class PluginConfiguration extends OkaeriConfig {
    @Comment("Choose type of data storage for plugin (MYSQL, POSTGRESQL, SQLITE, MONGODB)")
    private StorageType storageType = StorageType.MYSQL;
    @Comment("Database connection data")
    private String host = "0.0.0.0";
    private int port = 3306;
    private String database = "customskins";
    private String username = "customskins";
    private String password = "customskins";
    @Comment("SQLite file name")
    private String sqliteFile = "customskins.db";
    @Comment("Choose locale provider for messages (FIXED, PLAYER)")
    private LocaleProviderType localeProvider = LocaleProviderType.PLAYER;
    @Comment("Choose messages language. Only applicable if locale-provider is set to FIXED")
    private String locale = "en";

    public StorageType getStorageType() {
        return storageType;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getDatabase() {
        return database;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getSqliteFile() {
        return sqliteFile;
    }

    public LocaleProviderType getLocaleProvider() {
        return localeProvider;
    }

    public String getLocale() {
        return locale;
    }
}
