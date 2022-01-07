package pl.teksusik.customskins.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class ChatHelper {
    private final static MiniMessage MINI_MESSAGE = MiniMessage.builder().build();

    public static Component getAsComponent(String message, Object... replacements) {
        return MINI_MESSAGE.parse(String.format(message, replacements));
    }
}
