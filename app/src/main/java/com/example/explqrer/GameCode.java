package com.example.explqrer;

import static com.google.common.math.IntMath.pow;

import android.graphics.Bitmap;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;

/**
 * Store information about a code scanned by a player, so that it
 * can be displayed to the player. Also updates the database on creation
 * with a new image and/or location.
 */
public class GameCode implements Serializable {
    private final String sha256hex;

    private final int score;
    private Location location;
    private Bitmap photo;
    private String description;
    // TODO: Add comments
    // private linkToComments
    // private ArrayList<String> scannedByList;
    static HashFunction hash =  Hashing.sha256();

    /**
     * Constructor for GameCode.
     * @param barcode   a scanned barcode to be recorded
     * @param location  geolocation information. Can be null
     * @param photo     an image of the location of the barcode. Can be null
     * @param player    the player scanning the code
     */
    public GameCode(@NonNull String barcode, @NonNull String player,
                    @Nullable Location location, @Nullable Bitmap photo) {
        sha256hex = hash.hashString(barcode, StandardCharsets.UTF_8).toString();
        // TODO: Check Database for hash
        score = calculateScore(barcode);
        this.location = location;
        this.photo = photo;
    }



    GameCode(String rawValue) {
        sha256hex = hash.hashString(rawValue, StandardCharsets.US_ASCII).toString();
        score = calculateScore(rawValue);
    }

    /**
     * Calculate the score of the hash string
     * @return The score as an integer
     */
    public static int calculateScore(String rawValue) {
        HashFunction hash =  Hashing.sha256();
        String sha256hex = hash.hashString(rawValue, StandardCharsets.US_ASCII).toString();
        int ret = 0;
        int repeats = 0;
        char prevChar = sha256hex.charAt(0);

        for (int i = 1; i < sha256hex.length(); i++) {
            if (sha256hex.charAt(i) == prevChar) {
                repeats++;
            }
            else {
                if (repeats > 0) {
                    ret += pow(Integer.parseInt(String.valueOf(prevChar), 16), repeats);
                }
                repeats = 0;
            }
            prevChar = sha256hex.charAt(i);
        }
        if (repeats > 0) {
            ret += Integer.parseInt(String.valueOf(prevChar), 16);
        }
        return ret;
    }

    /**
     * Get the score
     * @return The score as an integer
     */
    public int getScore() {
        return score;
    }

    /**
     * Get the hash
     * @return The hash as a string
     */
    public String getSha256hex() {
        return sha256hex;
    }

    /**
     * Get the stored location of the code
     * @return
     */
    public Location getLocation() {
        return location;
    }

    /**
     * Get the bitmap of the image stored
     * @return
     */
    public Bitmap getPhoto() {
        return photo;
    }

    /**
     * Set the location info
     * @param location
     */
    public void setLocation(Location location) {
        this.location = location;
    }

    /**
     * Set the photo with a bitmap
     * @param photo
     */
    public void setPhoto(Bitmap photo) {
        this.photo = photo;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Return true if the object equals the GameCode
     * @param o the object to check
     * @return true if they are equal
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameCode gameCode = (GameCode) o;
        return getSha256hex().equals(gameCode.getSha256hex());
    }
}
