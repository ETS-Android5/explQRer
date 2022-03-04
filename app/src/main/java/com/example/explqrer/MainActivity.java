package com.example.explqrer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener {

    // DATA
    public static final String SHARED_PREFS_USERNAME_KEY = "Username";
    // Data
    private String username;
    // Views
    private BottomNavigationView bottomNavigationView;
    // Shared Preferences
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        loadData();

        bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        bottomNavigationView.setOnItemSelectedListener(this);

    }

    private void loadData() {
        Gson gson = new Gson();
        String json = sharedPreferences.getString(SHARED_PREFS_USERNAME_KEY, null);
        username = gson.fromJson(json, String.class);

        if (username.isEmpty()) {
            // Random 6 digit number
            username = String.format(Locale.US,"%06d",
                    (int) Math.floor(Math.random() * 1000000));
        }
    }

    private void saveData() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(username);
        editor.putString(SHARED_PREFS_USERNAME_KEY, json);
        editor.apply();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.map_nav:
                // TODO: add map activity
                return true;

            case R.id.profile_nav:
                // TODO: add profile activity
                return true;

            case R.id.scan_nav:
                // TODO: add scan activity
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
}