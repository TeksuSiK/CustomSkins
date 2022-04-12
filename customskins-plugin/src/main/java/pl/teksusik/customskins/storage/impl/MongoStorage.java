package pl.teksusik.customskins.storage.impl;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import pl.teksusik.customskins.skin.CustomSkin;
import pl.teksusik.customskins.storage.Storage;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;

public class MongoStorage implements Storage {
    private final MongoClient client;
    private final MongoDatabase database;

    public MongoStorage(String host, int port) {
        this.client = new MongoClient(host, port);
        this.database = this.client.getDatabase("customskins");
    }

    @Override
    public Optional<CustomSkin> findSkin(UUID owner, String name) {
        Document document = this.database.getCollection("customskin").find(Filters.and(
            Filters.eq("owner", owner),
            Filters.eq("name", name))
        ).first();

        return Optional.of(new CustomSkin(document.get("owner", UUID.class),
            document.getString("name"),
            document.getString("texture"),
            document.getString("signature")));
    }

    @Override
    public Collection<CustomSkin> getAllSkinsByOwner(UUID owner) {
        FindIterable<Document> iterable = this.database.getCollection("customskin").find(Filters.eq("owner", owner));
        Collection<CustomSkin> skins = new HashSet<>();
        for (Document document : iterable) {
            CustomSkin customSkin = new CustomSkin(document.get("owner", UUID.class),
                document.getString("name"),
                document.getString("texture"),
                document.getString("signature"));
            skins.add(customSkin);
        }

        return skins;
    }

    @Override
    public CustomSkin insertSkin(CustomSkin skin) {
        Document document = new Document("owner", skin.getOwner())
            .append("name", skin.getName())
            .append("texture", skin.getTexture())
            .append("signature", skin.getSignature());
        this.database.getCollection("customskin").insertOne(document);
        return skin;
    }

    @Override
    public void deleteSkin(CustomSkin skin) {
        this.database.getCollection("customskin").deleteOne(Filters.and(
            Filters.eq("owner", skin.getOwner()),
            Filters.eq("name", skin.getName()))
        );
    }
}
