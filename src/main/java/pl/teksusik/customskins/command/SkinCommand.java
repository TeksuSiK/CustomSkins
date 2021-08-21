package pl.teksusik.customskins.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import org.bukkit.entity.Player;
import pl.teksusik.customskins.data.PluginConfiguration;
import pl.teksusik.customskins.model.CustomSkin;
import pl.teksusik.customskins.service.SkinService;
import pl.teksusik.customskins.util.ChatHelper;

@CommandAlias("skin|skins|customskins")
public class SkinCommand extends BaseCommand {
    private final PluginConfiguration pluginConfiguration;
    private final SkinService skinService;

    public SkinCommand(PluginConfiguration pluginConfiguration, SkinService skinService) {
        this.pluginConfiguration = pluginConfiguration;
        this.skinService = skinService;
    }

    @Default
    public void onDefault(Player player) {
        for (String message : this.pluginConfiguration.getHelpCommands())
            ChatHelper.sendMessage(player, message);
    }

    @Subcommand("list")
    public void onList(Player player) {
        ChatHelper.sendMessage(player, this.pluginConfiguration.getSkinsAvailableMessage());
        for (CustomSkin customSkin : this.skinService.getSkins(player)) {
            ChatHelper.sendMessage(player, "<green> - <reset>" + customSkin.getSkinName());
        }
    }

    @Subcommand("wear")
    @Syntax("<name>")
    public void onWear(Player player, String[] args) {
        //TODO
    }

    @Subcommand("add")
    @Syntax("<name> <url> <model>")
    public void onAdd(Player player, String[] args) {
        //TODO
    }

    @Subcommand("remove")
    @Syntax("<name>")
    public void onRemove(Player player, String[] args) {
        this.skinService.getSkin(player, args[0]).ifPresentOrElse(this.skinService::deleteSkin,
                () -> ChatHelper.sendMessage(player, this.pluginConfiguration.getSkinNotExistsMessage()));
    }

    @Subcommand("version")
    public void onVersion(Player player) {
        ChatHelper.sendMessage(player, "CustomSkins v1.0 by teksusik.");
    }
}
