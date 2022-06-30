package pl.teksusik.customskins.configuration;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.Exclude;
import eu.okaeri.configs.annotation.NameModifier;
import eu.okaeri.configs.annotation.NameStrategy;
import eu.okaeri.configs.annotation.Names;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import pl.teksusik.customskins.storage.StorageType;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.TO_LOWER_CASE)
public class MessageConfiguration extends OkaeriConfig {
    @Exclude
    private final MiniMessage miniMessage = MiniMessage.miniMessage();

    @Comment("Messages configuration")
    @Comment("Help commands")
    private List<Component> helpCommands = new ArrayList<>() {
        {
            this.add(miniMessage.deserialize("<yellow>CustomSkins help:"));
            this.add(miniMessage.deserialize("<green>- <reset>/skins list <gold>- <reset>Shows off skin list"));
            this.add(miniMessage.deserialize("<green>- <reset>/skins wear <name> <gold>- <reset>Dresses up skin from list"));
            this.add(miniMessage.deserialize("<green>- <reset>/skins add <name> <URL> <Model> <gold>- <reset>Creates skin from URL"));
            this.add(miniMessage.deserialize("<green>- <reset>/skins delete <name> <gold>- <reset>Removes skin from list"));
            this.add(miniMessage.deserialize("<green>- <reset>/skins version <gold>- <reset>CustomSkins info"));
        }
    };
    @Comment("Skins available message")
    private Component skinsAvailable = this.miniMessage.deserialize("<green>Skins available:");
    @Comment("Skin not exists message")
    private Component skinNotExists = this.miniMessage.deserialize("<dark_red>Error: <red>Skin with provided name does not exists");
    @Comment("Skin changed message")
    private Component skinChanged = this.miniMessage.deserialize("<green>You successfully changed your skin");
    @Comment("Skin deleted message")
    private Component skinDeleted = this.miniMessage.deserialize("<green>You successfully deleted your skin");
    @Comment("Invalid model message")
    private Component invalidModel = this.miniMessage.deserialize("<dark_red>Error: <red>Model you provided does not exists. Valid models: DEFAULT, SLIM");
    @Comment("Skin already exists")
    private Component skinAlreadyExists = this.miniMessage.deserialize("<dark_red>Error: <red>Skin with provided name already exists");
    @Comment("Bad usage message")
    private Component badUsage = this.miniMessage.deserialize("<dark_red>Error: <red>Incorrect usage. Correct usage: /skins help");
    @Comment("Upload skin message")
    private Component skinUploaded = this.miniMessage.deserialize("<green>You successfully uploaded your skin");

    public List<Component> getHelpCommands() {
        return helpCommands;
    }

    public Component getSkinsAvailable() {
        return skinsAvailable;
    }

    public Component getSkinNotExists() {
        return skinNotExists;
    }

    public Component getSkinChanged() {
        return skinChanged;
    }

    public Component getSkinDeleted() {
        return skinDeleted;
    }

    public Component getInvalidModel() {
        return invalidModel;
    }

    public Component getSkinAlreadyExists() {
        return skinAlreadyExists;
    }

    public Component getBadUsage() {
        return badUsage;
    }

    public Component getSkinUploaded() {
        return skinUploaded;
    }

    @Exclude
    public static final Pattern LEGACY_COLOR_CODE_PATTERN = Pattern.compile("&([0-9A-Fa-fK-Ok-oRXrx][^&]*)");

    public static boolean containsLegacyColors(String string) {
        return LEGACY_COLOR_CODE_PATTERN.matcher(string).find();
    }
}
