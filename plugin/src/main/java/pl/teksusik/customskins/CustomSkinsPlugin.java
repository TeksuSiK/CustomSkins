package pl.teksusik.customskins;

import co.aikar.commands.PaperCommandManager;
import com.google.inject.Binder;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import eu.okaeri.configs.ConfigManager;
import eu.okaeri.configs.yaml.bukkit.YamlBukkitConfigurer;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.apache.commons.io.FilenameUtils;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bstats.charts.SingleLineChart;
import org.bukkit.plugin.java.JavaPlugin;
import org.mineskin.MineskinClient;
import org.slf4j.Logger;
import pl.teksusik.customskins.configuration.MessageConfiguration;
import pl.teksusik.customskins.configuration.MiniMessageTransformer;
import pl.teksusik.customskins.configuration.PluginConfiguration;
import pl.teksusik.customskins.configuration.i18n.MessageService;
import pl.teksusik.customskins.skin.SkinCommand;
import pl.teksusik.customskins.skin.SkinService;
import pl.teksusik.customskins.storage.Storage;
import pl.teksusik.customskins.storage.StorageType;
import pl.teksusik.customskins.storage.impl.MongoStorage;
import pl.teksusik.customskins.storage.impl.MySQLStorage;
import pl.teksusik.customskins.storage.impl.PostgreSQLStorage;
import pl.teksusik.customskins.storage.impl.SQLiteStorage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.stream.Stream;

public class CustomSkinsPlugin extends JavaPlugin implements Module {
    private final File pluginConfigurationFile = new File(getDataFolder(), "config.yml");
    private PluginConfiguration pluginConfiguration;

    private Storage skinStorage;
    private SkinService skinService;

    @Override
    public void onEnable() {
        Injector injector = Guice.createInjector(this);

        this.skinStorage = injector.getInstance(Storage.class);
        this.skinService = injector.getInstance(SkinService.class);

        PaperCommandManager paperCommandManager = new PaperCommandManager(this);
        paperCommandManager.registerCommand(injector.getInstance(SkinCommand.class));

        Metrics metrics = new Metrics(this, 15828);
        metrics.addCustomChart(new SingleLineChart("skins", () -> skinStorage.countSkins()));
        metrics.addCustomChart(new SimplePie("storage_type", () -> pluginConfiguration.getStorageType().toString()));
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
            case POSTGRESQL:
                return new PostgreSQLStorage(this.pluginConfiguration.getHost(),
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
                    this.pluginConfiguration.getPassword());
            default:
                throw new IllegalArgumentException("The storage type you entered is invalid");
        }
    }

    @Override
    public void configure(Binder binder) {
        binder.bind(CustomSkinsPlugin.class).toInstance(this);
        binder.bind(Logger.class).toInstance(this.getSLF4JLogger());
        binder.bind(PluginConfiguration.class).toInstance(this.loadPluginConfiguration());
        binder.bind(MessageService.class).toInstance(this.loadMessageService());
        binder.bind(Storage.class).toInstance(this.loadStorage());
        binder.bind(BukkitAudiences.class).toInstance(BukkitAudiences.create(this));
        binder.bind(MineskinClient.class).toInstance(new MineskinClient("CustomSkins"));
    }

    public Storage getSkinStorage() {
        return skinStorage;
    }

    public SkinService getSkinService() {
        return skinService;
    }
}
