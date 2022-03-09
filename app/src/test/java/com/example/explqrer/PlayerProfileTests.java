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
    public void getCodes() {
        assertTrue("List is not empty", player.getCodes().isEmpty());
        player.addCode(code);
        assertTrue("Hashes not equal", player.getCodes().contains(code));
        assertFalse("Unexpected item in set", player.getCodes().contains(new GameCode("123@teleworm.us")));
    }

    @Test
    public void addCode() {
        assertTrue(player.addCode(code));
    }

    @Test
    public void removeCode() {
        player.addCode(code);
        assertTrue(player.removeCode(code));
        assertEquals(0, player.getPoints());
    }

    @Test
    public void getHighestCode() {
        GameCode newCode2 = new GameCode("Finding a code with a hash that scores higher than 111 is hard");
        player.addCode(newCode2);
        assertEquals(newCode2, player.getHighestCode());
        GameCode newCode = new GameCode("Hello World");
        player.addCode(newCode);
        assertEquals(newCode2, player.getHighestCode());
        player.addCode(code);
        assertEquals(code, player.getHighestCode());
    }

    @Test
    public void getLowestCode() {
        GameCode newCode = new GameCode("Finding a code with a hash that scores higher than 111 is hard");
        player.addCode(newCode);
        assertEquals(newCode, player.getLowestCode());
        player.addCode(code);
        assertEquals(newCode, player.getLowestCode());
        GameCode newCode2 = new GameCode("Hello World");
        player.addCode(newCode2);
        assertEquals(newCode2, player.getLowestCode());
    }

    @Test
    public void removeLowest() {
        player.addCode(code);
        GameCode newCode = new GameCode("Hello World");
        player.addCode(newCode);
        assertEquals(code, player.getHighestCode());
        assertEquals(newCode, player.getLowestCode());
        player.removeCode(newCode);
        assertEquals(code, player.getHighestCode());
        assertEquals(code, player.getLowestCode());
    }

    @Test
    public void removeAll() {
        player.addCode(code);
        player.removeCode(code);
        assertNull(player.getHighestCode());
        assertNull(player.getLowestCode());
    }
}