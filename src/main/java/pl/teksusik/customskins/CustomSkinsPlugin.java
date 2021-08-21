package pl.teksusik.customskins;

import eu.okaeri.configs.ConfigManager;
import eu.okaeri.configs.yaml.bukkit.YamlBukkitConfigurer;
import org.bukkit.plugin.java.JavaPlugin;
import pl.teksusik.customskins.data.PluginConfiguration;

import java.io.File;

public class CustomSkinsPlugin extends JavaPlugin {
    private final File pluginConfigurationFile = new File(getDataFolder(), "config.yml");
    private PluginConfiguration pluginConfiguration;

    @Override
    public void onEnable() {
        this.loadPluginConfiguration();
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
}
