package pl.teksusik.customskins.i18n.locale;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import eu.okaeri.i18n.locale.LocaleProvider;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public class PlayerLocaleProvider implements LocaleProvider<Player> {
    @Inject
    @Named("defaultLocale")
    private Locale fallbackLocale;

    @Override
    public boolean supports(Class<?> type) {
        return Player.class.isAssignableFrom(type);
    }

    @Override
    public @Nullable Locale getLocale(Player entity) {
        Locale locale = Locale.forLanguageTag(entity.getLocale().replace("_", "-"));
        if (locale == null) {
            return this.fallbackLocale;
        }

        return locale;
    }
}
