package pl.teksusik.customskins.configuration.i18n;

import java.util.Locale;

public interface LocaleProvider<E> {
    boolean supports(Class<?> type);
    Locale getLocale(E entity);
}
