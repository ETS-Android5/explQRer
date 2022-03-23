package com.example.explqrer;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.OnTokenCanceledListener;
import com.mapbox.geojson.Point;
import com.mapbox.maps.CameraOptions;
import com.mapbox.maps.MapView;
import com.mapbox.maps.Style;
import com.mapbox.maps.plugin.LocationPuck2D;
import com.mapbox.maps.plugin.Plugin;
import com.mapbox.maps.plugin.annotation.AnnotationConfig;
import com.mapbox.maps.plugin.annotation.AnnotationPluginImpl;
import com.mapbox.maps.plugin.annotation.AnnotationType;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions;
import com.mapbox.maps.plugin.locationcomponent.LocationComponentPluginImpl;

import java.util.ArrayList;

public class MapActivity extends AppCompatActivity implements OnGetNearByQrsListener {

    private MapView mapView;

    private LocationRequest locationRequest;
    private double longitude, latitude;


    FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mapView = findViewById(R.id.map);
        mapView.getMapboxMap().setCamera(new CameraOptions.Builder().zoom(14.0).build());
        mapView.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS);

        LocationComponentPluginImpl locationComponentPlugin = mapView.getPlugin(Plugin.MAPBOX_LOCATION_COMPONENT_PLUGIN_ID);
        locationComponentPlugin.setEnabled(true);
        locationComponentPlugin.setLocationPuck(new LocationPuck2D(AppCompatResources.getDrawable(MapActivity.this, R.drawable.blue_circle)));
        locationComponentPlugin.addOnIndicatorPositionChangedListener(point ->
                mapView.getMapboxMap().setCamera(new CameraOptions.Builder().center(point).build()));


        locationRequest = LocationRequest.create();
        locationRequest.setInterval(4000);
        locationRequest.setFastestInterval(2000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
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
                Log.d("TAG", "Location = " +location.toString());
            } else {
                Log.d("TAG", "Null?");
            }
        }).addOnFailureListener(e -> {
            Log.d("TAG", "Failed!");
        });
        fusedLocationClient.requestLocationUpdates(locationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                for (Location location : locationResult.getLocations()) {
                    // loop and change the location
//                    updateLocation(new Point);
                    longitude = location.getLongitude();
                    latitude = location.getLatitude();
                    refreshNearby(latitude,longitude);
                }
            }
        }, getMainLooper());
    }

    private void refreshNearby(double latitude, double longitude) {
        Location location = new Location("");
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        new DataHandler().getNearByQrs(location, 1000, this);
    }

    private void updateLocation(double longitude, double latitude) {
    }

    @Override
    public void getNearbyQrs(ArrayList<Location> locations) {
        AnnotationPluginImpl annotationPlugin = mapView.getPlugin(Plugin.MAPBOX_ANNOTATION_PLUGIN_ID);
        PointAnnotationManager pointAnnotationManager = (PointAnnotationManager)
                annotationPlugin.createAnnotationManager(mapView, AnnotationType.PointAnnotation, new AnnotationConfig());
        Bitmap image = BitmapFactory.decodeResource(getResources(), R.drawable.red_marker);

        for (Location location: locations) {
            PointAnnotationOptions pointAnnotationOptions = new PointAnnotationOptions().withPoint(
                    Point.fromLngLat(location.getLongitude(), location.getLatitude())).withIconImage(image);
            pointAnnotationManager.create(pointAnnotationOptions);
        }
    }
}