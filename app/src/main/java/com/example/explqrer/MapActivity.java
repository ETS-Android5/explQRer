package com.example.explqrer;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.OnTokenCanceledListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mapbox.android.gestures.MoveGestureDetector;
import com.mapbox.geojson.Point;
import com.mapbox.maps.CameraOptions;
import com.mapbox.maps.MapView;
import com.mapbox.maps.Style;
import com.mapbox.maps.ViewAnnotationAnchor;
import com.mapbox.maps.ViewAnnotationOptions;
import com.mapbox.maps.extension.style.layers.properties.generated.IconAnchor;
import com.mapbox.maps.plugin.LocationPuck2D;
import com.mapbox.maps.plugin.Plugin;
import com.mapbox.maps.plugin.annotation.AnnotationConfig;
import com.mapbox.maps.plugin.annotation.AnnotationPluginImpl;
import com.mapbox.maps.plugin.annotation.AnnotationType;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotation;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions;
import com.mapbox.maps.plugin.gestures.GesturesPluginImpl;
import com.mapbox.maps.plugin.gestures.OnMoveListener;
import com.mapbox.maps.plugin.locationcomponent.LocationComponentPluginImpl;
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener;
import com.mapbox.maps.viewannotation.ViewAnnotationManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

public class MapActivity extends AppCompatActivity implements OnGetNearByQrsListener {
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;

    private MapView mapView;
    private FloatingActionButton recenterButton;
    private EditText locationSearch;
    private ImageButton locationSearchButton;
    private Button refreshButton;

    private LocationRequest locationRequest;
    private double playerLongitude, playerLatitude;
    private Bitmap image;

    private GesturesPluginImpl gesturePlugin;
    private LocationComponentPluginImpl locationComponentPlugin;
    private ViewAnnotationManager annotationManager;
    private PointAnnotationManager pointAnnotationManager;
    private final OnIndicatorPositionChangedListener indicatorPositionChangedListener = new OnIndicatorPositionChangedListener() {
        @Override
        public void onIndicatorPositionChanged(@NonNull Point point) {
            mapView.getMapboxMap().setCamera(new CameraOptions.Builder().center(point).build());
        }
    };
    private final OnMoveListener onMoveListener = new OnMoveListener() {
        @Override
        public void onMoveBegin(@NonNull MoveGestureDetector moveGestureDetector) {
            stopFollow();
        }

        @Override
        public boolean onMove(@NonNull MoveGestureDetector moveGestureDetector) {
            return false;
        }

        @Override
        public void onMoveEnd(@NonNull MoveGestureDetector moveGestureDetector) {
        }
    };

    // location update-------------
    private LocationListener locationListener;
    private ArrayList<Address> searchAddresses = new ArrayList<>();

    FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        Log.d("TAG", "onCreate:  1");

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mapView = findViewById(R.id.map);
        mapView.getMapboxMap().setCamera(new CameraOptions.Builder().zoom(14.0).build());
        mapView.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS);


        gesturePlugin = mapView.getPlugin(Plugin.MAPBOX_GESTURES_PLUGIN_ID);

        recenterButton = findViewById(R.id.recenter_location);
        refreshButton = findViewById(R.id.map_refresh_button);
        locationSearch = findViewById(R.id.search_location);
        locationSearchButton = findViewById(R.id.search_location_btn);
        locationSearchButton.setBackgroundResource(0);
        locationSearch.setSelection(0);
        Log.d("TAG", "onCreate:  2");


        locationComponentPlugin = mapView.getPlugin(Plugin.MAPBOX_LOCATION_COMPONENT_PLUGIN_ID);
        locationComponentPlugin.setEnabled(true);
        locationComponentPlugin.setLocationPuck(new LocationPuck2D(AppCompatResources.getDrawable(MapActivity.this, R.drawable.blue_circle)));
        AnnotationPluginImpl annotationPlugin = mapView.getPlugin(Plugin.MAPBOX_ANNOTATION_PLUGIN_ID);

        annotationManager = mapView.getViewAnnotationManager();

        pointAnnotationManager = (PointAnnotationManager)
                annotationPlugin.createAnnotationManager(mapView, AnnotationType.PointAnnotation,
                        new AnnotationConfig());
        // Pin icon by Icons8 https://icons8.com/icon/qYund0sKw42x/pin" https://icons8.com"

        image = BitmapFactory.decodeResource(getResources(), R.drawable.red_marker);


        locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        Location codeLocation = null;

        try {
            codeLocation = (Location) getIntent().getExtras().get("QR Location");
        } catch (Exception ignored) {
        }
        if (codeLocation != null) {
            double longitude = codeLocation.getLongitude();
            double latitude = codeLocation.getLatitude();
            refreshNearby(longitude, latitude);
            Point point = Point.fromLngLat(longitude, latitude);
            mapView.getMapboxMap().setCamera(new CameraOptions.Builder().center(point).build());
        } else {
            locationComponentPlugin.addOnIndicatorPositionChangedListener(indicatorPositionChangedListener);
            gesturePlugin.addOnMoveListener(onMoveListener);
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
                    playerLongitude = location.getLongitude();
                    playerLatitude = location.getLatitude();
                    refreshNearby(playerLongitude, playerLatitude);
                }
            });
        }


        fusedLocationClient.requestLocationUpdates(locationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                for (Location location : locationResult.getLocations()) {
                    // update the location
                    playerLongitude = location.getLongitude();
                    playerLatitude = location.getLatitude();
                }
            }
        }, getMainLooper());

        // get address
        locationSearchButton.setOnClickListener(view -> {
            String searchInputString = locationSearch.getText().toString();
            Address address1 = null;

            Locale locale = new Locale("", "CANADA");
            Geocoder geocoder = new Geocoder(MapActivity.this, locale);
            try {
                // An array of address that user search ( maximum = 10)
                searchAddresses = (ArrayList<Address>) geocoder.getFromLocationName(searchInputString, 10);

                if (searchAddresses.size() == 0) {  // No search result find
                    locationSearch.setText("");
                    Toast.makeText(MapActivity.this, "No address found", Toast.LENGTH_SHORT).show();

                } else { // find search result
                    //Toast.makeText(MapActivity.this, "Total " + searchAddresses.size() + " results finded", Toast.LENGTH_SHORT).show();
                    address1 = searchAddresses.get(0);

                    // get the center of the map
                    Point point2 = Point.fromLngLat(address1.getLongitude(), address1.getLatitude());
                    locationComponentPlugin.addOnIndicatorPositionChangedListener(point ->
                            mapView.getMapboxMap().setCamera(new CameraOptions.Builder().center(point2).build()));

                    locationSearch.setText("");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        // go back to current location
        recenterButton.setOnClickListener((v) -> {
            Point point3 = Point.fromLngLat(playerLongitude, playerLatitude);
            mapView.getMapboxMap().setCamera(new CameraOptions.Builder().center(point3).build());
            refreshNearby(playerLongitude, playerLatitude);
            followPlayer();
//            locationComponentPlugin.addOnIndicatorPositionChangedListener(point ->
//                    mapView.getMapboxMap().setCamera(new CameraOptions.Builder().center(point3).build()));

        });

        refreshButton.setOnClickListener(view -> {
            Point point = mapView.getMapboxMap().getCameraState().getCenter();
            refreshNearby(point.longitude(), point.latitude());
        });

        // click and update the location ( zoom in that location and see the qr code near by)
    }

    private void moveCamera(double longitude, double latitude) {

    }

    private void followPlayer() {
        locationComponentPlugin.addOnIndicatorPositionChangedListener(indicatorPositionChangedListener);
        gesturePlugin.addOnMoveListener(onMoveListener);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                for (Location location : locationResult.getLocations()) {
                    // update the location
                    playerLongitude = location.getLongitude();
                    playerLatitude = location.getLatitude();
                    refreshNearby(playerLongitude, playerLatitude);
                }
            }
        }, getMainLooper());
    }

    private void stopFollow() {
        locationComponentPlugin.removeOnIndicatorPositionChangedListener(indicatorPositionChangedListener);
        gesturePlugin.removeOnMoveListener(onMoveListener);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                for (Location location : locationResult.getLocations()) {
                    // update the location
                    playerLongitude = location.getLongitude();
                    playerLatitude = location.getLatitude();
                }
            }
        }, getMainLooper());
    }

    private void refreshNearby(double longitude, double latitude) {
        Location location = new Location("");
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        DataHandler.getInstance().getNearByQrs(location, 1500, this);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void getNearbyQrs(ArrayList<GameCode.CodeLocation> locations) {
        pointAnnotationManager.deleteAll();


        for (GameCode.CodeLocation location : locations) {
            Point point = Point.fromLngLat(location.location.getLongitude(), location.location.getLatitude());

            PointAnnotationOptions pointAnnotationOptions = new PointAnnotationOptions()
                    .withPoint(point)
                    .withIconImage(image)
                    .withIconAnchor(IconAnchor.TOP);
            PointAnnotation pointAnnotation = pointAnnotationManager.create(pointAnnotationOptions);

            ViewAnnotationOptions viewAnnotationOptions = new ViewAnnotationOptions.Builder()
                    .associatedFeatureId(pointAnnotation.getFeatureIdentifier())
                    .geometry(pointAnnotation.getPoint())
                    .anchor(ViewAnnotationAnchor.TOP)
                    .build();

            TextView pts = (TextView) annotationManager.addViewAnnotation(R.layout.map_qr, viewAnnotationOptions);
            pts.setText(GameCode.calculateScore(location.hash) + " pts");
            pts.setOnClickListener(view -> {
                Log.d("TAG", "WORKING!");

//                GameCodeFragment gameCodeFragment = GameCodeFragment.newInstance(location);
//                gameCodeFragment.show(getSupportFragmentManager(), "GAME_CODE");
                //TODO: popup for the clicked QR
            });
        }
    }


//    /**
//     * requestPermission result
//     * If add permission not add successfully, show the text
//     * @param requestCode
//     * @param permissions
//     * @param grantResults
//     */
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
//                                           @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//
//        if (requestCode== REQUEST_PERMISSIONS_REQUEST_CODE &&grantResults.length>0){
//            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
//                Toast.makeText(MapActivity.this,"Permission denied",
//                        Toast.LENGTH_LONG).show();
//            }
//        }
//    }
//
//    /**
//     * request permission
//     * @param permissions
//     */
//    public void requestPermissionsIfNecessary(String[] permissions) {
//        ArrayList<String> permissionsToRequest = new ArrayList<>();
//        for (String permission : permissions) {
//            if (ContextCompat.checkSelfPermission(this, permission) !=
//                    PackageManager.PERMISSION_GRANTED) {
//                // Permission is not granted
//                permissionsToRequest.add(permission);
//            }
//        }
//        // more than one permission is not granted
//        if (permissionsToRequest.size() > 0) {
//            ActivityCompat.requestPermissions(this, permissionsToRequest
//                    .toArray(new String[0]),REQUEST_PERMISSIONS_REQUEST_CODE);
//        }  // all are granted
//
//    }


}