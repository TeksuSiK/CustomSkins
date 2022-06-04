package pl.teksusik.customskins.configuration.i18n;

import eu.okaeri.configs.ConfigManager;
import eu.okaeri.configs.yaml.bukkit.YamlBukkitConfigurer;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import pl.teksusik.customskins.configuration.MessageConfiguration;
import pl.teksusik.customskins.configuration.MiniMessageTransformer;

import java.io.File;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MessageService {
    private final Map<Locale, MessageConfiguration> messageConfigurations = new HashMap<>();
    private final File langDirectory;

    private static final Locale DEFAULT_LOCALE = Locale.forLanguageTag("en-us");

    public MessageService(File langDirectory) {
        this.langDirectory = langDirectory;
    }

    private Locale getDefaultLocale() {
        return DEFAULT_LOCALE;
    }

    public void load() {
        if (!this.messageConfigurations.containsKey(this.getDefaultLocale())) {
            MessageConfiguration messageConfiguration = ConfigManager.create(MessageConfiguration.class, okaeriConfig -> {
                okaeriConfig.withConfigurer(new YamlBukkitConfigurer());
                okaeriConfig.withSerdesPack(registry -> registry.register(new MiniMessageTransformer(MiniMessage.miniMessage())));
                okaeriConfig.withBindFile(new File(this.langDirectory, this.getDefaultLocale() + ".yml"));
                okaeriConfig.saveDefaults();
                okaeriConfig.load();
            });

            this.messageConfigurations.put(this.getDefaultLocale(), messageConfiguration);
        }
    }

    public void registerConfiguration(Locale locale, MessageConfiguration messageConfiguration) {
        this.messageConfigurations.put(locale, messageConfiguration);
    }

    public MessageConfiguration getMessageConfiguration(Player entity) {
        return this.messageConfigurations.getOrDefault(this.getLocale(entity), this.messageConfigurations.get(Locale.ENGLISH));
    }

    private Locale getLocale(Player entity) {
        String locale = entity.getLocale();
        locale = locale.replace("_", "-");

        return Locale.forLanguageTag(locale);
    }
}
