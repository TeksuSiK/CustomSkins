package pl.teksusik.customskins.i18n;

import com.google.inject.Inject;
import eu.okaeri.i18n.configs.extended.CustomMEOCI18n;
import eu.okaeri.i18n.core.minecraft.adventure.AdventureMessage;
import eu.okaeri.placeholders.Placeholders;
import eu.okaeri.placeholders.message.CompiledMessage;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;


public class BI18n extends CustomMEOCI18n<AdventureMessage> {
    @Inject
    private BukkitAudiences adventure;

    @Override
    public BukkitMessageDispatcher get(String key) {
        return new BukkitMessageDispatcher(this, this.adventure, key);
    }

    @Override
    public AdventureMessage assembleMessage(Placeholders placeholders, CompiledMessage compiled) {
        return AdventureMessage.of(placeholders, compiled);
    }

    public List<String> getAvailableLocales() {
        return this.configs.keySet()
            .stream()
            .map(Locale::toLanguageTag)
            .collect(Collectors.toList());
    }
}
