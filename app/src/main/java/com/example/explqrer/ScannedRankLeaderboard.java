package com.example.explqrer;

public class ScannedRankLeaderboard {
    String playerRank, playerName;

    public ScannedRankLeaderboard(String playerRank, String playerName) {
        this.playerRank = playerRank;
        this.playerName = playerName;
    }

    public String getPlayerRank() {
        return playerRank;
    }

    public String getPlayerName() {
        return playerName;
    }
}
