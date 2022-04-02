package com.example.explqrer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * all the methods to create the User Profile
 */
public class UserProfileActivity extends AppCompatActivity
        implements NavigationBarView.OnItemSelectedListener,
        GameCodeFragment.GameCodeFragmentHost {

    private static final String[] paths = {"Select to delete QR", "Scan to sign-in", "Edit Profile"};
    private BottomNavigationView bottomNavigationView;
    private PlayerProfile player;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /* Site: stackoverflow.com
         * Link: https://stackoverflow.com/questions/28460300
         * Author: https://stackoverflow.com/users/3681880/suragch
         *  * Link: https://stackoverflow.com/a/56872365
         * Author: https://stackoverflow.com/users/6842344/parsa
         * Site: geeksforgeeks.org
         *  * Link: https://www.geeksforgeeks.org/popup-menu-in-android-with-example/
         * Author: https://auth.geeksforgeeks.org/user/ankur035/articles
         */
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        //button for comments
        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openComment();
            }
        });

        // get the player from main activity
        player = MainActivity.getPlayer();

        // Referencing and Initializing the button
        ImageButton button = (ImageButton) findViewById(R.id.settings);

        bottomNavigationView = findViewById(R.id.bottom_navigation_profile);
        bottomNavigationView.setOnItemSelectedListener((NavigationBarView.OnItemSelectedListener) this);

        // Setting onClick behavior to the button
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Initializing the popup menu and giving the reference as current context
                PopupMenu popupMenu = new PopupMenu(UserProfileActivity.this, button);

                // Inflating popup menu from popup_menu.xml file
                popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();
                        switch(id) {
                            case R.id.edit_profile:
                                // Edit profile
                                Intent intent = new Intent(getApplicationContext(), EditProfileActivity.class);
                                startActivity(intent);
                                break;
                            default:
                                break;
                        }
                        return false;
                    }
                });
                // Showing the popup menu
                popupMenu.show();
            }
        });

        this.populateBanner(player.getName()); //calls populateBanner to put points and scans in in banner recycler view
        GalleryBuilder.populateGallery(player,findViewById(R.id.image_gallery),getApplicationContext(),this); //calls populategallery of galleryBuilder to construct and populate the gallery
    }

    public void openComment(){
        Intent intent = new Intent(this, Comment.class);
        startActivity(intent);
    }


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

            case R.id.profile_qr_nav:
                // goes to UserProfile activity
                Intent profileIntent = new Intent(this, ProfileQr.class);
                startActivity(profileIntent);

                return true;

            case R.id.scan_nav:
                Intent scanningIntent= new Intent(this, ScanningPageActivity.class);
                startActivity(scanningIntent);
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

    /**
     * populates the Banner with the total points and the total scanned
     * @param playerName
     *    This is the username of the profile
     */
    private void populateBanner(String playerName){

        ArrayList<String> banner = new ArrayList<>();

        //get the Total points
        long playerTotalPoints =  player.getPoints();
        //get the Total scanned
        int playerTotalScanned = player.getNumCodes();
        //format the string to display total points in the banner
        String ptsTotalDisplay = "pts\n" + playerTotalPoints;
        //format the string to display total scanned in the banner
        String scannedTotalDisplay = "Scanned\n" + playerTotalScanned;

        banner.add(ptsTotalDisplay);
        banner.add(scannedTotalDisplay);

        //set up the RecyclerView
        final int time = 4000; // time delay for the sliding between items in recyclerview
        RecyclerView recyclerView = findViewById(R.id.points);
        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(UserProfileActivity.this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(horizontalLayoutManager);
        RecyclerViewAdapter myRecyclerViewAdapter = new RecyclerViewAdapter(this, banner);
        recyclerView.setAdapter(myRecyclerViewAdapter);


        //The LinearSnapHelper will snap the center of the target child view to the center of the attached RecyclerView
        final LinearSnapHelper linearSnapHelper = new LinearSnapHelper();
        linearSnapHelper.attachToRecyclerView(recyclerView);
        //initialize the timer
        final Timer timer = new Timer();
        //have this block run on a continuous intervals
        timer.schedule(new TimerTask() {
            //first position in the recycler
            int position = 0;
            //manages when the position should be incremented or decremented to get to the proper position
            @Override
            public void run() {
                //if true increment position by 1
                if (position < (myRecyclerViewAdapter.getItemCount() - 1 )) {
                    horizontalLayoutManager.smoothScrollToPosition(recyclerView, new RecyclerView.State(), position);
                    position++;
                }
                //else if false decrement position by 1
                else if (position == (myRecyclerViewAdapter.getItemCount() - 1 )) {
                    horizontalLayoutManager.smoothScrollToPosition(recyclerView, new RecyclerView.State(), 1);
                    position--;
                }
            }
        }, 0, time);
    }

    @Override
    public void createFragment(String hash) {
        GameCodeFragment gameCodeFragment = GameCodeFragment.newInstance(player.getCode(hash));
        gameCodeFragment.show(getSupportFragmentManager(),"GAME_CODE");
    }
}
