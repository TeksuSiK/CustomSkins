package pl.teksusik.customskins.data;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.NameModifier;
import eu.okaeri.configs.annotation.NameStrategy;
import eu.okaeri.configs.annotation.Names;
import pl.teksusik.customskins.model.StorageType;

import java.util.ArrayList;
import java.util.List;

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
    private String sqliteFile = "customskins.db";

    @Comment("Messages configuration")
    @Comment("Help commands")
    private List<String> helpCommands = new ArrayList<>() {
        {
            this.add("<yellow>CustomSkins help:");
            this.add("<green>- <reset>/skins list <gold>- <reset>Shows off skin list");
            this.add("<green>- <reset>/skins wear <name> <gold>- <reset>Dresses up skin from list");
            this.add("<green>- <reset>/skins add <name> <URL> <Model> <gold>- <reset>Creates skin from URL");
            this.add("<green>- <reset>/skins remove <name> <gold>- <reset>Removes skin from list");
            this.add("<green>- <reset>/skins version <gold>- <reset>CustomSkins info");
        }
    };
    @Comment("Skins available message")
    private String skinsAvailableMessage = "<green>Skins available:";
    @Comment("Skin not exists message")
    private String skinNotExistsMessage = "<dark_red>Error: <red>Skin with provided name does not exists";

    public StorageType getStorageType() {
        return storageType;
    }

    public String getMysqlHost() {
        return mysqlHost;
    }

    public int getMysqlPort() {
        return mysqlPort;
    }

    public String getMysqlDatabase() {
        return mysqlDatabase;
    }

    public String getMysqlUsername() {
        return mysqlUsername;
    }

    public String getMysqlPassword() {
        return mysqlPassword;
    }

    public String getSqliteFile() {
        return sqliteFile;
    }

    public List<String> getHelpCommands() {
        return helpCommands;
    }

    public String getSkinsAvailableMessage() {
        return skinsAvailableMessage;
    }

    public String getSkinNotExistsMessage() {
        return skinNotExistsMessage;
    }
}
