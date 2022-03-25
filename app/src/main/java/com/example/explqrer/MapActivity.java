package com.example.explqrer;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.OnTokenCanceledListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.api.Endpoint;
import com.mapbox.android.core.location.LocationEngine;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

public class MapActivity extends AppCompatActivity implements OnGetNearByQrsListener {
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;

    private MapView mapView;
    private FloatingActionButton button;
    private EditText locationSearch;
    private ImageButton locationSearchButton;

    private LocationRequest locationRequest;
    private double longitude, latitude;

    // location update-------------
    private LocationListener locationListener;
    private ArrayList<Address> searchAddresses = new ArrayList<Address>();


    FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        Log.d("TAG", "onCreate:  1");

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mapView = findViewById(R.id.map);
        mapView.getMapboxMap().setCamera(new CameraOptions.Builder().zoom(14.0).build());
        mapView.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS);
        button  = (FloatingActionButton) findViewById(R.id.update_location);
        locationSearch = (EditText) findViewById(R.id.search_location);
        locationSearchButton = (ImageButton) findViewById(R.id.search_location_btn);
        Log.d("TAG", "onCreate:  2");



        LocationComponentPluginImpl locationComponentPlugin = mapView.getPlugin(Plugin.MAPBOX_LOCATION_COMPONENT_PLUGIN_ID);
        locationComponentPlugin.setEnabled(true);
        locationComponentPlugin.setLocationPuck(new LocationPuck2D(AppCompatResources.getDrawable(MapActivity.this, R.drawable.blue_circle)));
        locationComponentPlugin.addOnIndicatorPositionChangedListener(point ->
                mapView.getMapboxMap().setCamera(new CameraOptions.Builder().center(point).build())

        );

        Log.d("TAG", "onCreate:  3");


        locationRequest = LocationRequest.create();
        locationRequest.setInterval(4000);
        locationRequest.setFastestInterval(2000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


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
                //Log.d("TAG", "Location = " +location.toString());  location could not use toString()
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

        // get address
        locationSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String searchInputString = locationSearch.getText().toString();

                Locale locale = new Locale("","CANADA");
                Geocoder geocoder = new Geocoder(MapActivity.this,locale);
                try {
                    // An array of address that user search ( maximum = 10)
                    searchAddresses= (ArrayList<Address>) geocoder.getFromLocationName(searchInputString,10);

                } catch (IOException e) {
                    e.printStackTrace();
                }





            }
        });

        // get the center of the map


        // click and update the location ( zoom in that location and see the qr code near by)

    }

    private void refreshNearby(double latitude, double longitude) {
        Location location = new Location("");
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        new DataHandler().getNearByQrs(location, 1000, this);


    }

    @Override
    public void getNearbyQrs(ArrayList<Location> locations) {
        AnnotationPluginImpl annotationPlugin = mapView.getPlugin(Plugin.MAPBOX_ANNOTATION_PLUGIN_ID);
        PointAnnotationManager pointAnnotationManager = (PointAnnotationManager)
                annotationPlugin.createAnnotationManager(mapView, AnnotationType.PointAnnotation, new AnnotationConfig());

        // Pin icon by Icons8 https://icons8.com/icon/qYund0sKw42x/pin" https://icons8.com"
        Bitmap image = BitmapFactory.decodeResource(getResources(), R.drawable.red_marker);

        for (Location location: locations) {
            PointAnnotationOptions pointAnnotationOptions = new PointAnnotationOptions().withPoint(
                    Point.fromLngLat(location.getLongitude(), location.getLatitude())).withIconImage(image);
            pointAnnotationManager.create(pointAnnotationOptions);
        }
    }

    /**
     * requestPermission result
     * If add permission not add successfully, show the text
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode== REQUEST_PERMISSIONS_REQUEST_CODE &&grantResults.length>0){
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MapActivity.this,"Permission denied",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * request permission
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
                    .toArray(new String[0]),REQUEST_PERMISSIONS_REQUEST_CODE);
        }  // all are granted

    }





}