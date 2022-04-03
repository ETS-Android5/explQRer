package com.example.explqrer;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
import java.util.List;
import java.util.Locale;

public class MapActivity extends AppCompatActivity implements OnGetNearByQrsListener {

    private MapView mapView;
    private final OnIndicatorPositionChangedListener indicatorPositionChangedListener = new OnIndicatorPositionChangedListener() {
        @Override
        public void onIndicatorPositionChanged(@NonNull Point point) {
            mapView.getMapboxMap().setCamera(new CameraOptions.Builder().center(point).build());
        }
    };
    private FloatingActionButton recenterButton;
    private AutoCompleteTextView locationSearch;
    private ImageButton locationSearchButton;
    private Button refreshButton, nearbyListButton;
    private LocationRequest locationRequest;
    private double playerLongitude, playerLatitude;
    private double searchLongitude, searchLatitude;
    private Bitmap image;
    private ActivityResultLauncher<Intent> nearbyLauncher;
    private GesturesPluginImpl gesturePlugin;
    private LocationComponentPluginImpl locationComponentPlugin;
    private ViewAnnotationManager annotationManager;
    private PointAnnotationManager pointAnnotationManager;
    // location update-------------
    private LocationListener locationListener;
    private final ArrayList<Address> searchAddresses = new ArrayList<>();
    private FusedLocationProviderClient fusedLocationClient;
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
    private ArrayList<GameCode.CodeLocation> codes;

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
        nearbyListButton = findViewById(R.id.nearby_list_button);


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
            Log.d("TAG", "onCreate: return location" + codeLocation.getLongitude() + codeLocation.getLatitude());
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


        List<String> suggestionResults = new ArrayList<String>();
        final String[][] array = {new String[6]};
        Locale locale = new Locale("", "CANADA");
        Geocoder geocoder = new Geocoder(MapActivity.this, locale);

        // get address
        locationSearchButton.setOnClickListener(view -> {
            String searchInputString = locationSearch.getText().toString();
            Address address1 = null;
            try {
                // An array of address that user search ( maximum = 10)
                ArrayList<Address> searchAddresses = (ArrayList<Address>) geocoder.getFromLocationName(searchInputString, 10);

                if (searchAddresses.size() == 0) {  // No search result find
                    locationSearch.setText("");
                    Toast.makeText(MapActivity.this, "No address found", Toast.LENGTH_SHORT).show();

                } else { // find search result
                    address1 = searchAddresses.get(0);
                    searchLongitude = address1.getLongitude(); // get search location
                    searchLatitude = address1.getLatitude();

                    // get the center of the map
                    stopFollow();
                    Point point2 = Point.fromLngLat(address1.getLongitude(), address1.getLatitude());
                    mapView.getMapboxMap().setCamera(new CameraOptions.Builder().center(point2).build());

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
        });

        refreshButton.setOnClickListener(view -> {
            Point point = mapView.getMapboxMap().getCameraState().getCenter();
            refreshNearby(point.longitude(), point.latitude());
        });

        // click and see nearby qr ( location and distance)
        nearbyListButton.setOnClickListener(view -> {
            refreshNearby(searchLongitude, searchLatitude); // change
            Intent intent = new Intent(MapActivity.this, NearbyQRListInMapShow.class);
            intent.putParcelableArrayListExtra("nearby_code_locations", codes);
            intent.putExtra("player_longitude", playerLongitude);
            intent.putExtra("player_latitude", playerLatitude);
            nearbyLauncher.launch(intent);


        });
        nearbyLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK) {
                assert (result.getData() != null);
                Location location = result.getData().getParcelableExtra("location");
                double lng = location.getLongitude();
                double lat = location.getLatitude();
                stopFollow();
                mapView.getMapboxMap().setCamera(new CameraOptions.Builder().center(Point.fromLngLat(lng, lat)).build());
                refreshNearby(lng, lat);
            }
        });
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
        codes = locations;
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
                    .allowOverlap(true)
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


}