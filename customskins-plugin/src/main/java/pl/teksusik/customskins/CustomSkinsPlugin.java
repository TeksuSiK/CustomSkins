package pl.teksusik.customskins;

import co.aikar.commands.PaperCommandManager;
import eu.okaeri.configs.ConfigManager;
import eu.okaeri.configs.yaml.bukkit.YamlBukkitConfigurer;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.apache.commons.io.FilenameUtils;
import org.bukkit.plugin.java.JavaPlugin;
import pl.teksusik.customskins.configuration.MessageConfiguration;
import pl.teksusik.customskins.configuration.MiniMessageTransformer;
import pl.teksusik.customskins.configuration.PluginConfiguration;
import pl.teksusik.customskins.configuration.i18n.MessageService;
import pl.teksusik.customskins.configuration.i18n.providers.PlayerLocaleProvider;
import pl.teksusik.customskins.libs.mineskin.MineskinClient;
import pl.teksusik.customskins.nms.NmsAccessor;
import pl.teksusik.customskins.nms.V1_12;
import pl.teksusik.customskins.nms.V1_13;
import pl.teksusik.customskins.nms.V1_14;
import pl.teksusik.customskins.nms.V1_15;
import pl.teksusik.customskins.nms.V1_16;
import pl.teksusik.customskins.nms.V1_17;
import pl.teksusik.customskins.nms.V1_18;
import pl.teksusik.customskins.skin.SkinCommand;
import pl.teksusik.customskins.skin.SkinService;
import pl.teksusik.customskins.storage.Storage;
import pl.teksusik.customskins.storage.StorageType;
import pl.teksusik.customskins.storage.impl.MongoStorage;
import pl.teksusik.customskins.storage.impl.MySQLStorage;
import pl.teksusik.customskins.storage.impl.SQLiteStorage;
import pl.teksusik.customskins.util.ReflectionHelper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.stream.Stream;

public class CustomSkinsPlugin extends JavaPlugin {
    private final File pluginConfigurationFile = new File(getDataFolder(), "config.yml");
    private PluginConfiguration pluginConfiguration;
    private MessageService messageService;

    private SkinService skinService;

    @Override
    public void onEnable() {
        this.pluginConfiguration = this.loadPluginConfiguration();
        this.messageService = this.loadMessageService();

        Storage storage = this.loadStorage();
        NmsAccessor nmsAccessor = this.prepareNmsAccessor();
        this.skinService = new SkinService(this, storage, nmsAccessor, new MineskinClient("CustomSkins"));

        BukkitAudiences adventure = BukkitAudiences.create(this);
        PaperCommandManager paperCommandManager = new PaperCommandManager(this);
        paperCommandManager.registerCommand(new SkinCommand(messageService, storage, skinService, adventure));
    }

    @Override
    public void onDisable() {
    }

    private PluginConfiguration loadPluginConfiguration() {
        return this.pluginConfiguration = ConfigManager.create(PluginConfiguration.class, okaeriConfig -> {
            okaeriConfig.withConfigurer(new YamlBukkitConfigurer());
            okaeriConfig.withBindFile(this.pluginConfigurationFile);
            okaeriConfig.saveDefaults();
            okaeriConfig.load();
        });
    }

    private MessageService loadMessageService() {
        File langDirectory = new File(this.getDataFolder(), "lang");
        if (!langDirectory.exists()) {
            langDirectory.mkdir();
        }

        MessageService messageService = new MessageService(langDirectory);
        messageService.registerLocaleProvider(new PlayerLocaleProvider());
        try (Stream<Path> files = Files.walk(langDirectory.toPath())
            .filter(Files::isReadable)
            .filter(Files::isRegularFile)) {
            files.forEach(path -> {
                Locale locale = Locale.forLanguageTag(FilenameUtils.removeExtension(path.getFileName().toString()));
                MessageConfiguration messageConfiguration = ConfigManager.create(MessageConfiguration.class, okaeriConfig -> {
                    okaeriConfig.withConfigurer(new YamlBukkitConfigurer());
                    okaeriConfig.withSerdesPack(registry -> registry.register(new MiniMessageTransformer(MiniMessage.miniMessage())));
                    okaeriConfig.withBindFile(path);
                    okaeriConfig.saveDefaults();
                    okaeriConfig.load();
                });

                messageService.registerConfiguration(locale, messageConfiguration);
            });
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        messageService.load();

        return messageService;
    }

    private Storage loadStorage() {
        final File sqliteFile = new File(getDataFolder(), this.pluginConfiguration.getSqliteFile());
        if (this.pluginConfiguration.getStorageType().equals(StorageType.SQLITE)) {
            if (!sqliteFile.exists()) {
                try {
                    sqliteFile.createNewFile();
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            }
        }

        switch (this.pluginConfiguration.getStorageType()) {
            case MYSQL:
                return new MySQLStorage(this.pluginConfiguration.getHost(),
                    this.pluginConfiguration.getPort(),
                    this.pluginConfiguration.getDatabase(),
                    this.pluginConfiguration.getUsername(),
                    this.pluginConfiguration.getPassword());
            case SQLITE:
                return new SQLiteStorage(sqliteFile);
            case MONGODB:
                return new MongoStorage(this.pluginConfiguration.getHost(),
                    this.pluginConfiguration.getPort(),
                    this.pluginConfiguration.getDatabase(),
                    this.pluginConfiguration.getUsername(),
                    this.pluginConfiguration.getPassword().toCharArray());
            default:
                throw new IllegalArgumentException("The storage type you entered is invalid");
        }
    }

    private NmsAccessor prepareNmsAccessor() {
        switch (ReflectionHelper.serverVersion) {
            case "v1_12_R1":
                return new V1_12();
            case "v1_13_R2":
                return new V1_13();
            case "v1_14_R1":
                return new V1_14();
            case "v1_15_R1":
                return new V1_15();
            case "v1_16_R3":
                return new V1_16();
            case "v1_17_R1":
                return new V1_17();
            case "v1_18_R1":
                return new V1_18();
            default:
                throw new RuntimeException(String.format("Could not find matching NmsAccessor for currently running server version: %s",
                    ReflectionHelper.serverVersion));
        }
    }

    public SkinService getSkinService() {
        return skinService;
    }
}
