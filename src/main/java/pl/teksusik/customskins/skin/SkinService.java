package pl.teksusik.customskins.skin;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import com.google.inject.Inject;
import org.bukkit.entity.Player;
import org.mineskin.MineskinClient;
import org.mineskin.SkinOptions;
import pl.teksusik.customskins.storage.Storage;

import java.util.concurrent.CompletableFuture;

public class SkinService {
    @Inject
    private Storage storage;
    @Inject
    private MineskinClient mineskinClient;

    public void setSkin(Player player, CustomSkin customSkin) {
        PlayerProfile profile = player.getPlayerProfile();
        ProfileProperty textures = new ProfileProperty("textures", customSkin.getTexture(), customSkin.getSignature());
        profile.setProperty(textures);
        player.setPlayerProfile(profile);
    }

    public void clearSkin(Player player) {
        PlayerProfile profile = player.getPlayerProfile();
        profile.removeProperty("textures");
        player.setPlayerProfile(profile);
    }

    public CompletableFuture<Void> uploadSkin(Player player, String name, String url, SkinOptions skinOptions) {
        return this.mineskinClient.generateUrl(url, skinOptions).thenAcceptAsync(skin -> {
            CustomSkin generatedSkin = new CustomSkin(player.getUniqueId(), name, skin.data.texture.value, skin.data.texture.signature);
            this.storage.insertSkin(generatedSkin);
        });
    }
}
