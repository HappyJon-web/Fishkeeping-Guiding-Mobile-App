package com.example.fishkeeping.ui.aquarium;

public class ModelAquarium {
    private int aquaID, aquaLength, aquaWidth, aquaHeight, aquaFishNo;
    private float aquaVolume;
    private String aquaName, aquaType;
    private byte[] aquaImage;

    public ModelAquarium(int aquaID, String aquaName, String aquaType, int aquaLength, int aquaWidth,
                         int aquaHeight, float aquaVolume, byte[] aquaImage, int aquaFishNo) {
        this.aquaID = aquaID;
        this.aquaLength = aquaLength;
        this.aquaWidth = aquaWidth;
        this.aquaHeight = aquaHeight;
        this.aquaFishNo = aquaFishNo;
        this.aquaVolume = aquaVolume;
        this.aquaName = aquaName;
        this.aquaType = aquaType;
        this.aquaImage = aquaImage;
    }

    public int getAquaID() {
        return aquaID;
    }

    public void setAquaID(int aquaID) {
        this.aquaID = aquaID;
    }

    public int getAquaLength() {
        return aquaLength;
    }

    public void setAquaLength(int aquaLength) {
        this.aquaLength = aquaLength;
    }

    public int getAquaWidth() {
        return aquaWidth;
    }

    public void setAquaWidth(int aquaWidth) {
        this.aquaWidth = aquaWidth;
    }

    public int getAquaHeight() {
        return aquaHeight;
    }

    public void setAquaHeight(int aquaHeight) {
        this.aquaHeight = aquaHeight;
    }

    public int getAquaFishNo() {
        return aquaFishNo;
    }

    public void setAquaFishNo(int aquaFishNo) {
        this.aquaFishNo = aquaFishNo;
    }

    public float getAquaVolume() {
        return aquaVolume;
    }

    public void setAquaVolume(float aquaVolume) {
        this.aquaVolume = aquaVolume;
    }

    public String getAquaName() {
        return aquaName;
    }

    public void setAquaName(String aquaName) {
        this.aquaName = aquaName;
    }

    public String getAquaType() {
        return aquaType;
    }

    public void setAquaType(String aquaType) {
        this.aquaType = aquaType;
    }

    public byte[] getAquaImage() {
        return aquaImage;
    }

    public void setAquaImage(byte[] aquaImage) {
        this.aquaImage = aquaImage;
    }
}
