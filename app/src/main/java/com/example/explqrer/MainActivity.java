package com.example.explqrer;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.gson.Gson;

import java.util.Locale;

public class MainActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener {

    // DATA
    private static final String SHARED_PREFS_PLAYER_KEY = "Player";
    // Data
    private PlayerProfile player;
    private ActivityResultLauncher<Intent> scannerLauncher;
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

        DataHandler dataHandler = new DataHandler();
        dataHandler.createPlayer(player.getName(), player.getName() + "@gmail.com");

        bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        bottomNavigationView.setSelectedItemId(R.id.scan_nav);
        bottomNavigationView.setOnItemSelectedListener(this);
        usernameText = findViewById(R.id.username_text);
        highestText = findViewById(R.id.highest_qr_display_main);
        lowestText = findViewById(R.id.lowest_qr_display_main);
        updateStrings();

        scannerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() != RESULT_OK) { return; }
                    assert result.getData() != null;
                    player.addCode((GameCode) result.getData().getSerializableExtra("Code"));
                    saveData();
                    updateStrings();
                });

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
                Toast.makeText(this, "Map not yet available.", Toast.LENGTH_SHORT).show();
                // TODO: add map activity
                return true;

            case R.id.profile_nav:
                // goes to UserProfile activity
                Intent profileIntent = new Intent(MainActivity.this, UserProfileActivity.class);
                profileIntent.putExtra("playerProfile", player);
                startActivity(profileIntent);
            
                return true;

            case R.id.scan_nav:
                Intent scanningIntent = new Intent(this, ScanningPageShow.class);
                scanningIntent.putExtra("playerProfile", player);
                scannerLauncher.launch(scanningIntent);
                return true;

            case R.id.search_nav:
                Toast.makeText(this, "Search not yet available.", Toast.LENGTH_SHORT).show();
                // TODO: add search activity
                return true;

            case R.id.leaderboard_nav:
                Toast.makeText(this, "Leaderboard not yet available.", Toast.LENGTH_SHORT).show();
                // TODO: add leaderboard activity
                return true;

        }
        return false;
    }

    private void updateStrings() {

        usernameText.setText(player.getName());

        highestText.setText("Highest: " + (player.getHighestCode() != null ?
                player.getHighestCode().getDescription() : "None"));
        lowestText.setText("Lowest: " + (player.getLowestCode() != null ?
                player.getLowestCode().getDescription() : "None"));
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