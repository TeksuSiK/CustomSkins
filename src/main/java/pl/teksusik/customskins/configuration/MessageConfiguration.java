package pl.teksusik.customskins.configuration;

import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.NameModifier;
import eu.okaeri.configs.annotation.NameStrategy;
import eu.okaeri.configs.annotation.Names;
import eu.okaeri.i18n.configs.LocaleConfig;

@Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.TO_LOWER_CASE)
public class MessageConfiguration extends LocaleConfig {
    @Comment("Messages configuration")
    private String helpCommands = "<yellow>CustomSkins help:" +
        "\n<green>- <reset>/skins list <gold>- <reset>Shows off skin list" +
        "\n<green>- <reset>/skins wear <name> <gold>- <reset>Dresses up skin from list" +
        "\n<green>- <reset>/skins add <name> <URL> <Model> <gold>- <reset>Creates skin from URL" +
        "\n<green>- <reset>/skins delete <name> <gold>- <reset>Removes skin from list";
    @Comment("Skins available message")
    private String skinsAvailable = "<green>Skins available: <reset>{skins}";
    @Comment("Skin not exists message")
    private String skinNotExists = "<dark_red>Error: <red>Skin named <reset>{skin} <red>does not exists";
    @Comment("Skin changed message")
    private String skinChanged = "<green>You successfully changed your skin to <reset>{skin}";
    @Comment("Skin deleted message")
    private String skinDeleted = "<green>You successfully deleted <reset>{skin} <green>skin";
    @Comment("Invalid model message")
    private String invalidModel = "<dark_red>Error: <red>Model <reset>{model} <red>does not exists. Valid models: DEFAULT, SLIM";
    @Comment("Skin already exists")
    private String skinAlreadyExists = "<dark_red>Error: <red>Skin named <reset>{skin} <red>already exists";
    @Comment("Bad usage message")
    private String badUsage = "<dark_red>Error: <red>Incorrect usage. Check correct usage: <reset>/skins";
    @Comment("Upload skin message")
    private String skinUploaded = "<green>You successfully uploaded <reset>{skin} <green>skin";

    public String getHelpCommands() {
        return helpCommands;
    }

    public String getSkinsAvailable() {
        return skinsAvailable;
    }

    public String getSkinNotExists() {
        return skinNotExists;
    }

    public String getSkinChanged() {
        return skinChanged;
    }

    public String getSkinDeleted() {
        return skinDeleted;
    }

    public String getInvalidModel() {
        return invalidModel;
    }

    public String getSkinAlreadyExists() {
        return skinAlreadyExists;
    }

    public String getBadUsage() {
        return badUsage;
    }

    public String getSkinUploaded() {
        return skinUploaded;
    }
}
