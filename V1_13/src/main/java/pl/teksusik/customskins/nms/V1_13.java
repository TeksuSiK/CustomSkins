package pl.teksusik.customskins.nms;

import com.mojang.authlib.GameProfile;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class V1_13 implements NmsAccessor {
    @Override
    public GameProfile getGameProfile(Player player) {
        return ((CraftPlayer) player).getProfile();
    }
}
