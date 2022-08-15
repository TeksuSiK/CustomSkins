package pl.teksusik.customskins.skin;

import com.google.inject.Inject;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.mineskin.MineskinClient;
import org.mineskin.SkinOptions;
import pl.teksusik.customskins.CustomSkinsPlugin;
import pl.teksusik.customskins.nms.NmsAccessor;
import pl.teksusik.customskins.storage.Storage;
import pl.teksusik.customskins.util.ReflectionHelper;

import javax.management.ReflectionException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.CompletableFuture;

public class SkinService {
    @Inject
    private CustomSkinsPlugin plugin;
    @Inject
    private Storage storage;
    @Inject
    private NmsAccessor nmsAccessor;
    @Inject
    private MineskinClient mineskinClient;

    private static final Method refreshPlayerMethod;
    private static final Method getHandleMethod;
    private static Method healthUpdateMethod;

    static {
        try {
            refreshPlayerMethod = ReflectionHelper.getBukkitClass("entity.CraftPlayer").getDeclaredMethod("refreshPlayer");
            refreshPlayerMethod.setAccessible(true);
            getHandleMethod = ReflectionHelper.getBukkitClass("entity.CraftPlayer").getDeclaredMethod("getHandle");
            getHandleMethod.setAccessible(true);

            // XP won't get updated on unsupported Paper builds
            try {
                healthUpdateMethod = ReflectionHelper.getBukkitClass("entity.CraftPlayer").getDeclaredMethod("triggerHealthUpdate");
                healthUpdateMethod.setAccessible(true);
            } catch (NoSuchMethodException ignored) {
            }

        } catch (Exception exception) {
            throw new ExceptionInInitializerError(exception);
        }
    }

    private void updateSkin(Player player) {
        try {
            refreshPlayerMethod.invoke(player);

            if (healthUpdateMethod != null) {
                healthUpdateMethod.invoke(player);
            } else {
                ReflectionHelper.invokeMethod(getHandleMethod.invoke(player), "triggerHealthUpdate");
            }
        } catch (IllegalAccessException | InvocationTargetException | ReflectionException ignored) {
        }
    }

    public void setSkin(Player player, CustomSkin customSkin) {
        GameProfile gameProfile = this.nmsAccessor.getGameProfile(player);
        PropertyMap propertyMap = gameProfile.getProperties();
        if (propertyMap != null)
            propertyMap.clear();
        propertyMap.put("textures", new Property("textures",
            customSkin.getTexture(),
            customSkin.getSignature()));
        Bukkit.getScheduler().runTask(this.plugin, () -> Bukkit.getOnlinePlayers()
            .stream()
            .filter(players -> !player.equals(players))
            .forEach(players -> {
                players.hidePlayer(this.plugin, player);
                players.showPlayer(this.plugin, player);
            }));
        this.updateSkin(player);
    }

    public CompletableFuture<Void> uploadSkin(Player player, String name, String url, SkinOptions skinOptions) {
        return this.mineskinClient.generateUrl(url, skinOptions).thenAcceptAsync(skin -> {
            CustomSkin generatedSkin = new CustomSkin(player.getUniqueId(), name, skin.data.texture.value, skin.data.texture.signature);
            this.storage.insertSkin(generatedSkin);
        });
    }
}
