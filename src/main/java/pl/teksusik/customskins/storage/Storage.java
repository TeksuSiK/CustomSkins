package pl.teksusik.customskins.storage;

import pl.teksusik.customskins.skin.CustomSkin;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface Storage {
    Optional<CustomSkin> findSkin(UUID owner, String name);
    Collection<CustomSkin> getAllSkinsByOwner(UUID owner);
    CustomSkin insertSkin(CustomSkin skin);
    void deleteSkin(CustomSkin skin);
    int countSkins();
}
