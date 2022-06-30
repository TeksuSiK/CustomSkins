package pl.teksusik.customskins.nms;

import com.mojang.authlib.GameProfile;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class V1_15 implements NmsAccessor {
    @Override
    public GameProfile getGameProfile(Player player) {
        return ((CraftPlayer) player).getProfile();
    }
}
