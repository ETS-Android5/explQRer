package com.example.explqrer;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Toolbar;

import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {

    /* Site: handyopinion.com
     * Link: https://handyopinion.com/edit-profile-activity-in-android-studio-kotlin-java/
     * Author: https://handyopinion.com/author/shayan/
     */

    EditText userName, userContact;
    private PlayerProfile player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // get the player from main activity
        player = MainActivity.getPlayer();

        viewInitializations();
    }

    private void setSupportActionBar(Toolbar toolbar) {
    }

    void viewInitializations() {
        userName = findViewById(R.id.username_text);
        userContact = findViewById(R.id.contact_text);
    }

    // Checking if the input in form is valid
    boolean validateInput() {
        if (userName.getText().toString().equals("")) {
            userName.setError("Please Enter Username");
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

            DataHandler dh = new DataHandler();
            dh.getPlayer(newUserName, new OnGetPlayerListener() {
                @Override
                public void getPlayerListener(PlayerProfile dataBasePlayer) {
                    if (dataBasePlayer == null) {
                        try {
                            updateSharePreferences(newUserName,newContactEmail);
                        } catch (Exception e) {
                            System.out.println("Warning: This username is taken");
                            Toast.makeText(EditProfileActivity.this, newUserName+ " is taken, please try another username", Toast.LENGTH_SHORT).show();
                        }
                    }

                }
            });

            super.finish();
        }
    }

    public void updateSharePreferences(String newUserName, String newContactEmail){
        System.out.println(player.getName());
        player.setName(newUserName);
        player.setContact(newContactEmail);
        System.out.println(player.getName());
    }
}