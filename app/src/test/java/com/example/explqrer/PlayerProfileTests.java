package com.example.explqrer;

import static org.junit.Assert.*;

import org.junit.*;

public class PlayerProfileTests {
    PlayerProfile player;
    GameCode code;

    @Before
    public void setUp() throws Exception {
        player = new PlayerProfile("John Doe", "123@teleworm.us");
        code = new GameCode("BFG5DGW54\n");
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void getName() {
        assertEquals("John Doe", player.getName());
    }

    @Test
    public void setName() {
        player.setName("Jane Doe");
        assertEquals("Jane Doe", player.getName());
    }

    @Test
    public void getContact() {
        assertEquals("123@teleworm.us", player.getContact());
    }

    @Test
    public void setContact() {
        player.setContact("321@teleworm.us");
        assertEquals("321@teleworm.us", player.getContact());
    }

    @Test
    public void getPoints() {
        player.addCode(code);
        assertEquals(111, player.getPoints());
    }

    @Test
    public void getQrHashes() {
        assertTrue("List is not empty", player.getQrHashes().isEmpty());
        player.addCode(code);
        assertEquals("Hashes not equal","696ce4dbd7bb57cbfe58b64f530f428b74999cb37e2ee60980490cd9552de3a6",
                player.getQrHashes().get(0));
        assertThrows(IndexOutOfBoundsException.class, () -> player.getQrHashes().get(1));
    }

    @Test
    public void addCode() {
        assertTrue(player.addCode(code));
    }

    @Test
    public void removeQrByHash() {
        player.addCode(code);
        assertTrue(player.removeQrByHash("696ce4dbd7bb57cbfe58b64f530f428b74999cb37e2ee60980490cd9552de3a6"));
        assertEquals(0, player.getPoints());
    }
}