package pl.teksusik.customskins.i18n.locale;

import eu.okaeri.i18n.locale.LocaleProvider;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public class FixedLocaleProvider implements LocaleProvider<Object> {
    private final Locale locale;

    public FixedLocaleProvider(Locale locale) {
        this.locale = locale;
    }

    @Override
    public boolean supports(Class<?> type) {
        return true;
    }

    @Override
    public @Nullable Locale getLocale(Object entity) {
        return locale;
    }
}
