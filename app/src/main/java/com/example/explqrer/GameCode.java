package com.example.explqrer;

import static com.google.firebase.crashlytics.buildtools.reloc.com.google.common.math.IntMath.pow;

import android.location.Location;
import android.media.Image;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.common.util.Hex;
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.hash.HashFunction;
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.hash.Hashing;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.codec.digest.DigestUtils;
import com.google.mlkit.vision.barcode.common.Barcode;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

/**
 * Store information about a code scanned by a player, so that it
 * can be displayed to the player. Also updates the database on creation
 * with a new image and/or location.
 */
public class GameCode {
    private final String sha256hex;

    private final int score;
    private Location location;
    // TODO: Add photos
    // private linkToPhotos
    // TODO: Add comments
    // private linkToComments
    private ArrayList<String> scannedByList;
    HashFunction hash =  Hashing.sha256();

    /**
     * Constructor for GameCode.
     * @param barcode   a scanned barcode to be recorded
     * @param location  geolocation information. Can be null
     * @param photo     an image of the location of the barcode. Can be null
     * @param player    the player scanning the code
     */
//    public GameCode(@NonNull Barcode barcode, @NonNull Player player, @Nullable Location location, @Nullable Image photo) {
//        sha256hex = hash.hashBytes(barcode.getRawBytes()).toString();
//        // TODO: Check Database for hash
//        score = calculateScore();
//    }


    GameCode(String rawValue) {
        sha256hex = hash.hashString(rawValue, StandardCharsets.US_ASCII).toString();
        score = calculateScore();
    }

    /**
     * Helper function used to calculate the score of the hash string
     * @return The score as an integer
     */
    private int calculateScore() {
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
}
