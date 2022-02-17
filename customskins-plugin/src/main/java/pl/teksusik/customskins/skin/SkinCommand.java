package pl.teksusik.customskins.skin;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import pl.teksusik.customskins.configuration.PluginConfiguration;
import pl.teksusik.customskins.libs.mineskin.SkinOptions;
import pl.teksusik.customskins.libs.mineskin.Variant;
import pl.teksusik.customskins.libs.mineskin.Visibility;

@CommandAlias("skin|skins|customskins")
public class SkinCommand extends BaseCommand {
    private final PluginConfiguration pluginConfiguration;
    private final SkinService skinService;
    private final BukkitAudiences adventure;

    public SkinCommand(PluginConfiguration pluginConfiguration, SkinService skinService, BukkitAudiences adventure) {
        this.pluginConfiguration = pluginConfiguration;
        this.skinService = skinService;
        this.adventure = adventure;
    }

    @Default
    public void onDefault(Player player) {
        for (Component message : this.pluginConfiguration.getHelpCommands())
            this.adventure.player(player).sendMessage(message);
    }

    @Subcommand("list")
    public void onList(Player player) {
        this.adventure.player(player).sendMessage(this.pluginConfiguration.getSkinsAvailable());
        for (CustomSkin customSkin : this.skinService.getSkins(player)) {
            this.adventure.player(player).sendMessage(Component.text("- " + customSkin.getName()));
        }
    }

    @Subcommand("wear")
    @Syntax("<name>")
    public void onWear(Player player, String[] args) {
        if (args.length != 1) {
            this.adventure.player(player).sendMessage(this.pluginConfiguration.getBadUsage());
            return;
        }

        this.skinService.getSkin(player, args[0]).ifPresentOrElse(customSkin -> {
            this.skinService.setSkin(player, customSkin);
            this.adventure.player(player).sendMessage(this.pluginConfiguration.getSkinChanged());
        }, () -> this.adventure.player(player).sendMessage(this.pluginConfiguration.getSkinNotExists()));
    }

    @Subcommand("add")
    @Syntax("<name> <url> <model>")
    public void onAdd(Player player, String[] args) {
        if (args.length != 3) {
            this.adventure.player(player).sendMessage(this.pluginConfiguration.getBadUsage());
            return;
        }

        String name = args[0];
        if (this.skinService.getSkin(player, name).isPresent()) {
            this.adventure.player(player).sendMessage(this.pluginConfiguration.getSkinAlreadyExists());
            return;
        }

        Variant variant;
        try {
            variant = Variant.valueOf(args[2].toUpperCase());
        } catch (IllegalArgumentException exception) {
            this.adventure.player(player).sendMessage(this.pluginConfiguration.getInvalidModel());
            return;
        }

        this.skinService.uploadSkin(player, name, args[1], SkinOptions.create(name, variant, Visibility.PRIVATE))
            .thenAccept(action -> this.adventure.player(player).sendMessage(this.pluginConfiguration.getSkinUploaded()));
    }

    @Subcommand("delete")
    @Syntax("<name>")
    public void onDelete(Player player, String[] args) {
        if (args.length != 1) {
            this.adventure.player(player).sendMessage(this.pluginConfiguration.getBadUsage());
            return;
        }

        this.skinService.getSkin(player, args[0]).ifPresentOrElse(customSkin -> {
            this.skinService.deleteSkin(customSkin);
            this.adventure.player(player).sendMessage(this.pluginConfiguration.getSkinDeleted());
        }, () -> this.adventure.player(player).sendMessage(this.pluginConfiguration.getSkinNotExists()));
    }

    @Subcommand("version")
    public void onVersion(Player player) {
        this.adventure.player(player).sendMessage(Component.text("CustomSkins v1.2 by teksusik."));
    }
}
