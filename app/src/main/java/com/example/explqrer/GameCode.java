package com.example.explqrer;

import static com.google.firebase.crashlytics.buildtools.reloc.com.google.common.math.IntMath.pow;

import android.location.Location;

import androidx.annotation.NonNull;

import com.google.android.gms.common.util.Hex;
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.hash.HashFunction;
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.hash.Hashing;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.codec.digest.DigestUtils;
import com.google.mlkit.vision.barcode.common.Barcode;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

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

    GameCode(@NonNull Barcode barcode, Location location, Photo photo, Player player) {
        sha256hex = hash.hashBytes(barcode.getRawBytes()).toString();
        score = calculateScore();
    }
    GameCode(@NonNull Barcode barcode, Location location, Player player) {
        sha256hex = hash.hashBytes(barcode.getRawBytes()).toString();
        score = calculateScore();
    }
    GameCode(@NonNull Barcode barcode, Photo photo, Player player) {
        sha256hex = hash.hashBytes(barcode.getRawBytes()).toString();
        score = calculateScore();
    }
    GameCode(@NonNull Barcode barcode, Player player) {
        sha256hex = hash.hashBytes(barcode.getRawBytes()).toString();
        score = calculateScore();
    }


    GameCode(String rawValue) {
        sha256hex = hash.hashString(rawValue, StandardCharsets.US_ASCII).toString();
        score = calculateScore();
    }

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

    public int getScore() {
        return score;
    }

    public String getSha256hex() {
        return sha256hex;
    }
}
