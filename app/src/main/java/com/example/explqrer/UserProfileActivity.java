package com.example.explqrer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class UserProfileActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    private static final String[] paths = {"Edit Profile", "Scan to sign-in", "Select to delete QR"};

    // DATA
    public static final String SHARED_PREFS_PLAYER_KEY = "Player";
    // Data
    private PlayerProfile player;

    private SharedPreferences sharedPreferences;

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

        sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        loadData();

        Spinner spinner = findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(UserProfileActivity.this, android.R.layout.simple_spinner_item, paths);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        this.populateBanner(); //calls populateBanner to put points and scans in in banner recycler view
        this.populateGallery(); //calls populateGallery to put images in the gallery recycler view

    }

    /* Site: stackoverflow.com
     * Link: https://stackoverflow.com/questions/29134094/recyclerview-horizontal-scroll-snap-in-center
     * Author: need to add author of answer
     * @return target position
     */
    LinearSnapHelper snapHelper = new LinearSnapHelper() {
        @Override
        public int findTargetSnapPosition(RecyclerView.LayoutManager layoutManager, int velocityX, int velocityY) {
            View centerView = findSnapView(layoutManager);
            if (centerView == null)
                return RecyclerView.NO_POSITION;

            int position = layoutManager.getPosition(centerView);
            int targetPosition = -1;
            if (layoutManager.canScrollHorizontally()) {
                if (velocityX < 0) {
                    targetPosition = position - 1;
                } else {
                    targetPosition = position + 1;
                }
            }

            if (layoutManager.canScrollVertically()) {
                if (velocityY < 0) {
                    targetPosition = position - 1;
                } else {
                    targetPosition = position + 1;
                }
            }

            final int firstItem = 0;
            final int lastItem = layoutManager.getItemCount() - 1;
            targetPosition = Math.min(lastItem, Math.max(targetPosition, firstItem));
            return targetPosition;
        }
    };


    @Override
    public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {

        switch (position) {
            case 0:
                // Edit player profile
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

    private void populateBanner(){

        ArrayList<String> banner = new ArrayList<>();
        DataHandler dataHandler = new DataHandler();
        //need to remove the hard coded user
        Long playerTotalPoints = dataHandler.getPtsL("explorer");
        Long playerTotalScanned = dataHandler.getQrL("explorer");
        String ptsTotalDisplay = "pts\n" + playerTotalPoints;
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
        //initilize the timer
        final Timer timer = new Timer();
        //have this block run on a continuous intravals
        timer.schedule(new TimerTask() {
            //first position in the recycler
            int position = 0;
            //manages when the position should be incremented or decremented to get to the propper position
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
     */
    private void populateGallery(){
        // TODO: getting the data from the Data Base and loop through populating the lists
        Integer[] imagePoints = {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29};
        Integer[] imageIds = {R.drawable.nature1, R.drawable.nature2, R.drawable.nature3,
                R.drawable.nature4,
                R.drawable.nature5,
                R.drawable.nature6,
                R.drawable.nature7,
                R.drawable.nature8,
                R.drawable.nature9,
                R.drawable.nature10,
                R.drawable.nature11,
                R.drawable.nature12,
                R.drawable.nature13,
                R.drawable.nature14,
                R.drawable.nature15,
                R.drawable.nature16,
                R.drawable.nature17,
                R.drawable.nature18,
                R.drawable.nature19,
                R.drawable.nature20,
                R.drawable.nature21,
                R.drawable.nature22,
                R.drawable.nature23,
                R.drawable.nature24,
                R.drawable.nature25,
                R.drawable.nature26,
                R.drawable.nature27,
                R.drawable.nature28,
                R.drawable.nature29
        };

        //set up the RecyclerView for the gallery
        RecyclerView galleryRecyclerView = findViewById(R.id.image_gallery);
        galleryRecyclerView.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(),4);
        galleryRecyclerView.setLayoutManager(gridLayoutManager);

        //loop through all the imagePoints and imageIds populate imageListItem objects with the image's point value and the image IDs
        ArrayList<ImageListItem> listOfImages = new ArrayList<>();
        for( int i = 0; i < imageIds.length;i++){
            ImageListItem imageListItem = new ImageListItem();
            imageListItem.setImageId(imageIds[i]);
            imageListItem.setImagePoints(imagePoints[i]);
            listOfImages.add(imageListItem);
        }

        GalleryAdapter imageListAdapter = new GalleryAdapter(getApplicationContext(),listOfImages);
        galleryRecyclerView.setAdapter(imageListAdapter);
    }

    /**
     * load the username from shared preferences
     */
    private void loadData() {
        Gson gson = new Gson();
        String json = sharedPreferences.getString(SHARED_PREFS_PLAYER_KEY, null);
        player = gson.fromJson(json, PlayerProfile.class);
    }
}
