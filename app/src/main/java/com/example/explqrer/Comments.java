package com.example.explqrer;

/**
 * Class to represent the comment data
 */
public class Comments {
    private String username;
    private String comment;

    /**
     * Constructor to initialize an empty object
     */
    public Comments(){
        username = "";
        comment = "";
    }

    /**
     * Constructor to initialize the object
     * @param username
     *  This is the username of the user
     * @param comment
     *  This is the comment that the user has left on the QrCode
     */
    public Comments(String username ,String comment){
        this.username = username;
        this.comment = comment;
    }

    /**
     * Function to get the username
     * @return The username of the user
     *
     */
    public String getUsername() {
        return username;
    }

    /**
     * Function to set the username
     * @param username
     *  This is the updated username of the user
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Function to get the comment left by the user
     * @return The comment left by the user
     */
    public String getComment() {
        return comment;
    }

    /**
     * Function to set the comment
     * @param comment
     *  This is the comment to set
     */
    public void setComment(String comment) {
        this.comment = comment;
    }
}
