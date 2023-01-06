package pl.teksusik.customskins.skin;

import java.util.UUID;

public class CustomSkin {
    private UUID owner;
    private String name;
    private String texture;
    private String signature;

    public CustomSkin(UUID owner, String name, String texture, String signature) {
        this.owner = owner;
        this.name = name;
        this.texture = texture;
        this.signature = signature;
    }

    public UUID getOwner() {
        return owner;
    }

    public void setOwner(UUID owner) {
        this.owner = owner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTexture() {
        return texture;
    }

    public void setTexture(String texture) {
        this.texture = texture;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }
}
