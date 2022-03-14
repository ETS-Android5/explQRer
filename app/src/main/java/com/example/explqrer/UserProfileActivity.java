package com.example.explqrer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * all the methods to create the User Profile
 */
public class UserProfileActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    private static final String[] paths = {"Edit Profile", "Scan to sign-in", "Select to delete QR"};

    private PlayerProfile player;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        /* Site: stackoverflow.com
         * Link: https://stackoverflow.com/questions/28460300
         * Author: https://stackoverflow.com/users/3681880/suragch
         *  * Link: https://stackoverflow.com/a/56872365
         * Author: https://stackoverflow.com/users/6842344/parsa
         */
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        // get the player from main activity
        player= (PlayerProfile) getIntent().getSerializableExtra("playerProfile");

        Spinner spinner = findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(UserProfileActivity.this, android.R.layout.simple_spinner_item, paths);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        this.populateBanner(player.getName()); //calls populateBanner to put points and scans in in banner recycler view
        this.populateGallery(player.getName()); //calls populateGallery to put images in the gallery recycler view and provides name of player as parameter

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {

        switch (position) {
            case 0:
                // Edit player profile
                Intent intent = new Intent(this, EditProfile.class);
                startActivity(intent);
                break;
            case 1:
                // Scan to sign in
                break;
            case 2:
                // Select to delete QR
                break;

        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    /**
     * populates the Banner with the total points and the total scanned
     * @param playerName
     *    This is the username of the profile
     */
    private void populateBanner(String playerName){

        ArrayList<String> banner = new ArrayList<>();

        //get the Total points
        Long playerTotalPoints =  player.getPoints();
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
    private void populateGallery(String playerName){

        //set up the RecyclerView for the gallery
        RecyclerView galleryRecyclerView = findViewById(R.id.image_gallery);
        galleryRecyclerView.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(),4);
        galleryRecyclerView.setLayoutManager(gridLayoutManager);

        ArrayList<GalleryListItem> listOfImages = GalleryList.updateGallery(playerName);

        GalleryAdapter galleryListAdapter = new GalleryAdapter(getApplicationContext(),listOfImages);
        galleryRecyclerView.setAdapter(galleryListAdapter);
    }
}
