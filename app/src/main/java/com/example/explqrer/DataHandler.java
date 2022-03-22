package com.example.explqrer;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DataHandler {

    // Firestore Objects
    final private FirebaseFirestore db;
    final private FirebaseStorage storage;

    public DataHandler(){
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
    }

    /*
     * # of QRs scanned leaderboard
     * pts leaderboard
     * Highest Unique QRs scanned leader board
     * Player info database
     * QR code database
     */

    // QR code database

    /**
     * Method to add the user to the QRcode when a user scans it
     * @param code
     *  This is the hash of the QRcode
     * @param username
     *  This is the username of the user that has scanned the QRcode
     */
    public void addQR(GameCode code, String username, PlayerProfile playerProfile){
        // Check if it exists if it does add username or add qr and username

        String hash = code.getSha256hex();
        // Connect to collection
        CollectionReference cr = db.collection("qrbase");

        // Get the document
        DocumentReference docRef = cr.document(hash);
        docRef.get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                DocumentSnapshot documentSnapshot = task.getResult();
                // Check if the document exists, add username if it does
                if(documentSnapshot.exists()){
                    docRef.update("users", FieldValue.arrayUnion(username));
                    if(documentSnapshot.getData().get("location") == null){
                        docRef.update("location",code.getLocation().getProvider());
                    }
                }
                else{
                    Map<String,Object> data = new HashMap<>();
                    ArrayList<String> usernames = new ArrayList<>();
                    usernames.add(username);
                    data.put("users", usernames);
                    if (code.getLocation() == null){
                        data.put("location",code.getLocation());
                    }
                    else{
                        data.put("location",code.getLocation().getProvider());
                    }
                    docRef.set(data)
                            .addOnSuccessListener(unused -> Log.d(TAG, "Success"))
                            .addOnFailureListener(e -> Log.d(TAG, "Failure"));
                }
            }
        });
        // No need to upload photos anymore
