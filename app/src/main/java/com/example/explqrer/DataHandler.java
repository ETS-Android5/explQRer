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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataHandler {
    private FirebaseFirestore db;

    public DataHandler(){
        db = FirebaseFirestore.getInstance();
    }

    /*
     * TODO: # of QRs scanned leaderboard
     * TODO: pts leaderboard
     * TODO: Highest Unique QRs scanned leader board
     * TODO: Player info database
     * TODO: QR code database
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
}
