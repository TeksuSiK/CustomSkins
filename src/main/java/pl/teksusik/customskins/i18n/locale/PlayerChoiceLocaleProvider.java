package pl.teksusik.customskins.i18n.locale;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import eu.okaeri.i18n.locale.LocaleProvider;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import pl.teksusik.customskins.storage.Storage;

import java.util.Locale;
import java.util.Optional;

public class PlayerChoiceLocaleProvider implements LocaleProvider<Player> {
    @Inject
    private Storage storage;
    @Inject
    @Named("defaultLocale")
    private Locale fallbackLocale;

    @Override
    public boolean supports(Class<?> type) {
        return Player.class.isAssignableFrom(type);
    }

    @Override
    public @Nullable Locale getLocale(Player entity) {
        Optional<String> preferableLocale = this.storage.findLocale(entity.getUniqueId());
        if (preferableLocale.isEmpty()) {
            return this.fallbackLocale;
        }

        Locale locale = Locale.forLanguageTag(preferableLocale.get().replace("_", "-"));
        if (locale == null) {
            return this.fallbackLocale;
        }

        return locale;
    }
}
