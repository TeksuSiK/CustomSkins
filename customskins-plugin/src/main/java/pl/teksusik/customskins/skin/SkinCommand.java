package pl.teksusik.customskins.skin;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.entity.Player;
import pl.teksusik.customskins.configuration.PluginConfiguration;
import pl.teksusik.customskins.libs.mineskin.SkinOptions;
import pl.teksusik.customskins.libs.mineskin.Variant;
import pl.teksusik.customskins.libs.mineskin.Visibility;
import pl.teksusik.customskins.util.ChatHelper;

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
        for (String message : this.pluginConfiguration.getHelpCommands())
            this.adventure.player(player).sendMessage(ChatHelper.getAsComponent(message));
    }

    @Subcommand("list")
    public void onList(Player player) {
        this.adventure.player(player).sendMessage(ChatHelper.getAsComponent(this.pluginConfiguration.getSkinsAvailableMessage()));
        for (CustomSkin customSkin : this.skinService.getSkins(player)) {
            this.adventure.player(player).sendMessage(ChatHelper.getAsComponent("<green> - <reset>" + customSkin.getName()));
        }
    }

    @Subcommand("wear")
    @Syntax("<name>")
    public void onWear(Player player, String[] args) {
        if (args.length != 1) {
            this.adventure.player(player).sendMessage(ChatHelper.getAsComponent(this.pluginConfiguration.getBadUsageMessage()));
            return;
        }

        this.skinService.getSkin(player, args[0]).ifPresentOrElse(customSkin -> {
            this.skinService.setSkin(player, customSkin);
            this.adventure.player(player).sendMessage(ChatHelper.getAsComponent(this.pluginConfiguration.getSkinChangedMessage()));
        }, () -> this.adventure.player(player).sendMessage(ChatHelper.getAsComponent(this.pluginConfiguration.getSkinNotExistsMessage())));
    }

    @Subcommand("add")
    @Syntax("<name> <url> <model>")
    public void onAdd(Player player, String[] args) {
        if (args.length != 3) {
            this.adventure.player(player).sendMessage(ChatHelper.getAsComponent(this.pluginConfiguration.getBadUsageMessage()));
            return;
        }

        String name = args[0];
        if (skinService.getSkin(player, name).isPresent()) {
            this.adventure.player(player).sendMessage(ChatHelper.getAsComponent(this.pluginConfiguration.getSkinAlreadyExists()));
            return;
        }

        Variant variant;
        try {
            variant = Variant.valueOf(args[2].toUpperCase());
        } catch (IllegalArgumentException exception) {
            this.adventure.player(player).sendMessage(ChatHelper.getAsComponent(this.pluginConfiguration.getInvalidModelMessage()));
            return;
        }

        skinService.uploadSkin(player, name, args[1], SkinOptions.create(name, variant, Visibility.PRIVATE));
    }

    @Subcommand("delete")
    @Syntax("<name>")
    public void onDelete(Player player, String[] args) {
        if (args.length != 1) {
            this.adventure.player(player).sendMessage(ChatHelper.getAsComponent(this.pluginConfiguration.getBadUsageMessage()));
            return;
        }

        this.skinService.getSkin(player, args[0]).ifPresentOrElse(customSkin -> {
            this.skinService.deleteSkin(customSkin);
            this.adventure.player(player).sendMessage(ChatHelper.getAsComponent(this.pluginConfiguration.getSkinDeletedMessage()));
        }, () -> this.adventure.player(player).sendMessage(ChatHelper.getAsComponent(this.pluginConfiguration.getSkinNotExistsMessage())));
    }

    @Subcommand("version")
    public void onVersion(Player player) {
        this.adventure.player(player).sendMessage(ChatHelper.getAsComponent("CustomSkins v1.0 by teksusik."));
    }
}
