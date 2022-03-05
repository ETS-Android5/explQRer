package com.example.explqrer;


import com.google.mlkit.vision.barcode.common.Barcode;

import org.junit.*;
import static org.junit.Assert.*;

public class GameCodeTest {
    GameCode code;

    @Before
    public void setup() {
        code = new GameCode("BFG5DGW54\n");
    }

    @Test
    public void testHash() {
        assertEquals("696ce4dbd7bb57cbfe58b64f530f428b74999cb37e2ee60980490cd9552de3a6", code.getSha256hex());
    }

    @Test
    public void testCalculateScore() {
        assertEquals(111, code.getScore());
    }
}
