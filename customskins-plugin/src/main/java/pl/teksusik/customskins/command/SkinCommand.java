package pl.teksusik.customskins.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.entity.Player;
import pl.teksusik.customskins.data.PluginConfiguration;
import pl.teksusik.customskins.libs.mineskin.SkinOptions;
import pl.teksusik.customskins.libs.mineskin.Variant;
import pl.teksusik.customskins.libs.mineskin.Visibility;
import pl.teksusik.customskins.model.CustomSkin;
import pl.teksusik.customskins.service.SkinService;
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
            adventure.player(player).sendMessage(ChatHelper.getAsComponent(message));
    }

    @Subcommand("list")
    public void onList(Player player) {
        adventure.player(player).sendMessage(ChatHelper.getAsComponent(this.pluginConfiguration.getSkinsAvailableMessage()));
        for (CustomSkin customSkin : this.skinService.getSkins(player)) {
            adventure.player(player).sendMessage(ChatHelper.getAsComponent("<green> - <reset>" + customSkin.getSkinName()));
        }
    }

    @Subcommand("wear")
    @Syntax("<name>")
    public void onWear(Player player, String[] args) {
        if (args.length != 1) {
            adventure.player(player).sendMessage(ChatHelper.getAsComponent(this.pluginConfiguration.getBadUsageMessage()));
            return;
        }

        this.skinService.getSkin(player, args[0]).ifPresentOrElse(customSkin -> {
            this.skinService.setSkin(player, customSkin);
            adventure.player(player).sendMessage(ChatHelper.getAsComponent(this.pluginConfiguration.getSkinChangedMessage()));
        }, () -> adventure.player(player).sendMessage(ChatHelper.getAsComponent(this.pluginConfiguration.getSkinNotExistsMessage())));
    }

    @Subcommand("add")
    @Syntax("<name> <url> <model>")
    public void onAdd(Player player, String[] args) {
        if (args.length != 3) {
            adventure.player(player).sendMessage(ChatHelper.getAsComponent(this.pluginConfiguration.getBadUsageMessage()));
            return;
        }

        String name = args[0];
        if (skinService.getSkin(player, name).isPresent()) {
            adventure.player(player).sendMessage(ChatHelper.getAsComponent(this.pluginConfiguration.getSkinAlreadyExists()));
            return;
        }

        Variant variant;
        try {
            variant = Variant.valueOf(args[2].toUpperCase());
        } catch (IllegalArgumentException exception) {
            adventure.player(player).sendMessage(ChatHelper.getAsComponent(this.pluginConfiguration.getInvalidModelMessage()));
            return;
        }

        skinService.uploadSkin(player, name, args[1], SkinOptions.create(name, variant, Visibility.PRIVATE));
    }

    @Subcommand("delete")
    @Syntax("<name>")
    public void onDelete(Player player, String[] args) {
        if (args.length != 1) {
            adventure.player(player).sendMessage(ChatHelper.getAsComponent(this.pluginConfiguration.getBadUsageMessage()));
            return;
        }

        this.skinService.getSkin(player, args[0]).ifPresentOrElse(customSkin -> {
            this.skinService.deleteSkin(customSkin);
            adventure.player(player).sendMessage(ChatHelper.getAsComponent(this.pluginConfiguration.getSkinDeletedMessage()));
        }, () -> adventure.player(player).sendMessage(ChatHelper.getAsComponent(this.pluginConfiguration.getSkinNotExistsMessage())));
    }

    @Subcommand("version")
    public void onVersion(Player player) {
        adventure.player(player).sendMessage(ChatHelper.getAsComponent("CustomSkins v1.0 by teksusik."));
    }
}
