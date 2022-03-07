package com.example.explqrer;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class ScannerTest {
    Scanner scanner;

    @Before
    public void setup(){ scanner = new Scanner("BFG5DGW54\n"); }

    @Test
    public void testHash() {
        scanner.hashQrCode();
        assertEquals("696ce4dbd7bb57cbfe58b64f530f428b74999cb37e2ee60980490cd9552de3a6", scanner.getQrHash());
    }
}


