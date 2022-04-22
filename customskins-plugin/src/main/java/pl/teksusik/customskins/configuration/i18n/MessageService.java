package pl.teksusik.customskins.configuration.i18n;

import eu.okaeri.configs.ConfigManager;
import eu.okaeri.configs.yaml.bukkit.YamlBukkitConfigurer;
import net.kyori.adventure.text.minimessage.MiniMessage;
import pl.teksusik.customskins.configuration.MessageConfiguration;
import pl.teksusik.customskins.configuration.MiniMessageTransformer;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MessageService {
    private final List<LocaleProvider<?>> localeProviders = new ArrayList<>();
    private final Map<Locale, MessageConfiguration> messageConfigurations = new HashMap<>();
    private final File langDirectory;

    private static final Locale DEFAULT_LOCALE = Locale.forLanguageTag("en-us");

    public MessageService(File langDirectory) {
        this.langDirectory = langDirectory;
    }

    private LocaleProvider getLocaleProvider(Class<?> entityType) {
        return this.localeProviders.stream()
            .filter(provider -> provider.supports(entityType))
            .findAny()
            .orElse(null);
    }

    private Locale getLocale(Object entity) {
        LocaleProvider localeProvider = this.getLocaleProvider(entity.getClass());
        if (localeProvider == null) {
            throw new IllegalArgumentException(String.format("Locale provider for %s not exists", entity.getClass()));
        }

        Locale locale = localeProvider.getLocale(entity);
        if (locale == null) {
            return this.getDefaultLocale();
        }

        return locale;
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

    public <E> void registerLocaleProvider(LocaleProvider<E> localeProvider) {
        this.localeProviders.add(localeProvider);
    }

    public void registerConfiguration(Locale locale, MessageConfiguration messageConfiguration) {
        this.messageConfigurations.put(locale, messageConfiguration);
    }

    public <E> MessageConfiguration getMessageConfiguration(E entity) {
        return this.messageConfigurations.getOrDefault(this.getLocale(entity), this.messageConfigurations.get(Locale.ENGLISH));
    }
}
