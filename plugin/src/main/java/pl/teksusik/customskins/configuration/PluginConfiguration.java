package pl.teksusik.customskins.configuration;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.Exclude;
import eu.okaeri.configs.annotation.NameModifier;
import eu.okaeri.configs.annotation.NameStrategy;
import eu.okaeri.configs.annotation.Names;
import pl.teksusik.customskins.storage.StorageType;

import java.util.regex.Pattern;

@Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.TO_LOWER_CASE)
public class PluginConfiguration extends OkaeriConfig {
    @Comment("Choose type of data storage for plugin (MYSQL, SQLITE, MONGODB)")
    private StorageType storageType = StorageType.MYSQL;
    @Comment("Database connection data")
    private String host = "0.0.0.0";
    private int port = 3306;
    private String database = "customskins";
    private String username = "customskins";
    private String password = "customskins";
    @Comment("SQLite file name")
    private String sqliteFile = "customskins.db";

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

    @Exclude
    public static final Pattern LEGACY_COLOR_CODE_PATTERN = Pattern.compile("&([0-9A-Fa-fK-Ok-oRXrx][^&]*)");

    public static boolean containsLegacyColors(String string) {
        return LEGACY_COLOR_CODE_PATTERN.matcher(string).find();
    }
}
