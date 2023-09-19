package pl.teksusik.customskins.storage.impl;

import com.zaxxer.hikari.HikariDataSource;
import pl.teksusik.customskins.skin.CustomSkin;
import pl.teksusik.customskins.storage.Storage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;

public abstract class SQLStorage implements Storage {
    protected HikariDataSource hikariDataSource;

    public void createTableIfNotExists() {
        try (final Connection connection = this.hikariDataSource.getConnection();
             final PreparedStatement createSkinsTable = connection.prepareStatement("CREATE TABLE IF NOT EXISTS customskins_skins (owner varchar(36) NOT NULL, name varchar(51) NOT NULL, texture longtext NOT NULL, signature longtext NOT NULL);");
             final PreparedStatement createI18nTable = connection.prepareStatement("CREATE TABLE IF NOT EXISTS customskins_i18n (owner varchar(36) NOT NULL PRIMARY KEY, locale VARCHAR(10));")) {
            createSkinsTable.executeUpdate();
            createI18nTable.executeUpdate();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public Optional<CustomSkin> findSkin(UUID owner, String name) {
        return this.getAllSkinsByOwner(owner).stream()
            .filter(skin -> skin.getName().equalsIgnoreCase(name))
            .findFirst();
    }

    @Override
    public Collection<CustomSkin> getAllSkinsByOwner(UUID owner) {
        try (final Connection connection = this.hikariDataSource.getConnection();
             final PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM customskins_skins WHERE owner = ?")) {
            preparedStatement.setString(1, owner.toString());
            Collection<CustomSkin> skins = new HashSet<>();

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    skins.add(new CustomSkin(owner,
                        resultSet.getString("name"),
                        resultSet.getString("texture"),
                        resultSet.getString("signature")));
                }
            }

            return skins;
        } catch (SQLException exception) {
            exception.printStackTrace();
        }

        return Collections.emptySet();
    }

    @Override
    public CustomSkin insertSkin(CustomSkin skin) {
        try (final Connection connection = this.hikariDataSource.getConnection();
             final PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO customskins_skins VALUES (?, ?, ?, ?)")) {
            preparedStatement.setString(1, skin.getOwner().toString());
            preparedStatement.setString(2, skin.getName());
            preparedStatement.setString(3, skin.getTexture());
            preparedStatement.setString(4, skin.getSignature());
            preparedStatement.executeUpdate();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }

        return skin;
    }

    @Override
    public void deleteSkin(CustomSkin skin) {
        try (final Connection connection = this.hikariDataSource.getConnection();
             final PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM customskins_skins WHERE owner = ? AND name = ?")) {
            preparedStatement.setString(1, skin.getOwner().toString());
            preparedStatement.setString(2, skin.getName());
            preparedStatement.executeUpdate();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public int countSkins() {
        try (Connection connection = this.hikariDataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT COUNT(*) FROM customskins_skins");
             ResultSet resultSet = preparedStatement.executeQuery()) {
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }

        return 0;
    }

    @Override
    public Optional<String> findLocale(UUID owner) {
        try (Connection connection = this.hikariDataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT locale FROM customskins_i18n WHERE owner = ?")) {
            preparedStatement.setString(1, owner.toString());

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    String locale = resultSet.getString("locale");
                    return Optional.of(locale);
                }
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }

        return Optional.empty();
    }

    @Override
    public String setLocale(UUID owner, String locale) {
        try (Connection connection = this.hikariDataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO customskins_i18n VALUES (?, ?) ON CONFLICT (owner) DO UPDATE SET locale = ?")) {
            preparedStatement.setString(1, owner.toString());
            preparedStatement.setString(2, locale);
            preparedStatement.setString(3, locale);
            preparedStatement.executeUpdate();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }

        return locale;
    }
}
