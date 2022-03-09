package com.example.explqrer;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class PlayerProfile {

    private String name;
    private String contact;
    private long points;
    private ArrayList<String> qrHashes;

    public PlayerProfile(String username, String Contact) {
        name = username;
        contact = Contact;
        points = 0;
        qrHashes = new ArrayList<>();
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

// /**
//  * This set method sets the points.
//  *  @param pts a long as score.
//  * /
// public void setPlayerPoints(long pts) {
//     playerPoints = pts;
// }

    /**
     * Get the list of scanned code hashes
     * @return The scanned code hashes in an ArrayList
     */
    public ArrayList<String> getQrHashes() {
        return qrHashes;
    }

    public boolean addCode(@NonNull GameCode code) {
        qrHashes.add(code.getSha256hex());
        points += code.getScore();
        return true;
    }

    public boolean removeQrByHash(String hash) {
        if (qrHashes.remove(hash)) {
            // TODO: add some error checking
            points -= GameCode.calculateScore(hash);
            return true;
        }
        return false;
    }
}
