package com.example.explqrer;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Class to handle the reading and writing of data
 */
public class DataHandler {
    FirebaseFirestore db;
    CollectionReference totalQr, pts, unique;

    DataHandler(){
        db = FirebaseFirestore.getInstance();
    }


}