//        if (code.getPhoto() != null) {
//            uploadImage(code, username);
//        }
        // Update the points
        updatePts(username,code.getScore());
        updateScanned(username,1);
        hasScannedBefore(code.getSha256hex(), username, new OnHasScannedBeforeListener() {
            @Override
            public void hasScannedBeforeListener(boolean flag) {
                if(!flag){
                    updateUniqueScanned(username,1);
                }
            }
        });

        //update player json
        updatePlayerJson(playerProfile);

    }

    // Function to get all the qr codes

    /**
     * Method to get all the qr hashes and the users that scanned that qr code
     *
     */
    public void getQR(OnGetQrsListener listener){
        CollectionReference cr = db.collection("qrbase");

        // Get the documents
        Map<String,Object> qrs = new HashMap<>();

        cr.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot doc: task.getResult()){
                        String qr = doc.getId();
                        ArrayList<String> usernames = (ArrayList<String>) doc.getData().get("users");
                        qrs.put(qr,usernames);
                    }
                    listener.onQrFilled(qrs);
                } else {
                    listener.onError(task.getException());
                }
            }
        });
    }

    // Function to get the qrs of a specific user

    /**
     * Method to get all the hashes of the QRs scanned by a specific user
     * @param username
     *  This is the username of the user
     * @return
     *  Arraylist with all the hashes of the QR codes
     */
    //TODO: Sorted lists
    public void userQrs(String username, OnUserQrsListener listener){
        this.getQR(new OnGetQrsListener() {
            @Override
            public void onQrFilled(Map<String,Object> map) {
                ArrayList<String> qrs = new ArrayList<>();
                System.out.println("in userqrs");
                System.out.println(map.keySet());
                for(String hash: map.keySet()){
                    ArrayList<String> users = (ArrayList<String>) map.get(hash);
                    for(String user: users) {
                        System.out.println(user);
                        if (user.equals(username)) {
                            qrs.add(hash);
                        }
                    }
                }
                listener.onUserQrsFilled(qrs);
            }

            @Override
            public void onError(Exception taskException) {
                // Handle error
            }
        });
    }

    // Player Info database

    // Function to create new player
    public void createPlayer(PlayerProfile playerProfile){
        // Collection reference
        CollectionReference cr = db.collection("player");

        // Create hash map and add to document
        Map<String,Object> data = new HashMap<>();
        data.put("contact",playerProfile.getContact());
        data.put("pts",0);
        data.put("scanned",0);
        data.put("uniqueScanned",0);
        data.put("ptsL",-1);
        data.put("qrL",-1);
        data.put("uniqueL", -1);
        Gson gson = new Gson();
        data.put("json", gson.toJson(playerProfile));
        cr.document(playerProfile.getName()).set(data);
    }

    public void updatePlayerJson(PlayerProfile playerProfile){
        // Collection Reference
        CollectionReference cr = db.collection("player");

        // Document reference
        DocumentReference docRef = cr.document(playerProfile.getName());

        Gson gson = new Gson();
        docRef.update("json", gson.toJson(playerProfile);
    }
    // Function to get a specific player info
    // Will return null if player doesnt exist

    public void updatePlayerUsername(String oldUsername, PlayerProfile newPlayerProfile){
        // Collection Reference
        CollectionReference cr = db.collection("player");

        // Document reference
        DocumentReference docRef = cr.document(oldUsername);

        // New document reference
        DocumentReference newDocRef = cr.document(newPlayerProfile.getName());

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot doc = task.getResult();
                    if(doc.exists()){
                        Map<String,Object> data = doc.getData();
                        newDocRef.set(data);
                        updatePlayerJson(newPlayerProfile);
                    }
                }
            }
        });

        // Delete old doc
        docRef.delete();

    }

    /**
     * This function returns all the data that is stored about a user
     * @param username
     *  This is the username of the user
     * @return
     *  It return a Hashmap of all the data of the user
     *  If it returns null that means the player doesnt exist, this can be used to check if
     *  the player exists or not.
     */
    public void getPlayer(String username, OnGetPlayerListener listener){
        // Collection reference
        CollectionReference cr = db.collection("player");

        //Get the data of the specific player

        DocumentReference dr = cr.document(username);
        dr.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    Gson gson = new Gson();
                    PlayerProfile playerProfile;
                    DocumentSnapshot doc = task.getResult();
                    if(doc.exists()){
//                        data = doc.getData().get("json");
                        String json = (String) doc.getData().get("json");
                        playerProfile = gson.fromJson(json, PlayerProfile.class)
                    }
                    else{
                        playerProfile = null;
                    }
                    listener.getPlayerListener(playerProfile);
                }
                else{
                    listener.getPlayerListener(null);
                }
            }
        });
    }

    // Function to update the pts

    /**
     * Method to update the points of a specific player
     * @param username
     *  This is the username of the player
     * @param pts
     *  This is the point by which we have to update the points by
     */
    public void updatePts(String username, long pts){
        // Collection ref
        CollectionReference cr = db.collection("player");

        // Document reference
        DocumentReference dr = cr.document(username);

        dr.update("pts",FieldValue.increment(pts));

        // Update ptsL
        this.updatePtsL();
    }

    // Function to update scanned

    /**
     * Method to update the number of scanned qr codes
     * @param username
     *  This is the username of the player
     * @param scanned
     *  This is the number of qr codes scanned
     */
    public void updateScanned(String username, long scanned){
        // Collection Ref
        CollectionReference cr = db.collection("player");

        // Document reference
        DocumentReference dr = cr.document(username);

        dr.update("scanned",FieldValue.increment(scanned));

        // Update qrL
        this.updateQrL();
    }

    /**
     * This function updates the unique scanned of the user
     * @param username
     *  This is the username of the user
     * @param uniqueScanned
     *  This is the integer value by which we have to update the unique scanned
     */
    public void updateUniqueScanned(String username, long uniqueScanned){
        // Collection Ref
        CollectionReference cr = db.collection("player");

        // Document reference
        DocumentReference dr = cr.document(username);

        dr.update("uniqueScanned",FieldValue.increment(uniqueScanned));

        // Update uniqueL
        updateUniqueL();
    }

    // Pts leader board
    // get leader board
    // update ptsL,
    // get ptsL for player

    /**
     * Method to update the points leaderboard
     */
    public void updatePtsL(){
        // Collection ref
        CollectionReference cr = db.collection("player");

        // Update the ptsL for each player
        cr.orderBy("pts", Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    int pos = 1;
                    for(QueryDocumentSnapshot doc : task.getResult()){
                        String username = doc.getId();
                        DocumentReference dr = cr.document(username);
                        dr.update("ptsL",pos++);
                    }
                }
            }
        });
    }

    /**
     * Method to get the position of the user on the points leaderboard
     * @param username
     *  This is the username of the user
     * @return
     *  The position of the user on the leaderboard
     */
    public void getPtsL(String username, OnGetPtsLListener listener){
        // Collection Reference
        CollectionReference cr = db.collection("player");

        // Doc reference
        DocumentReference dr = cr.document(username);

        // Get the ptsL and store it


        dr.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    long ptsL = 0;
                    DocumentSnapshot doc = task.getResult();
                    if(doc.exists()){
                        ptsL = (long) doc.getData().get("ptsL");
                    }
                    listener.getPtsLListener(ptsL);
                }
                else {
                    // if the task is unsuccessful
                    listener.getPtsLListener(-1);
                }
            }
        });
    }

    /**
     * Method to get the points leader board
     * @return
     *  It returns an arraylist with the usernames of the users which represents the leaderboard
     */
    public void getPtsLeaderBoard(OnGetPtsLeaderBoardListener listener){

        // Collection reference
        CollectionReference cr = db.collection("player");

        cr.orderBy("ptsL", Query.Direction.ASCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    ArrayList<String> leaderboard = new ArrayList<>();
                    for(QueryDocumentSnapshot doc: task.getResult()){
                        leaderboard.add(doc.getId());
                    }
                    listener.getPtsLeaderBoardListener(leaderboard);
                }
                else {
                    listener.getPtsLeaderBoardListener(null);
                }
            }
        });
    }


    // # of QRs scanned leaderboard
    // get leader board, update qrL, get qrL for player

    /**
     * Method to update the qr leaderboard
     */
    public void updateQrL(){
        // Collection ref
        CollectionReference cr = db.collection("player");

        // Update the ptsL for each player
        cr.orderBy("scanned", Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    int pos = 1;
                    for(QueryDocumentSnapshot doc : task.getResult()){
                        String username = doc.getId();
                        DocumentReference dr = cr.document(username);
                        dr.update("qrL",pos++);
                    }
                }
            }
        });
    }

    /**
     * Method to get the position of the user on the qr scanned leaderboard
     * @param username
     *  This is the username of the user
     * @return
     *  The position of the user on the leaderboard
     */
    public void getQrL(String username, OnGetQrLListener listener){
        // Collection Reference
        CollectionReference cr = db.collection("player");

        // Doc reference
        DocumentReference dr = cr.document(username);

        // Get the ptsL and store it


        dr.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    long qrL = 0;
                    DocumentSnapshot doc = task.getResult();
                    if(doc.exists()){
                        qrL = (long) doc.getData().get("qrL");
                    }
                    listener.getQrLListener(qrL);
                }
                else {
                    // if there is error
                    listener.getQrLListener(-1);
                }
            }
        });
    }

    /**
     * Method to get the qr scanned leader board
     * @return
     *  It returns an arraylist with the usernames of the users which represents the leaderboard
     */
    public void getQrLeaderBoard(OnGetQrLeaderBoardListener listener){

        // Collection reference
        CollectionReference cr = db.collection("player");

        cr.orderBy("qrL", Query.Direction.ASCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    ArrayList<String> leaderboard = new ArrayList<>();
                    for(QueryDocumentSnapshot doc: task.getResult()){
                        leaderboard.add(doc.getId());
                    }
                    listener.getQrLeaderBoardListener(leaderboard);
                }
                else{
                    // if error
                    listener.getQrLeaderBoardListener(null);
                }
            }
        });
    }



    // Highest Unique QRs scanned leader board
    // get leader board, update uniqueL, get uniqueL for player

    /**
     * Method to get update the unique scanned leaderboard
     */
    public void updateUniqueL(){
        // Collection ref
        CollectionReference cr = db.collection("player");

        // Update the ptsL for each player
        cr.orderBy("uniqueScanned", Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    int pos = 1;
                    for(QueryDocumentSnapshot doc : task.getResult()){
                        String username = doc.getId();
                        DocumentReference dr = cr.document(username);
                        dr.update("uniqueL",pos++);
                    }
                }
            }
        });
    }

    /**
     * Method to get the position of the user on the Unique scanned leaderboard
     * @param username
     *  This is the username of the user
     * @return
     *  The position of the user on the leaderboard
     */
    public void getUniqueL(String username, OnGetUniqueLListener listener){
        // Collection Reference
        CollectionReference cr = db.collection("player");

        // Doc reference
        DocumentReference dr = cr.document(username);

        // Get the ptsL and store it
        dr.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    long qrL = 0;
                    DocumentSnapshot doc = task.getResult();
                    if(doc.exists()){
                        qrL = (long) doc.getData().get("uniqueL");
                    }
                    listener.getUniqueLListener(qrL);
                }
                else{
                    //If there is error
                    listener.getUniqueLListener(-1);
                }
            }
        });
    }

    /**
     * Method to get the unique scanned leader board
     * @return
     *  It returns an arraylist with the usernames of the users which represents the leaderboard
     */
    public void getUniqueLeaderBoard(OnGetUniqueLeaderBoardListener listener){

        // Collection reference
        CollectionReference cr = db.collection("player");

        cr.orderBy("uniqueL", Query.Direction.ASCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    ArrayList<String> leaderboard = new ArrayList<>();
                    for(QueryDocumentSnapshot doc: task.getResult()){
                        leaderboard.add(doc.getId());
                    }
                    listener.getUniqueLeaderBoardListener(leaderboard);
                }
                else{
                    // Error
                    listener.getUniqueLeaderBoardListener(null);
                }
            }
        });
    }

    /**
     * Method to check if the qr has been scanned before by a given user
     * @param hash
     *  The qr hash
     * @param username
     *  The name of the user
     * @return
     *  It returns true if a given user has scanned the qr code before or false if the
     *  user didn't scanned the qr before
     */
    public void hasScannedBefore(String hash, String username, OnHasScannedBeforeListener listener){
        // Connect to collection
        CollectionReference cr = db.collection("qrbase");

        // Get the document
        DocumentReference docRef = cr.document(hash);

        // Set the flag to false by default

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                boolean flag = false;
                if(task.isSuccessful()){
                    DocumentSnapshot doc = task.getResult();
                    if(doc.exists()){
                        ArrayList<String> usernames = (ArrayList<String>) doc.getData().get("users");
                        for(String user: usernames){
                            if (user.equals(username)) {
                                flag = true;
                                break;
                            }
                        }
                    }
                }
                listener.hasScannedBeforeListener(flag);
            }
        });
    }

    /**
     * Method to check if the qr code is being scanned for the first time
     * @param hash
     *  This is the hash of the qr code
     * @return
     *  Returns true if the qr code is being scanned for the first time else returns false
     */
    @Deprecated
    public Boolean firstScan(String hash){
        // Connect to collection
        CollectionReference cr = db.collection("qrbase");

        // Get the document
        DocumentReference docRef = cr.document(hash);

        // Set the flag to false by default
        final boolean[] flag = {true};
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot doc = task.getResult();
                    if(doc.exists()){
                        flag[0] = false;
                    }
                }
            }
        });
        return  flag[0];
    }

    // Image upload
    @Deprecated
    public void uploadImage(GameCode code, String username){
        // Connect to collection
        CollectionReference collectionReference = db.collection("images");

        String hash = code.getSha256hex();
        long pts = code.getScore();
        Bitmap image = code.getPhoto();
        // Document reference
        DocumentReference doc = collectionReference.document(hash);

        Map<String,Object> data = new HashMap<>();
        data.put("pts",pts);

        doc.set(data);

        if (image != null) {
            // Get the StorageReference
            StorageReference storageReference = storage.getReference();
            // Defining the child of storageReference
            StorageReference imageRef = storageReference.child("images/"+hash+".jpg");

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 70, baos);
            byte[] imageData = baos.toByteArray();

            // Upload the image
            imageRef.putBytes(imageData);
        }
    }

    // Method to get the point of a specific hash
    @Deprecated
    public long hashPts(String hash){
        // TODO: REMOVE
        // Connect to collection
        CollectionReference collectionReference = db.collection("images");

        // Document reference
        DocumentReference doc = collectionReference.document(hash);

        // Store the point
        final long[] pts = {0};

        // Get the document
        doc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot doc = task.getResult();
                    if(doc.exists()){
                        pts[0] =(long) doc.getData().get("pts");
                    }
                }
            }
        });

        return pts[0];
    }

    // Method to download the image
    // The method returns null if the image doesnt exist
    @Deprecated
    public File downloadImage(String hash){
        StorageReference storageReference = storage.getReference();
        StorageReference imageRef = storageReference.child("images/"+hash+".jpg");

        // https://firebase.google.com/docs/storage/android/download-files
        
        File localFile = null;
        try {
            localFile = File.createTempFile(hash, ".jpg");;
            imageRef.getFile(localFile);
            System.out.println("ref: "+ imageRef);


        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("path: "+ localFile);
        System.out.println("before return localfile");
        System.out.println("path: "+ localFile.getAbsolutePath());
        System.out.println("before return localfile path");
        return localFile;
    }
}