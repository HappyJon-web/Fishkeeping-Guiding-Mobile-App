package com.example.fishkeeping.ui.fish;

public class ModelFish {
    private int fish_id, aquaID, fishQuantity;
    private String fishName, fishSpecies, fishGender;
    byte[] fishImage;

    public ModelFish(int fish_id, int aquaID,  String fishName, String fishSpecies,
                     String fishGender, int fishQuantity, byte[] fishImage) {
        this.fish_id = fish_id;
        this.aquaID = aquaID;
        this.fishQuantity = fishQuantity;
        this.fishName = fishName;
        this.fishSpecies = fishSpecies;
        this.fishGender = fishGender;
        this.fishImage = fishImage;
    }

    public int getFish_id() {
        return fish_id;
    }

    public void setFish_id(int fish_id) {
        this.fish_id = fish_id;
    }

    public int getAquaID() {
        return aquaID;
    }

    public void setAquaID(int aquaID) {
        this.aquaID = aquaID;
    }

    public int getFishQuantity() {
        return fishQuantity;
    }

    public void setFishQuantity(int fishQuantity) {
        this.fishQuantity = fishQuantity;
    }

    public String getFishName() {
        return fishName;
    }

    public void setFishName(String fishName) {
        this.fishName = fishName;
    }

    public String getFishSpecies() {
        return fishSpecies;
    }

    public void setFishSpecies(String fishSpecies) {
        this.fishSpecies = fishSpecies;
    }

    public String getFishGender() {
        return fishGender;
    }

    public void setFishGender(String fishGender) {
        this.fishGender = fishGender;
    }

    public byte[] getFishImage() {
        return fishImage;
    }

    public void setFishImage(byte[] fishImage) {
        this.fishImage = fishImage;
    }
}
