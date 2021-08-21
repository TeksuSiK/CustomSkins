package pl.teksusik.customskins;

import co.aikar.commands.PaperCommandManager;
import eu.okaeri.configs.ConfigManager;
import eu.okaeri.configs.yaml.bukkit.YamlBukkitConfigurer;
import org.bukkit.plugin.java.JavaPlugin;
import pl.teksusik.customskins.command.SkinCommand;
import pl.teksusik.customskins.data.PluginConfiguration;
import pl.teksusik.customskins.data.Storage;
import pl.teksusik.customskins.data.impl.MySQLStorage;
import pl.teksusik.customskins.data.impl.SQLiteStorage;
import pl.teksusik.customskins.libs.mineskin.MineskinClient;
import pl.teksusik.customskins.listener.PlayerJoinListener;
import pl.teksusik.customskins.model.StorageType;
import pl.teksusik.customskins.service.SkinService;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class CustomSkinsPlugin extends JavaPlugin {
    private final File pluginConfigurationFile = new File(getDataFolder(), "config.yml");
    private PluginConfiguration pluginConfiguration;

    private Storage storage;
    private SkinService skinService;

    private PaperCommandManager paperCommandManager;

    @Override
    public void onEnable() {
        this.loadPluginConfiguration();
        this.loadDatabase();
        this.skinService = new SkinService(this, storage, new MineskinClient(Executors.newFixedThreadPool(2), "customskins"));
        this.skinService.prepareSQL();
        this.paperCommandManager = new PaperCommandManager(this);
        this.paperCommandManager.registerCommand(new SkinCommand(pluginConfiguration, skinService));
        this.getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
    }

    @Override
    public void onDisable() {}

    private void loadPluginConfiguration() {
        this.pluginConfiguration = ConfigManager.create(PluginConfiguration.class, okaeriConfig -> {
           okaeriConfig.withConfigurer(new YamlBukkitConfigurer());
           okaeriConfig.withBindFile(pluginConfigurationFile);
           okaeriConfig.saveDefaults();
           okaeriConfig.load();
        });
    }

    private void loadDatabase() {
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

        this.storage = switch (this.pluginConfiguration.getStorageType()) {
            case MYSQL -> new MySQLStorage(this.pluginConfiguration.getMysqlHost(),
                    this.pluginConfiguration.getMysqlPort(),
                    this.pluginConfiguration.getMysqlDatabase(),
                    this.pluginConfiguration.getMysqlUsername(),
                    this.pluginConfiguration.getMysqlPassword());
            case SQLITE -> new SQLiteStorage(sqliteFile);
        };
    }

    public SkinService getSkinService() {
        return skinService;
    }
}
