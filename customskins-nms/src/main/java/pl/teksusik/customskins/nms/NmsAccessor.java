package pl.teksusik.customskins.nms;

import com.mojang.authlib.GameProfile;
import org.bukkit.entity.Player;

public interface NmsAccessor {
    GameProfile getGameProfile(Player player);
}
