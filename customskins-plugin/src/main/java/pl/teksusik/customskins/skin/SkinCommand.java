package pl.teksusik.customskins.skin;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import pl.teksusik.customskins.configuration.MessageConfiguration;
import pl.teksusik.customskins.libs.mineskin.SkinOptions;
import pl.teksusik.customskins.libs.mineskin.Variant;
import pl.teksusik.customskins.libs.mineskin.Visibility;
import pl.teksusik.customskins.storage.Storage;

@CommandAlias("skin|skins|customskins")
public class SkinCommand extends BaseCommand {
    private final MessageConfiguration messageConfiguration;
    private final Storage skinStorage;
    private final SkinService skinService;
    private final BukkitAudiences adventure;

    public SkinCommand(MessageConfiguration messageConfiguration, Storage skinStorage, SkinService skinService, BukkitAudiences adventure) {
        this.messageConfiguration = messageConfiguration;
        this.skinStorage = skinStorage;
        this.skinService = skinService;
        this.adventure = adventure;
    }

    @Default
    public void onDefault(Player player) {
        for (Component message : this.messageConfiguration.getHelpCommands())
            this.adventure.player(player).sendMessage(message);
    }

    @Subcommand("list")
    public void onList(Player player) {
        this.adventure.player(player).sendMessage(this.messageConfiguration.getSkinsAvailable());
        for (CustomSkin customSkin : this.skinStorage.getAllSkinsByOwner(player.getUniqueId())) {
            this.adventure.player(player).sendMessage(Component.text("- " + customSkin.getName()));
        }
    }

    @Subcommand("wear")
    @Syntax("<name>")
    public void onWear(Player player, String[] args) {
        if (args.length != 1) {
            this.adventure.player(player).sendMessage(this.messageConfiguration.getBadUsage());
            return;
        }

        this.skinStorage.findSkin(player.getUniqueId(), args[0]).ifPresentOrElse(customSkin -> {
            this.skinService.setSkin(player, customSkin);
            this.adventure.player(player).sendMessage(this.messageConfiguration.getSkinChanged());
        }, () -> this.adventure.player(player).sendMessage(this.messageConfiguration.getSkinNotExists()));
    }

    @Subcommand("add")
    @Syntax("<name> <url> <model>")
    public void onAdd(Player player, String[] args) {
        if (args.length != 3) {
            this.adventure.player(player).sendMessage(this.messageConfiguration.getBadUsage());
            return;
        }

        String name = args[0];
        if (this.skinStorage.findSkin(player.getUniqueId(), name).isPresent()) {
            this.adventure.player(player).sendMessage(this.messageConfiguration.getSkinAlreadyExists());
            return;
        }

        Variant variant;
        try {
            variant = Variant.valueOf(args[2].toUpperCase());
        } catch (IllegalArgumentException exception) {
            this.adventure.player(player).sendMessage(this.messageConfiguration.getInvalidModel());
            return;
        }

        this.skinService.uploadSkin(player, name, args[1], SkinOptions.create(name, variant, Visibility.PRIVATE))
            .thenAccept(action -> this.adventure.player(player).sendMessage(this.messageConfiguration.getSkinUploaded()));
    }

    @Subcommand("delete")
    @Syntax("<name>")
    public void onDelete(Player player, String[] args) {
        if (args.length != 1) {
            this.adventure.player(player).sendMessage(this.messageConfiguration.getBadUsage());
            return;
        }

        this.skinStorage.findSkin(player.getUniqueId(), args[0]).ifPresentOrElse(customSkin -> {
            this.skinStorage.deleteSkin(customSkin);
            this.adventure.player(player).sendMessage(this.messageConfiguration.getSkinDeleted());
        }, () -> this.adventure.player(player).sendMessage(this.messageConfiguration.getSkinNotExists()));
    }

    @Subcommand("version")
    public void onVersion(Player player) {
        this.adventure.player(player).sendMessage(Component.text("CustomSkins 1.3-RELEASE by teksusik."));
    }
}
