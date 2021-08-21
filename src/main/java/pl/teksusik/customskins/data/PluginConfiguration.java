package pl.teksusik.customskins.data;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.NameModifier;
import eu.okaeri.configs.annotation.NameStrategy;
import eu.okaeri.configs.annotation.Names;
import pl.teksusik.customskins.model.StorageType;

@Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.TO_LOWER_CASE)
public class PluginConfiguration extends OkaeriConfig {
    @Comment("Choose type of data storage for plugin (MYSQL, SQLITE)")
    private StorageType storageType = StorageType.MYSQL;
    @Comment("MySQL connection data")
    private String mysqlHost = "0.0.0.0";
    private int mysqlPort = 3306;
    private String mysqlDatabase = "customskins";
    private String mysqlUsername = "customskins";
    private String mysqlPassword = "customskins";
    @Comment("SQLite file name")
    private String sqliteFile = "";
}
