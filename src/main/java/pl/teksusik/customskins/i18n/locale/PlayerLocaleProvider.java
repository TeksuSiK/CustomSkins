package pl.teksusik.customskins.i18n.locale;

import eu.okaeri.i18n.locale.LocaleProvider;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public class PlayerLocaleProvider implements LocaleProvider<Player> {
    private final Locale fallbackLocale;

    public PlayerLocaleProvider(Locale fallbackLocale) {
        this.fallbackLocale = fallbackLocale;
    }

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
