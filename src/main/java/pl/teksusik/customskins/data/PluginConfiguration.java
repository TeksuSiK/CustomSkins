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
            this.add("<green>- <reset>/skins delete <name> <gold>- <reset>Removes skin from list");
            this.add("<green>- <reset>/skins version <gold>- <reset>CustomSkins info");
        }
    };
    @Comment("Skins available message")
    private String skinsAvailableMessage = "<green>Skins available:";
    @Comment("Skin not exists message")
    private String skinNotExistsMessage = "<dark_red>Error: <red>Skin with provided name does not exists";
    @Comment("Skin changed message")
    private String skinChangedMessage = "<green>You successfully changed your skin";
    @Comment("Skin deleted message")
    private String skinDeletedMessage = "<green>You successfully deleted your skin";
    @Comment("Invalid model message")
    private String invalidModelMessage = "<dark_red>Error: <red>Model you provided does not exists. Valid models: DEFAULT, SLIM";
    @Comment("Skin already exists")
    private String skinAlreadyExists = "<dark_red>Error: <red>Skin with provided name already exists";
    @Comment("Bad ussage message")
    private String badUsageMessage = "<dark_red>Error: <red>Incorrect usage. Correct usage: /skins help";

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

    public String getSkinChangedMessage() {
        return skinChangedMessage;
    }

    public String getSkinDeletedMessage() {
        return skinDeletedMessage;
    }

    public String getInvalidModelMessage() {
        return invalidModelMessage;
    }

    public String getSkinAlreadyExists() {
        return skinAlreadyExists;
    }

    public String getBadUsageMessage() {
        return badUsageMessage;
    }
}
