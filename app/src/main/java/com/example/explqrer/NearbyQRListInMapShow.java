package com.example.explqrer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;


public class NearbyQRListInMapShow extends AppCompatActivity {
    //view
    ListView locationList;
    // model
    ArrayList<GameCode.CodeLocation> locations;
    ArrayAdapter<GameCode.CodeLocation> locationAdapter;
    private double playerLongitude;
    private double playerLatitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_qrlist_in_map_show);
        // get data from map activity
        locations = getIntent().getParcelableArrayListExtra("nearby_code_locations");
        playerLongitude = getIntent().getDoubleExtra("player_longitude",0.00);
        playerLatitude = getIntent().getDoubleExtra("player_latitude",0.00);

        locationList = (ListView) findViewById(R.id.location_list);

        // set adapter
        locationAdapter = new LocationAdapter(
                NearbyQRListInMapShow.this,
                locations,
                playerLongitude,playerLatitude);
        locationList.setAdapter(locationAdapter);

        // click list view
        locationList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // select one nearby location that player want to see
                GameCode.CodeLocation selectedQRLocation = locationAdapter.getItem(i);
                // back to the previous activity
                Intent intent = new Intent(NearbyQRListInMapShow.this, MapActivity.class);
                intent.putExtra("QR Location", selectedQRLocation.getLocation());
                startActivity(intent);
            }
        });

    }


}