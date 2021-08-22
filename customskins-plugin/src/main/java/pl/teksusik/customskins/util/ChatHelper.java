package pl.teksusik.customskins.util;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

public class ChatHelper {
    private final static MiniMessage MINI_MESSAGE = MiniMessage.get();

    public static void sendMessage(Player player, String message, Object... replacements) {
        player.sendMessage(MINI_MESSAGE.parse(String.format(message, replacements)));
    }
}
