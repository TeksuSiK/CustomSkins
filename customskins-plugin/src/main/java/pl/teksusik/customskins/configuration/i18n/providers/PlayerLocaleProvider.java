package pl.teksusik.customskins.configuration.i18n.providers;

import org.bukkit.entity.Player;
import pl.teksusik.customskins.configuration.i18n.LocaleProvider;

import java.util.Locale;

public class PlayerLocaleProvider implements LocaleProvider<Player> {
    @Override
    public boolean supports(Class<?> type) {
        return Player.class.isAssignableFrom(type);
    }

    @Override
    public Locale getLocale(Player entity) {
        String locale = entity.getLocale();
        locale = locale.replace("_", "-");

        return Locale.forLanguageTag(locale);
    }
}
