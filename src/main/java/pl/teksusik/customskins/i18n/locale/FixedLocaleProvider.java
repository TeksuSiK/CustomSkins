package pl.teksusik.customskins.i18n.locale;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import eu.okaeri.i18n.locale.LocaleProvider;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public class FixedLocaleProvider implements LocaleProvider<Object> {
    @Inject
    @Named("defaultLocale")
    private Locale locale;

    @Override
    public boolean supports(Class<?> type) {
        return true;
    }

    @Override
    public @Nullable Locale getLocale(Object entity) {
        return locale;
    }
}
