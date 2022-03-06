package com.example.explqrer;

import android.location.Location;

public class Scanner {
    private String qrHash;
    private GameCode code;
    private Location location;

    // TODO: Link to photos

    public Scanner(String qr){
        this.qrHash = "";
        this.code = new GameCode(qr);
    }

    public void hashQrCode(){
        this.qrHash = code.getSha256hex();
    }

    public String getQrHash() {
        return qrHash;
    }

    public void setQrHash(String qrHash) {
        this.qrHash = qrHash;
    }

    public GameCode getCode() {
        return code;
    }

    public void setCode(GameCode code) {
        this.code = code;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
// create the qr code obj
// Hash it
// TODO: Look for existing match
// TODO: -If it exist add player username
// Add location function
// TODO: Add photo
