package pl.teksusik.customskins.util;

import org.bukkit.entity.Player;

public class ChatHelper {
    public static void sendMessage(Player player, String message, Object... replacements) {
        player.sendMessage(String.format(message, replacements));
    }
}
