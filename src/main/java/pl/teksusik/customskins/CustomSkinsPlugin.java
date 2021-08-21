package pl.teksusik.customskins;

import eu.okaeri.configs.ConfigManager;
import eu.okaeri.configs.yaml.bukkit.YamlBukkitConfigurer;
import org.bukkit.plugin.java.JavaPlugin;
import pl.teksusik.customskins.data.PluginConfiguration;
import pl.teksusik.customskins.data.Storage;
import pl.teksusik.customskins.data.impl.MySQLStorage;
import pl.teksusik.customskins.data.impl.SQLiteStorage;
import pl.teksusik.customskins.service.SkinService;

import java.io.File;

public class CustomSkinsPlugin extends JavaPlugin {
    private final File pluginConfigurationFile = new File(getDataFolder(), "config.yml");
    private PluginConfiguration pluginConfiguration;

    private Storage storage;
    private SkinService skinService;

    @Override
    public void onEnable() {
        this.loadPluginConfiguration();
        this.loadDatabase();
        this.skinService = new SkinService(storage);
        this.skinService.prepareSQL();
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
        this.storage = switch (this.pluginConfiguration.getStorageType()) {
            case MYSQL -> new MySQLStorage(this.pluginConfiguration.getMysqlHost(),
                    this.pluginConfiguration.getMysqlPort(),
                    this.pluginConfiguration.getMysqlDatabase(),
                    this.pluginConfiguration.getMysqlUsername(),
                    this.pluginConfiguration.getMysqlPassword());
            case SQLITE -> new SQLiteStorage(this.pluginConfiguration.getSqliteFile());
        };
    }
}
