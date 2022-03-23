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

    // Checking if the input in form is valid
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

    public void performEditProfile(View v) {
        if (validateInput()) {

            // Input is valid, send data to the server

            String newUserName = userName.getText().toString();
            String newContactEmail = userContact.getText().toString();

            DataHandler dataHandler = new DataHandler();
            dataHandler.getPlayer(newUserName, new OnGetPlayerListener() {
                @Override
                public void getPlayerListener(PlayerProfile dataBasePlayer) {
                    if (dataBasePlayer == null) {
                        System.out.println("update");
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

    public void updatePlayerInfo(String newUserName, String newContactEmail, DataHandler dataHandler){
        System.out.println("in update");
        String oldUserName = player.getName();
        player.setName(newUserName);
        player.setContact(newContactEmail);
        dataHandler.updatePlayerUsername(oldUserName,player);
        saveData();
    }

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