package com.example.explqrer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class EditProfile extends AppCompatActivity {

    /* Site: handyopinion.com
     * Link: https://handyopinion.com/edit-profile-activity-in-android-studio-kotlin-java/
     * Author: https://handyopinion.com/author/shayan/
     */

    EditText userName, userContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        viewInitializations();
    }

    void viewInitializations() {
        userName = findViewById(R.id.username_text);
        userContact = findViewById(R.id.contact_text);

        // To show back button in actionbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    // Checking if the input in form is valid
    boolean validateInput() {
        if (userName.getText().toString().equals("")) {
            userName.setError("Please Enter Username");
            return false;
        }
        if (userContact.getText().toString().equals("")) {
            userContact.setError("Please Enter Contact");
            return false;
        }

        return true;
    }

    public void performEditProfile (View v) {
        if (validateInput()) {

            // Input is valid, here send data to your server

            String user_name = userName.getText().toString();
            String user_contact = userContact.getText().toString();

            DataHandler dh = new DataHandler();
            dh.createPlayer(user_name, user_contact);
        }
    }
}