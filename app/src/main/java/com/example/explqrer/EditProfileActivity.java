package com.example.explqrer;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Locale;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {

    /* Site: handyopinion.com
     * Link: https://handyopinion.com/edit-profile-activity-in-android-studio-kotlin-java/
     * Author: https://handyopinion.com/author/shayan/
     */

    EditText userName, userContact;
    private PlayerProfile player;

    // Shared Preferences
    private SharedPreferences sharedPreferences;
    // DATA
    private static final String SHARED_PREFS_PLAYER_KEY = "Player";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // get the player from main activity
        player = MainActivity.getPlayer();
        sharedPreferences = getPreferences(Context.MODE_PRIVATE);

        viewInitializations();
    }

    void viewInitializations() {
        userName = findViewById(R.id.username_text);
        userContact = findViewById(R.id.contact_text);
    }

    /**
     * Checking if the input in form is valid
     * @return true
     *     When username or email entered is not empty
     * @return false
     *     When username or email entered is not an empty space
     */
    boolean validateInput() {
        if (userName.getText().toString().equals("")) {
            userName.setError("Please Enter Username With Only Letters and Numbers Cannot be Empty");
            return false;
        }
        if (userContact.getText().toString().equals("")) {
            userContact.setError("Please Enter Email");
            return false;
        }

        return true;
    }

    /**
     * preform actions to update information in the app
     * @param v
     *   Takens in a view
     */
    public void performEditProfile(View v) {
        if (validateInput()) {

            // Input is valid, send data to the server

            String newUserName = userName.getText().toString();
            String newContactEmail = userContact.getText().toString();

            DataHandler dataHandler = new DataHandler();
            //get player with the newUserName inputed from the data base
            dataHandler.getPlayer(newUserName, new OnGetPlayerListener() {

                @Override
                public void getPlayerListener(PlayerProfile dataBasePlayer) {
                    if (dataBasePlayer == null) {
                        System.out.println("update");
                        //updates information in the data base and locally in shared preferences
                        updatePlayerInfo(newUserName,newContactEmail,dataHandler);

                    }else{
                        System.out.println("toast");
                        Toast.makeText(EditProfileActivity.this, newUserName+ " is taken, please try another username", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            super.finish();
        }
    }

    /**
     * updates the username and email of object player profile
     * @param newUserName
     *    takes in a new username
     * @param newContactEmail
     *    takes in new contact email
     * @param dataHandler
     *    takes in a data handler
     */
    public void updatePlayerInfo(String newUserName, String newContactEmail, DataHandler dataHandler){
        String oldUserName = player.getName();
        player.setName(newUserName);
        player.setContact(newContactEmail);
        //call to update the player info in the dataBase
        dataHandler.updatePlayerUsername(oldUserName,player);
        //call to update the player info in the shared preferences
        saveData();
    }

    /**
     * saves data to shared preferences
     */
    private void saveData() {
        System.out.println("in save data in edit profile");
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
        String json = gson.toJson(player);
        editor.putString(SHARED_PREFS_PLAYER_KEY, json);
        System.out.println("before apply");
        editor.apply();

    }

}