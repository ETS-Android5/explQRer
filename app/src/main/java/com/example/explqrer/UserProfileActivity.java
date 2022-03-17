package com.example.explqrer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * all the methods to create the User Profile
 */
public class UserProfileActivity extends AppCompatActivity{

    private static final String[] paths = {"Select to delete QR", "Scan to sign-in", "Edit Profile"};

    private PlayerProfile player;

    @Override
    protected void onCreate(Bundle savedInstanceState){
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

        // get the player from main activity
        player = MainActivity.getPlayer();

        // Referencing and Initializing the button
        ImageButton button = (ImageButton) findViewById(R.id.settings);

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
                            case R.id.scan_sign_in:
                                // Scan to sign in
                                Intent myIntent = new Intent(getApplicationContext(), ProfileQr.class);
                                startActivity(myIntent);
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
        this.populateGallery(player); //calls populateGallery to put images in the gallery recycler view and provides name of player as parameter

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

    /**
     * This populates the gallery with a GalleryAdapter
     *  Link: https://www.androidauthority.com/how-to-build-an-image-gallery-app-718976/
     *  Author: Adam Sinicki
     */
    private void populateGallery(PlayerProfile player){

        //set up the RecyclerView for the gallery
        RecyclerView galleryRecyclerView = findViewById(R.id.image_gallery);
        galleryRecyclerView.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(),4);
        galleryRecyclerView.setLayoutManager(gridLayoutManager);

        ArrayList<GalleryListItem> listOfImages = GalleryList.updateGallery(player);

        GalleryAdapter galleryListAdapter = new GalleryAdapter(getApplicationContext(),listOfImages);
        System.out.println("before adapter");
        galleryRecyclerView.setAdapter(galleryListAdapter);
    }
}
