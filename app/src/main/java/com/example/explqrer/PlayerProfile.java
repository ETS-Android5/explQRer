package com.example.explqrer;

import androidx.core.graphics.drawable.IconCompat;

public class PlayerProfile {

    private String playerName;
    private String playerInfo;
    private long playerPoints;

    public PlayerProfile(String username, String Contact) {
        playerName = username;
        playerInfo = Contact;
        playerPoints = 0;
    }

    /**
     * Returns the String value of player name.
     *  @return playerName, a String value.
     */
    public String getName() {
        return playerName;
    }

    /**
     * This set method sets the name of the player.
     *  @param username a String as name.
     */
    public void setName(String username) {
        playerName = username;
    }

    /**
     * Returns the String value of player name.
     *  @return playerInfo, a String value.
     */
    public String getPlayerInfo() {
        return playerInfo;
    }
    /**
     * This set method sets the name of the player.
     *  @param contact a String as name.
     */
    public void setPlayerInfo(String contact) {
        playerInfo = contact;
    }

    /**
     * Returns the long value of player points.
     *  @return playerInfo, a long value.
     */
    public long getPlayerPoints() {
        return playerPoints;
    }
    /**
     * This set method sets the points.
     *  @param pts a long as score.
     */
    public void setPlayerPoints(long pts) {
        playerPoints = pts;
    }
    
}
