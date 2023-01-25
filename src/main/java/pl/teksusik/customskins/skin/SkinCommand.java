package pl.teksusik.customskins.skin;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import com.google.inject.Inject;
import org.bukkit.entity.Player;
import org.mineskin.SkinOptions;
import org.mineskin.Variant;
import org.mineskin.Visibility;
import pl.teksusik.customskins.configuration.MessageConfiguration;
import pl.teksusik.customskins.i18n.BI18n;
import pl.teksusik.customskins.storage.Storage;

import java.util.List;
import java.util.stream.Collectors;

@CommandAlias("skin|skins|customskins")
public class SkinCommand extends BaseCommand {
    @Inject
    private BI18n i18n;
    @Inject
    private MessageConfiguration messages;
    @Inject
    private Storage skinStorage;
    @Inject
    private SkinService skinService;

    @Default
    public void onDefault(Player player) {
        this.i18n.get(this.messages.getHelpCommands()).sendTo(player);
    }

    @Subcommand("list")
    public void onList(Player player) {
        List<String> customSkins = this.skinStorage.getAllSkinsByOwner(player.getUniqueId())
            .stream()
            .map(CustomSkin::getName)
            .collect(Collectors.toList());
        String skins = String.join(", ", customSkins);
        this.i18n.get(this.messages.getSkinsAvailable())
            .with("skins", skins)
            .sendTo(player);
    }

    @Subcommand("wear")
    @Syntax("<name>")
    public void onWear(Player player, String[] args) {
        if (args.length != 1) {
            this.i18n.get(this.messages.getBadUsage())
                .sendTo(player);
            return;
        }

        this.skinStorage.findSkin(player.getUniqueId(), args[0]).ifPresentOrElse(customSkin -> {
            this.skinService.setSkin(player, customSkin);
            this.i18n.get(this.messages.getSkinChanged())
                .with("skin", customSkin.getName())
                .sendTo(player);
        }, () -> this.i18n.get(this.messages.getSkinNotExists())
            .with("skin", args[0])
            .sendTo(player));
    }

    @Subcommand("add")
    @Syntax("<name> <url> <model>")
    public void onAdd(Player player, String[] args) {
        if (args.length != 3) {
            this.i18n.get(this.messages.getBadUsage())
                .sendTo(player);
            return;
        }

        String name = args[0];
        if (this.skinStorage.findSkin(player.getUniqueId(), name).isPresent()) {
            this.i18n.get(this.messages.getSkinAlreadyExists())
                .with("skin", name)
                .sendTo(player);
            return;
        }

        Variant variant;
        try {
            variant = Variant.valueOf(args[2].toUpperCase());
        } catch (IllegalArgumentException exception) {
            this.i18n.get(this.messages.getInvalidModel())
                .with("model", args[2])
                .sendTo(player);
            return;
        }

        this.skinService.uploadSkin(player, name, args[1], SkinOptions.create(name, variant, Visibility.PRIVATE))
            .thenAccept(action -> this.i18n.get(this.messages.getSkinUploaded())
                .with("skin", name)
                .sendTo(player));
    }

    @Subcommand("delete")
    @Syntax("<name>")
    public void onDelete(Player player, String[] args) {
        if (args.length != 1) {
            this.i18n.get(this.messages.getBadUsage())
                .sendTo(player);
            return;
        }

        this.skinStorage.findSkin(player.getUniqueId(), args[0]).ifPresentOrElse(customSkin -> {
            this.skinStorage.deleteSkin(customSkin);
            this.i18n.get(this.messages.getSkinDeleted())
                .with("skin", customSkin.getName())
                .sendTo(player);
        }, () -> this.i18n.get(this.messages.getSkinNotExists())
            .with("skin", args[0])
            .sendTo(player));
    }
}
