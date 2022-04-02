package com.example.explqrer;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.OnTokenCanceledListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity
        implements NavigationBarView.OnItemSelectedListener,
        CodeScannedFragment.CodeScannerFragmentListener,
        OnGetPlayerListener {

    // DATA
    private static final String SHARED_PREFS_PLAYER_KEY = "Player";
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    // Data
    private static PlayerProfile player;
    private ActivityResultLauncher<Intent> scannerLauncher;
    private DataHandler dataHandler;
    private FusedLocationProviderClient fusedLocationClient;
    // Views
    private TextView  usernameText, pointsRank, scannedRank, uniqueRank;
    private BottomNavigationView bottomNavigationView;

    // Shared Preferences
    private SharedPreferences sharedPreferences;

    /**
     * Get the player
     *
     * @return
     */
    public static PlayerProfile getPlayer() {
        return player;
    }

    /**
     * Update the player
     *
     * @param player the new player
     */
    public void setPlayer(PlayerProfile player) {
        MainActivity.player = player;
        saveData();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /* Site: stackoverflow.com
         * Link: https://stackoverflow.com/a/57175501
         * Author: https://stackoverflow.com/users/5255963/ali-rezaiyan
         */
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dataHandler = DataHandler.getInstance();
        sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        bottomNavigationView.setSelectedItemId(R.id.scan_nav);
        bottomNavigationView.setOnItemSelectedListener(this);
        usernameText = findViewById(R.id.user_name);
        pointsRank = findViewById(R.id.points_leaderboard_rank);
        scannedRank = findViewById(R.id.scanned_leaderboard_rank);
        uniqueRank = findViewById(R.id.unique_leaderboard_rank);
        loadData();
        requestPermissionsIfNecessary(new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.CAMERA
        });

        scannerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() != RESULT_OK && result.getResultCode() != 2 && result.getResultCode() != 3) {
                        return;
                    }
                    assert result.getData() != null;
                    if (result.getResultCode() == RESULT_OK) {
                        CodeScannedFragment codeScannedFragment = CodeScannedFragment
                                .newInstance(result.getData().getStringExtra("Code"));
                        codeScannedFragment.show(getSupportFragmentManager(), "CODE_SCANNED");
                    } else if (result.getResultCode() == 2) {
                        dataHandler.getPlayer(result.getData().getStringExtra("Username"), this);
                    } else {
                        String username = result.getData().getStringExtra("Username");
                        Intent intent = new Intent(this, PlayerDisplayActivity.class);
                        intent.putExtra("playerName", username);
                        startActivity(intent);
                    }

                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        saveData();
    }
    // end reference

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
            player = new PlayerProfile(String.format(Locale.US, "%06d",
                    (int) Math.floor(Math.random() * 1000000)), "");
            dataHandler.createPlayer(player);
            saveData();
        } else {
            dataHandler.getPlayer(player.getName(), this);
        }
    }

    private void saveData() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
        String json = gson.toJson(player);
        editor.putString(SHARED_PREFS_PLAYER_KEY, json);
        editor.apply();
        if (player != null) {
            updateStrings();
        } else {
            Toast.makeText(this, "Player Deleted", Toast.LENGTH_SHORT).show();
            loadData();
        }
    }

    /**
     * Called when a navigation item is selected
     *
     * @param item the selected item
     * @return True if the selection was processed successfully
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.map_nav:
                Intent intent = new Intent(MainActivity.this, MapActivity.class);
                startActivity(intent);
                return true;

            case R.id.profile_nav:
                // goes to UserProfile activity
                Intent profileIntent = new Intent(MainActivity.this, UserProfileActivity.class);
                startActivity(profileIntent);

                return true;

            case R.id.scan_nav:
                Intent scanningIntent = new Intent(this, ScanningPageActivity.class);
                scannerLauncher.launch(scanningIntent);
                return true;

            case R.id.search_nav:
                // goes to search activity
                Intent searchIntent = new Intent(this, SearchActivity.class);
                startActivity(searchIntent);
                return true;

            case R.id.leaderboard_nav:
                Intent leaderboardIntent = new Intent(this, ScannedRank.class);
                startActivity(leaderboardIntent);
                return true;

        }
        return false;
    }

    private void updateStrings() {
        System.out.print(player.getName());
        usernameText.setText(player.getName());
        dataHandler.getPtsL("465282", new OnGetPtsLListener() {
            @Override
            public void getPtsLListener(long ptsl) {
                String p = ptsl + "";
//                pointsRank.setText(p);
            }
        });

//        scannedRank.setText(player.getNumCodes());
//        uniqueRank.setText(player.getNumCodes());
    }

    @Override
    public void processQR(GameCode code, Boolean recordLocation) {

        if (recordLocation) {
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
            fusedLocationClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, new CancellationToken() {
                @NonNull
                @Override
                public CancellationToken onCanceledRequested(@NonNull OnTokenCanceledListener onTokenCanceledListener) {
                    return null;
                }

                @Override
                public boolean isCancellationRequested() {
                    return false;
                }
            }).addOnSuccessListener(this, location -> {
                if (location != null) {
                    code.setLocation(location);
                } else {
                    Toast.makeText(MainActivity.this, "Location not recorded",
                            Toast.LENGTH_SHORT).show();
                }
                addQR(code);
            }).addOnFailureListener(e -> {
                Toast.makeText(MainActivity.this, "Location not recorded",
                        Toast.LENGTH_SHORT).show();
                addQR(code);

            });

        } else {
            addQR(code);
        }
    }

    /**
     * Call all the methods needed to add a code to the database and update the UI
     *
     * @param code
     */
    private void addQR(GameCode code) {
        player.addCode(code);
        dataHandler.addQR(code, player);
        saveData();
    }

    /**
     * requestPermission result
     * If add permission not add successfully, show the text
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE && grantResults.length > 0) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, "Permission denied",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * request permission
     *
     * @param permissions
     */
    public void requestPermissionsIfNecessary(String[] permissions) {
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) !=
                    PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted
                permissionsToRequest.add(permission);
            }
        }
        // more than one permission is not granted
        if (permissionsToRequest.size() > 0) {
            ActivityCompat.requestPermissions(this, permissionsToRequest
                    .toArray(new String[0]), REQUEST_PERMISSIONS_REQUEST_CODE);
        }  // all are granted

    }


    @Override
    public void getPlayerListener(PlayerProfile player) {
        setPlayer(player);
    }
}