package com.example.explqrer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import org.mapsforge.map.view.MapView;

public class MapActivity extends AppCompatActivity {
    private MapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        mapView = findViewById(R.id.map);

        
    }
}