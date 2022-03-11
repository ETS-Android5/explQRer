package com.example.explqrer;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.HashSet;

public class PlayerProfile implements Serializable {

    private String name;
    private String contact;
    private long points;
    private GameCode highest, lowest;
    private HashSet<GameCode> codes;

    public PlayerProfile(String username, String Contact) {
        name = username;
        contact = Contact;
        points = 0;
        codes = new HashSet<>() ;
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
    public HashSet<GameCode> getCodes() {
        return codes;
    }

    /**
     * Add a GameCode to the PlayerProfile
     * @param code the code to add
     * @return true if the code was added, false otherwise
     */
    public boolean addCode(@NonNull GameCode code) {
        if (codes.add(code)) {
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
        if (codes.remove(code)) {
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
        for (GameCode code : codes) {
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
}
