package pl.teksusik.customskins.configuration.i18n;

import eu.okaeri.configs.ConfigManager;
import eu.okaeri.configs.yaml.bukkit.YamlBukkitConfigurer;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.apache.commons.io.FilenameUtils;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import pl.teksusik.customskins.configuration.MessageConfiguration;
import pl.teksusik.customskins.configuration.MiniMessageTransformer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class MessageService {
    private final JavaPlugin plugin;
    private final Map<String, MessageConfiguration> messagesConfiguration;
    private MessageConfiguration defaultConfiguration;

    public MessageService(JavaPlugin plugin) {
        this.plugin = plugin;
        this.messagesConfiguration = this.loadMessageConfiguration();
    }

    public Map<String, MessageConfiguration> loadMessageConfiguration() {
        File langDirectory = new File(this.plugin.getDataFolder(), "lang");
        if (!langDirectory.exists()) {
            langDirectory.mkdir();
        }

        Map<String, MessageConfiguration> messagesConfiguration = new HashMap<>();
        this.defaultConfiguration = ConfigManager.create(MessageConfiguration.class, okaeriConfig -> {
            okaeriConfig.withConfigurer(new YamlBukkitConfigurer());
            okaeriConfig.withSerdesPack(registry -> registry.register(new MiniMessageTransformer(MiniMessage.miniMessage())));
            okaeriConfig.withBindFile(new File(langDirectory, "en_US.yml"));
            okaeriConfig.saveDefaults();
            okaeriConfig.load();
        });

        try (Stream<Path> files = Files.walk(langDirectory.toPath())
            .filter(Files::isReadable)
            .filter(Files::isRegularFile)) {
            files.forEach(path -> {
                String locale = FilenameUtils.removeExtension(path.getFileName().toString()).toLowerCase();
                MessageConfiguration messageConfiguration = ConfigManager.create(MessageConfiguration.class, okaeriConfig -> {
                    okaeriConfig.withConfigurer(new YamlBukkitConfigurer());
                    okaeriConfig.withSerdesPack(registry -> registry.register(new MiniMessageTransformer(MiniMessage.miniMessage())));
                    okaeriConfig.withBindFile(path);
                    okaeriConfig.saveDefaults();
                    okaeriConfig.load();
                });

                messagesConfiguration.put(locale, messageConfiguration);
            });
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        return messagesConfiguration;
    }

    public MessageConfiguration getMessageConfiguration(Player player) {
        return this.messagesConfiguration.getOrDefault(player.getLocale().toLowerCase(), this.defaultConfiguration);
    }
}
