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
    /**
     * Returns the String value of QrHash.
     *  @return qrHash, a String value.
     */
    public String getQrHash() {
        return qrHash;
    }
    /**
     * This set method sets the qrHash.
     *  @param qrHash a String.
     */
    public void setQrHash(String qrHash) {
        this.qrHash = qrHash;
    }
    /**
     * Returns the String value of Code.
     *  @return code, a String value.
     */
    public GameCode getCode() {
        return code;
    }
    /**
     * This set method sets the code.
     *  @param code a String.
     */
    public void setCode(GameCode code) {
        this.code = code;
    }
    /**
     * Returns the String value of Location.
     *  @return location, a String value.
     */
    public Location getLocation() {
        return location;
    }
    /**
     * This set method sets the location.
     *  @param location a String.
     */
    public void setLocation(Location location) {
        this.location = location;
    }
}

// create the qr code object
// Hash it
// TODO: Look for existing match
// TODO: -If it exist add player username
// Add location function
// TODO: Add photo
