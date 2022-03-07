package com.example.explqrer;

import static android.content.ContentValues.TAG;

import android.nfc.Tag;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataHandler {
    final private FirebaseFirestore db;

    public DataHandler(){
        db = FirebaseFirestore.getInstance();
    }

    /*
     * TODO: # of QRs scanned leaderboard
     * TODO: pts leaderboard
     * TODO: Highest Unique QRs scanned leader board
     * Player info database
     * QR code database
     */

    // QR code database
    public void addQR(String hash,String username){
        // Check if it exists if it does add username or add qr and username

        // Connect to collection
        CollectionReference cr = db.collection("qrbase");

        // Get the document
        DocumentReference docRef = cr.document(hash);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot documentSnapshot = task.getResult();
                    // Check if the document exists, add username if it does
                    if(documentSnapshot.exists()){
                        docRef.update("users", FieldValue.arrayUnion(username));
                    }
                    else{
                        Map<String,Object> data = new HashMap<>();
                        ArrayList<String> usernames = new ArrayList<>();
                        usernames.add(username);
                        data.put("users", usernames);
                        docRef.set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Log.d(TAG, "Success");
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "Failure");
                            }
                        });
                    }
                }
            }
        });

    }

    // Function to get all the qr codes
    public Map<String,Object> getQR(){
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
                }
            }
        });
        return qrs;
    }

    // Player Info database

    // Function to create new player
    public void createPlayer(String username, String contact){
        // Collection reference
        CollectionReference cr = db.collection("player");

        // Create hash map and add to document
        Map<String,Object> data = new HashMap<>();
        data.put("contact",contact);
        data.put("pts",0);
        data.put("scanned",0);
        data.put("ptsL",-1);
        data.put("qrL",-1);
        data.put("uniqueL", -1);

        cr.document(username).set(data);
    }

    // Function to get a specific player info
    // Will throw error if player doesnt exist
    // TODO: handle error part
    public Map<String,Object> getPlayer(String username){
        // Collection reference
        CollectionReference cr = db.collection("player");

        //Get the data of the specific player
        final Map<String, Object>[] data = new Map[]{new HashMap<>()};

        DocumentReference dr = cr.document(username);

        dr.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot doc = task.getResult();
                    if(doc.exists()){
                        data[0] = doc.getData();
                    }
                }
            }
        });

        // Return the hashmap
        return data[0];
    }

    // Function to update the pts
    public void updatePts(String username, long pts){
        // Collection ref
        CollectionReference cr = db.collection("player");

        // Document reference
        DocumentReference dr = cr.document(username);

        dr.update("pts",FieldValue.increment(pts));

        // TODO: Update ptsL
        this.updatePtsL();
    }

    // Function to update scanned
    public void updateScanned(String username, long scanned){
        // Collection Ref
        CollectionReference cr = db.collection("player");

        // Document reference
        DocumentReference dr = cr.document(username);

        dr.update("scanned",FieldValue.increment(scanned));

        // TODO: Update qrL , uniqueL
    }

    // TODO: Pts leader board
    // TODO: get leader board
    // update ptsL,
    // get ptsL for player
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

    public long getPtsL(String username){
        // Collection Reference
        CollectionReference cr = db.collection("player");

        // Doc reference
        DocumentReference dr = cr.document(username);

        // Get the ptsL and store it
        final long[] ptsL = {0};

        dr.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot doc = task.getResult();
                    if(doc.exists()){
                        ptsL[0] = (long) doc.getData().get("ptsL");
                    }
                }
            }
        });

        return ptsL[0];
    }

    public ArrayList<String> getPtsLeaderBoard(){
        // Hashmap to return
        ArrayList<String> leaderboard = new ArrayList<>();

        // Collection reference
        CollectionReference cr = db.collection("player");

        cr.orderBy("ptsL", Query.Direction.ASCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot doc: task.getResult()){
                        leaderboard.add(doc.getId());
                    }
                }
            }
        });

        return leaderboard;
    }


    // TODO: # of QRs scanned leaderboard
    // TODO: get leader board, update qrL, get qrL for player



    // TODO: Highest Unique QRs scanned leader board
    // TODO: get leader board, update uniqueL, get uniqueL for player
}
