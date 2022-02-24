package com.example.explqrer;

import android.location.Location;

import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.hash.HashFunction;
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.hash.Hashing;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.codec.digest.DigestUtils;
import com.google.mlkit.vision.barcode.common.Barcode;

import java.util.ArrayList;

public class GameCode {
    private String sha256hex;
    private int score;
    private Location location;
    // TODO: Add photos
    // private linkToPhotos
    // TODO: Add comments
    // private linkToComments
    private ArrayList<String> scannedByList;

    GameCode(Barcode barcode) {
        HashFunction hash =  Hashing.sha256();
        sha256hex = hash.hashBytes(barcode.getRawBytes()).toString();

    }
}
