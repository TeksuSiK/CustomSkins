package pl.teksusik.customskins.model;

import java.util.UUID;

public class CustomSkin {
    private UUID skinOwner;
    private String skinName;
    private String skinTexture;
    private String skinSignature;

    public CustomSkin(UUID skinOwner, String skinName, String skinTexture, String skinSignature) {
        this.skinOwner = skinOwner;
        this.skinName = skinName;
        this.skinTexture = skinTexture;
        this.skinSignature = skinSignature;
    }

    public UUID getSkinOwner() {
        return skinOwner;
    }

    public void setSkinOwner(UUID skinOwner) {
        this.skinOwner = skinOwner;
    }

    public String getSkinName() {
        return skinName;
    }

    public void setSkinName(String skinName) {
        this.skinName = skinName;
    }

    public String getSkinTexture() {
        return skinTexture;
    }

    public void setSkinTexture(String skinTexture) {
        this.skinTexture = skinTexture;
    }

    public String getSkinSignature() {
        return skinSignature;
    }

    public void setSkinSignature(String skinSignature) {
        this.skinSignature = skinSignature;
    }
}
