package com.example.explqrer;

import android.location.Location;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.HashMap;

public class PlayerProfile implements Serializable {

    private String name;
    private String contact;
    private long points;
    private GameCode highest, lowest;
    private HashMap<String, GameCode> codes;
    private boolean isAdmin;

    public PlayerProfile(String username, String Contact) {
        name = username;
        contact = Contact;
        points = 0;
        codes = new HashMap<>() ;
        isAdmin = false;
    }

    /**
     * Returns the String value of player name.
     *  @return playerName, a String value.
     */
    public String getName() {
        return name;
    }
    /**
     * This set method sets the name of the player.
     *  @param name a String as name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the String value of player info.
     *  @return playerInfo, a String value.
     */
    public String getContact() {
        return contact;
    }

    /**
     * This set method sets the player info.
     *  @param contact a String.
     */
    public void setContact(String contact) {
        this.contact = contact;
    }

    /**
     * Returns the long value of player points.
     *  @return playerInfo, a long value.
     */
    public long getPoints() {
        return points;
    }

    /**
     * Get the list of scanned code hashes
     * @return The scanned code hashes in an ArrayList
     */
    public HashMap<String, GameCode> getCodes() {
        return codes;
    }

    /**
     * Get the game code object from a player
     * @param hash
     * @return
     * Can return null
     */
    @Nullable
    public GameCode getCode(String hash) {
        return codes.get(hash);
    }

    /**
     * Add a GameCode to the PlayerProfile
     * @param code the code to add
     * @return true if the code was added, false otherwise
     */
    public boolean addCode(@NonNull GameCode code) {

        if (!codes.containsKey(code.getSha256hex())) {
            codes.put(code.getSha256hex(), code);
            int score = code.getScore();
            points += score;
            if (highest == null || score > highest.getScore()) {
                highest = code;
            }
            if (lowest == null || score < lowest.getScore()) {
                lowest = code;
            }
            return true;
        }
        return false;
    }

    /**
     * Remove a code from a player profile
     * @param code the code to remove
     * @return true if the code was successfully removed
     */
    public boolean removeCode(GameCode code) {
        if (codes.containsKey(code.getSha256hex())) {
            codes.remove(code.getSha256hex());
            // TODO: add some error checking
            points -= code.getScore();
            if (code == lowest || code == highest) {
                refreshProfile();
            }
            return true;
        }
        return false;
    }

    /**
     * get the highest scoring GameCode from the account
     * @return the aforementioned code
     */
    public GameCode getHighestCode() {
        return highest;
    }

    /**
     * get the lowest scoring GameCode from the account
     * @return the aforementioned code
     */
    public GameCode getLowestCode() {
        return lowest;
    }

    /**
     * refresh the profile, including highest and lowest codes,
     * as well as fetching new information from the database.
     */
    public void refreshProfile() {
        // TODO: fetch profile from database


        // refresh highest and lowest
        highest = lowest = null;
        for (GameCode code : codes.values()) {
            if (highest == null || code.getScore() > highest.getScore()) {
                highest = code;
            }
            if (lowest == null || code.getScore() < lowest.getScore()) {
                lowest = code;
            }
        }
    }

    /**
     * Get the total number of codes scanned by the player
     * @return the number of codes
     */
    public int getNumCodes() {
        return codes.size();
    }

    /**
     * Check if a player profile already contains a given code.
     * @param rawValue
     * @return
     */
    public boolean hasCode(String rawValue) {
        return codes.containsKey(new GameCode(rawValue));
    }

    public void addLocationToCode(GameCode code, Location location) {
        if (codes.containsKey(code)) {
            codes.get(code).setLocation(location);
        }
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAsAdmin(){
        isAdmin = true;
    }
}
