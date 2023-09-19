package pl.teksusik.customskins.storage.impl;

import com.mongodb.ConnectionString;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.conversions.Bson;
import pl.teksusik.customskins.skin.CustomSkin;
import pl.teksusik.customskins.storage.Storage;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;

public class MongoStorage implements Storage {
    private final MongoCollection<Document> skinCollection;
    private final MongoCollection<Document> i18nCollection;

    public MongoStorage(String host, int port, String databaseName, String username, String password) {
        ConnectionString mongoUri = new ConnectionString(String.format("mongodb://%s:%s@%s:%s", username, password, host, port));
        MongoClient client = MongoClients.create(mongoUri);
        MongoDatabase database = client.getDatabase(databaseName);

        this.skinCollection = database.getCollection("customskin");
        this.i18nCollection = database.getCollection("customskin_i18n");
    }

    @Override
    public Optional<CustomSkin> findSkin(UUID owner, String name) {
        Document document = this.skinCollection.find(Filters.and(
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
        FindIterable<Document> iterable = this.skinCollection.find(Filters.eq("owner", owner));
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
        this.skinCollection.insertOne(document);
        return skin;
    }

    @Override
    public void deleteSkin(CustomSkin skin) {
        this.skinCollection.deleteOne(Filters.and(
            Filters.eq("owner", skin.getOwner()),
            Filters.eq("name", skin.getName()))
        );
    }

    @Override
    public int countSkins() {
        return (int) this.skinCollection.countDocuments();
    }

    @Override
    public Optional<String> findLocale(UUID owner) {
        Document document = this.i18nCollection.find(
            Filters.eq("owner", owner)
        ).first();

        if (document == null) {
            return Optional.empty();
        }

        return Optional.of(document.getString("locale"));
    }

    @Override
    public String setLocale(UUID owner, String locale) {
        Bson filter = Filters.eq("owner", owner);
        Bson update = Updates.set("locale", locale);

        this.i18nCollection.updateOne(filter, update, new UpdateOptions().upsert(true));
        return locale;
    }
}
