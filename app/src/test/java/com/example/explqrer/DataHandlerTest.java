package com.example.explqrer;


import org.junit.*;
import org.junit.rules.TestRule;
import org.mockito.Mock;


import static org.junit.Assert.*;

import android.app.Application;
import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.content.Context;
import android.test.mock;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;

public class DataHandlerTest {
    DataHandler dh;

    @Before
    public void setDh(){
        MockContext
        FirebaseApp.initializeApp(null);
        dh = new DataHandler();
    }

    @Test
    public void addQRTest(){
        dh.addQR("4ygnejl","username");
        dh.addQR("4ygnejl","username2");
    }
}
