package com.example.explqrer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.gson.Gson;
import com.google.mlkit.vision.barcode.common.Barcode;

import java.util.Locale;

public class MainActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener {

    // DATA
    public static final String SHARED_PREFS_PLAYER_KEY = "Player";
    // Data
    private PlayerProfile player;
    // Views
    private TextView  usernameText, highestText, lowestText;
    private BottomNavigationView bottomNavigationView;

    // Shared Preferences
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /* Site: stackoverflow.com
         * Link: https://stackoverflow.com/a/57175501
         * Author: https://stackoverflow.com/users/5255963/ali-rezaiyan
         */
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        loadData();

        bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        bottomNavigationView.setOnItemSelectedListener(this);
        usernameText = findViewById(R.id.username_text);
        usernameText.setText(player.getName());
        highestText = findViewById(R.id.highest_qr_display_main);
        lowestText = findViewById(R.id.lowest_qr_display_main);
        highestText.setText("Highest: " + (player.getHighestCode() != null ?
                player.getHighestCode().getSha256hex() : "None"));
        lowestText.setText("Lowest: " + (player.getLowestCode() != null ?
                player.getLowestCode().getSha256hex() : "None"));

        // get the data from the scanning page
        Barcode barcode = (Barcode) getIntent().getSerializableExtra("barcode");
        //Toast.makeText(this, String.valueOf(barcode.getRawValue()), Toast.LENGTH_SHORT).show();




    }

    /* Adapted from:
     * Source: youtube.com
     * Link: https://www.youtube.com/watch?v=jcliHGR3CHo&t=1s
     * Author: https://www.youtube.com/channel/UC_Fh8kvtkVPkeihBs42jGcA
     * */
    private void loadData() {
        Gson gson = new Gson();
        String json = sharedPreferences.getString(SHARED_PREFS_PLAYER_KEY, null);
        player = gson.fromJson(json, PlayerProfile.class);

        if (player == null) {
            // Random 6 digit number
            player = new PlayerProfile(String.format(Locale.US,"%06d",
                    (int) Math.floor(Math.random() * 1000000)), "");
            saveData();
        }
    }
    private void saveData() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(player);
        editor.putString(SHARED_PREFS_PLAYER_KEY, json);
        editor.apply();
    }
    // end reference

    /**
     * Called when a navigation item is selected
     * @param item the selected item
     * @return True if the selection was processed successfully
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.map_nav:
                // TODO: add map activity
                return true;

            case R.id.profile_nav:
                // goes to UserProfile activity
                Intent profileIntent = new Intent(MainActivity.this, UserProfileActivity.class);
                startActivity(profileIntent);
                return true;

            case R.id.scan_nav:
                // TODO: add scan activity
                Intent scanningIntent = new Intent(MainActivity.this, ScanningPageShow.class);
                startActivity(scanningIntent);
                return true;

            case R.id.search_nav:
                // TODO: add search activity
                return true;

            case R.id.leaderboard_nav:
                // TODO: add leaderboard activity
                return true;

        }
        return false;
    }

    /**
     * Get the username
     * @return username as String
     */
    public PlayerProfile getPlayer() {
        return player;
    }

    /**
     * Update the username
     * @param player the new username
     */
    public void setPlayer(PlayerProfile player) {
        this.player = player;
        saveData();
    }
}