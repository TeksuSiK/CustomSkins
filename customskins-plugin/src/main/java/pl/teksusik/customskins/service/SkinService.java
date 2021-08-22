package pl.teksusik.customskins.service;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import pl.teksusik.customskins.CustomSkinsPlugin;
import pl.teksusik.customskins.data.Storage;
import pl.teksusik.customskins.libs.mineskin.MineskinClient;
import pl.teksusik.customskins.libs.mineskin.SkinOptions;
import pl.teksusik.customskins.model.CustomSkin;
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
    }

    private static final String CREATE_QUERY = """
            CREATE TABLE IF NOT EXISTS `customskins_skins` (
              `skinOwner` varchar(36) NOT NULL,
              `skinName` varchar(51) NOT NULL,
              `skinTexture` longtext NOT NULL,
              `skinSignature` longtext NOT NULL
            );
            """;

    public void prepareSQL() {
        try (final Connection connection = this.storage.getHikariDataSource().getConnection();
             final PreparedStatement preparedStatement = connection.prepareStatement(CREATE_QUERY)) {
            preparedStatement.executeUpdate();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public void loadSkins(UUID uuid) {
        try (final Connection connection = this.storage.getHikariDataSource().getConnection();
            final PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM customskins_skins WHERE skinOwner = ?")) {
            preparedStatement.setString(1, uuid.toString());

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next())
                    this.addSkin(new CustomSkin(uuid,
                            resultSet.getString("skinName"),
                            resultSet.getString("skinTexture"),
                            resultSet.getString("skinSignature")));
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public void loadSkins(Player player) {
        loadSkins(player.getUniqueId());
    }

    public Set<CustomSkin> getSkins(UUID uuid) {
        return this.customSkins.stream()
                .filter(customSkin -> customSkin.getSkinOwner().equals(uuid))
                .collect(Collectors.toSet());
    }

    public Set<CustomSkin> getSkins(Player player) {
        return getSkins(player.getUniqueId());
    }

    public Optional<CustomSkin> getSkin(UUID skinOwner, String skinName) {
        return this.customSkins.stream()
                .filter(customSkin -> customSkin.getSkinOwner().equals(skinOwner))
                .filter(customSkin -> customSkin.getSkinName().equalsIgnoreCase(skinName))
                .findFirst();
    }

    public Optional<CustomSkin> getSkin(Player player, String skinName) {
        return getSkin(player.getUniqueId(), skinName);
    }

    public CustomSkin createSkin(CustomSkin customSkin) {
        try (final Connection connection = this.storage.getHikariDataSource().getConnection();
            final PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO customskins_skins VALUES (?, ?, ?, ?)")) {
            preparedStatement.setString(1, customSkin.getSkinOwner().toString());
            preparedStatement.setString(2, customSkin.getSkinName());
            preparedStatement.setString(3, customSkin.getSkinTexture());
            preparedStatement.setString(4, customSkin.getSkinSignature());
            preparedStatement.executeUpdate();
            this.addSkin(customSkin);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return customSkin;
    }

    public void deleteSkin(CustomSkin customSkin) {
        try (final Connection connection = this.storage.getHikariDataSource().getConnection();
             final PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM customskins_skins WHERE skinOwner = ? AND skinName = ?")) {
            preparedStatement.setString(1, customSkin.getSkinOwner().toString());
            preparedStatement.setString(2, customSkin.getSkinName());
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
        } catch (IllegalAccessException | InvocationTargetException | ReflectionException exception) {
            exception.printStackTrace();
        }
    }

    public void setSkin(Player player, CustomSkin customSkin) {
        GameProfile gameProfile = this.nmsAccessor.getGameProfile(player);
        PropertyMap propertyMap = gameProfile.getProperties();
        if (propertyMap != null)
            propertyMap.clear();
        propertyMap.put("textures", new Property("textures",
                customSkin.getSkinTexture(),
                customSkin.getSkinSignature()));
        Bukkit.getScheduler().runTask(this.plugin, () -> Bukkit.getOnlinePlayers()
                .stream()
                .filter(players -> !player.equals(players))
                .forEach(players -> {
                    players.hidePlayer(this.plugin, player);
                    players.showPlayer(this.plugin, player);
                }));
        updateSkin(player);
    }

    public void uploadSkin(Player player, String name, String url, SkinOptions skinOptions) {
        mineskinClient.generateUrl(url, skinOptions).thenAcceptAsync(skin -> {
            CustomSkin generatedSkin = new CustomSkin(player.getUniqueId(), name, skin.data.texture.value, skin.data.texture.signature);
            createSkin(generatedSkin);
        });
    }
}
