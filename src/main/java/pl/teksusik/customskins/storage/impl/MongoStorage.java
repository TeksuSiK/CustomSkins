package pl.teksusik.customskins.storage.impl;

import com.mongodb.ConnectionString;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
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
    private final MongoCollection<Document> collection;

    public MongoStorage(String host, int port, String databaseName, String username, String password) {
        ConnectionString mongoUri = new ConnectionString(String.format("mongodb://%s:%s@%s:%s", username, password, host, port));
        MongoClient client = MongoClients.create(mongoUri);
        MongoDatabase database = client.getDatabase(databaseName);

        this.collection = database.getCollection("customskin");
    }

    @Override
    public Optional<CustomSkin> findSkin(UUID owner, String name) {
        Document document = this.collection.find(Filters.and(
            Filters.eq("owner", owner),
            Filters.eq("name", name))
        ).first();

        if (document == null) {
            return Optional.empty();
        }

        return Optional.of(new CustomSkin(document.get("owner", UUID.class),
            document.getString("name"),
            document.getString("texture"),
            document.getString("signature")));
    }

    @Override
    public Collection<CustomSkin> getAllSkinsByOwner(UUID owner) {
        FindIterable<Document> iterable = this.collection.find(Filters.eq("owner", owner));
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
        this.collection.insertOne(document);
        return skin;
    }

    @Override
    public void deleteSkin(CustomSkin skin) {
        this.collection.deleteOne(Filters.and(
            Filters.eq("owner", skin.getOwner()),
            Filters.eq("name", skin.getName()))
        );
    }

    @Override
    public int countSkins() {
        return (int) this.collection.countDocuments();
    }
}
