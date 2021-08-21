package pl.teksusik.customskins.service;

import org.bukkit.entity.Player;
import pl.teksusik.customskins.data.Storage;
import pl.teksusik.customskins.model.CustomSkin;

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
    private final Storage storage;

    public SkinService(Storage storage) {
        this.storage = storage;
    }

    private static final String CREATE_QUERY = """
            CREATE TABLE IF NOT EXISTS `customskins_skins` (
              `skinOwner` varchar(36) NOT NULL,
              `skinName` varchar(51) NOT NULL,
              `skinTexture` longtext NOT NULL,
              `skinSignature` longtext NOT NULL,
              PRIMARY KEY (`skinOwner`,`skinName`)
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
                if (!resultSet.next())
                    return;
                this.addSkin(new CustomSkin(uuid,
                        resultSet.getString("skinName"),
                        resultSet.getString("skinTexture"),
                        resultSet.getString("skinOwner")));
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
}
