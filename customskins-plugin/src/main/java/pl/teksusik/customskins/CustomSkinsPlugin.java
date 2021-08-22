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
import pl.teksusik.customskins.nms.*;
import pl.teksusik.customskins.service.SkinService;
import pl.teksusik.customskins.util.ReflectionHelper;

import java.io.File;
import java.io.IOException;

public class CustomSkinsPlugin extends JavaPlugin {
    private final File pluginConfigurationFile = new File(getDataFolder(), "config.yml");
    private PluginConfiguration pluginConfiguration;

    private Storage storage;
    private SkinService skinService;
    private NmsAccessor nmsAccessor;

    private PaperCommandManager paperCommandManager;

    @Override
    public void onEnable() {
        this.loadPluginConfiguration();
        this.loadDatabase();
        this.nmsAccessor = this.prepareNmsAccessor();
        this.skinService = new SkinService(this, storage, nmsAccessor, new MineskinClient("pl/teksusik/customskins"));
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

    private NmsAccessor prepareNmsAccessor() {
        switch (ReflectionHelper.serverVersion) {
            case "v1_8_R3":
                return new V1_8();
            case "v1_9_R2":
                return new V1_9();
            case "v1_10_R1":
                return new V1_10();
            case "v1_11_R1":
                return new V1_11();
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
            default:
                throw new RuntimeException(String.format("Could not find matching NmsAccessor for currently running server version: %s",
                        ReflectionHelper.serverVersion));
        }
    }

    public SkinService getSkinService() {
        return skinService;
    }
}
