package com.example.explqrer;


import org.junit.*;
import org.junit.rules.TestRule;

import static org.junit.Assert.*;

import android.arch.core.executor.testing.InstantTaskExecutorRule;

public class DataHandlerTest {
    DataHandler dh;

    @Before
    public void setDh(){
        dh = new DataHandler();
    }

    @Test
    public void addQRTest(){
        dh.addQR("4ygnejl","username");
        dh.addQR("4ygnejl","username2");
    }
}
