package pl.teksusik.customskins.nms;

import com.mojang.authlib.GameProfile;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class V1_16 implements NmsAccessor {
    @Override
    public GameProfile getGameProfile(Player player) {
        return ((CraftPlayer) player).getProfile();
    }
}
