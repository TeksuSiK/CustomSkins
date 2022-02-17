package pl.teksusik.customskins.skin;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import pl.teksusik.customskins.CustomSkinsPlugin;
import pl.teksusik.customskins.storage.Storage;
import pl.teksusik.customskins.libs.mineskin.MineskinClient;
import pl.teksusik.customskins.libs.mineskin.SkinOptions;
import pl.teksusik.customskins.nms.NmsAccessor;
import pl.teksusik.customskins.util.ReflectionHelper;

import javax.management.ReflectionException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class SkinService {
    private final Set<CustomSkin> customSkins = new HashSet<>();
    private final CustomSkinsPlugin plugin;
    private final Storage storage;
    private final NmsAccessor nmsAccessor;
    private final MineskinClient mineskinClient;

    public SkinService(CustomSkinsPlugin plugin, Storage storage, NmsAccessor nmsAccessor, MineskinClient mineskinClient) {
        this.plugin = plugin;
        this.storage = storage;
        this.nmsAccessor = nmsAccessor;
        this.mineskinClient = mineskinClient;

        try (final Connection connection = this.storage.getHikariDataSource().getConnection();
             final PreparedStatement preparedStatement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS `customskins_skins` (`owner` varchar(36) NOT NULL, `name` varchar(51) NOT NULL, `texture` longtext NOT NULL, `signature` longtext NOT NULL);")) {
            preparedStatement.executeUpdate();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }

    }

    public void loadSkins(UUID uuid) {
        try (final Connection connection = this.storage.getHikariDataSource().getConnection();
             final PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM customskins_skins WHERE owner = ?")) {
            preparedStatement.setString(1, uuid.toString());

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next())
                    this.addSkin(new CustomSkin(uuid,
                        resultSet.getString("name"),
                        resultSet.getString("texture"),
                        resultSet.getString("signature")));
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public void loadSkins(Player player) {
        this.loadSkins(player.getUniqueId());
    }

    public Set<CustomSkin> getSkins(UUID uuid) {
        return this.customSkins.stream()
            .filter(customSkin -> customSkin.getOwner().equals(uuid))
            .collect(Collectors.toSet());
    }

    public Set<CustomSkin> getSkins(Player player) {
        return this.getSkins(player.getUniqueId());
    }

    public Optional<CustomSkin> getSkin(UUID owner, String name) {
        return this.customSkins.stream()
            .filter(customSkin -> customSkin.getOwner().equals(owner))
            .filter(customSkin -> customSkin.getName().equalsIgnoreCase(name))
            .findFirst();
    }

    public Optional<CustomSkin> getSkin(Player player, String name) {
        return this.getSkin(player.getUniqueId(), name);
    }

    public CustomSkin createSkin(CustomSkin customSkin) {
        try (final Connection connection = this.storage.getHikariDataSource().getConnection();
             final PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO customskins_skins VALUES (?, ?, ?, ?)")) {
            preparedStatement.setString(1, customSkin.getOwner().toString());
            preparedStatement.setString(2, customSkin.getName());
            preparedStatement.setString(3, customSkin.getTexture());
            preparedStatement.setString(4, customSkin.getSignature());
            preparedStatement.executeUpdate();
            this.addSkin(customSkin);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return customSkin;
    }

    public void deleteSkin(CustomSkin customSkin) {
        try (final Connection connection = this.storage.getHikariDataSource().getConnection();
             final PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM customskins_skins WHERE owner = ? AND name = ?")) {
            preparedStatement.setString(1, customSkin.getOwner().toString());
            preparedStatement.setString(2, customSkin.getName());
            preparedStatement.executeUpdate();
            this.removeSkin(customSkin);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    private synchronized void addSkin(CustomSkin customSkin) {
        this.customSkins.add(customSkin);
    }

    private synchronized void removeSkin(CustomSkin customSkin) {
        this.customSkins.remove(customSkin);
    }

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

    public void uploadSkin(Player player, String name, String url, SkinOptions skinOptions) {
        this.mineskinClient.generateUrl(url, skinOptions).thenAcceptAsync(skin -> {
            CustomSkin generatedSkin = new CustomSkin(player.getUniqueId(), name, skin.data.texture.value, skin.data.texture.signature);
            createSkin(generatedSkin);
        });
    }
}
