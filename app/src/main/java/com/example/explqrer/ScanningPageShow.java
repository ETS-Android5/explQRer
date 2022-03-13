package com.example.explqrer;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.location.Location;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Size;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;
//import com.j256.ormlite.stmt.query.In;

import org.w3c.dom.Text;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Citation 1: https://www.youtube.com/watch?v=iryHXuwuJ3Q
 * Author: Android Coding Time
 * Much of this section was taken from the tutorial provided in this video
 *
 * Citation 2: https://developers.google.com/ml-kit/vision/barcode-scanning/android
 */

public class ScanningPageShow extends AppCompatActivity {
    private final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    private final int REQUEST_IMAGE_CAPTURE = 1;

    // view in activity
    private TextView qrPoints;
    private ImageView goBack,scanCode,takePhoto,takePhotoDenied,getGeolocation, geolocationDenied, showPhoto;
    AlertDialog.Builder alertBuilderPhoto, alertBuilderGeolocation;
    AlertDialog alertDialogPhoto, alertDialogGeolocation;

    // view for qr code
    private ListenableFuture cameraProviderFuture;
    private ExecutorService  cameraExecutor;
    private PreviewView previewView;
    private MyImageAnalyzer imageAnalyzer;

    // object get from previous intent
    private PlayerProfile playerProfile;

    private Bitmap imageBitmap;

//    private Barcode barcodeReturn; // Now, just scan one, return 1
//    private boolean isScanning = false;
//    private HashMap<Barcode, Location> codeLocation; // if player needs to scan multiple in one activity
//    private HashMap<Barcode, Image> codeImage;// if player could scan multiple ( do later)

    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                    if (result.getResultCode() == Activity.RESULT_OK && result.getData()!= null) {
                        Intent data = result.getData();
                        Bundle extras = data.getExtras();
                        imageBitmap = (Bitmap) extras.get("data");
                        if ( imageBitmap!= null){
                            showPhoto.findViewById(R.id.show_photo);
                            showPhoto.setImageBitmap(imageBitmap);
                        }

                       // TODO: Convert bitmap to image and store in gameCode constructor


                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanning_page_show);

        // get the player from main activity
        playerProfile = (PlayerProfile) getIntent().getSerializableExtra("playerProfile");

        // get ImageView and Textview for later use
        qrPoints = findViewById(R.id.qr_points);
        goBack = findViewById(R.id.go_back);
        scanCode = findViewById(R.id.scanning_qr);
        takePhoto = findViewById(R.id.take_photo);
        takePhotoDenied = findViewById(R.id.take_photo_denied);
        getGeolocation = findViewById(R.id.get_geolocation);
        geolocationDenied = findViewById(R.id.geolocation_denied);
        showPhoto = findViewById(R.id.show_photo);

        scanCode.setVisibility(View.VISIBLE);
        takePhoto.setVisibility(View.INVISIBLE);


        // request permission
        requestPermissionsIfNecessary(new String[]{
                // if you need to show the current location, uncomment the line below
                Manifest.permission.CAMERA
        });

        previewView = findViewById(R.id.previewView);
        this.getWindow().setFlags(1024,1024);

        cameraExecutor = Executors.newSingleThreadExecutor();
        cameraProviderFuture = ProcessCameraProvider.getInstance(ScanningPageShow.this);
        imageAnalyzer = new MyImageAnalyzer(qrPoints);


        cameraProviderFuture.addListener(() -> {
            try {
                if ( ActivityCompat.checkSelfPermission( ScanningPageShow.this,
                        Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(ScanningPageShow.this,
                            new String[]{Manifest.permission.CAMERA}, 1);
                }else {
                    ProcessCameraProvider processCameraProvider =
                            (ProcessCameraProvider) cameraProviderFuture.get();
                    bindPreview(processCameraProvider);
                }
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        },ContextCompat.getMainExecutor(ScanningPageShow.this));

        // click go Back ( If just scan 1, then only need this when there is no code scanning)
        goBack.setOnClickListener(view -> {
            /*TODO: send Image: photo
              TODO: send Location: location
              TODO: send barcode
             */
            Intent intent = new Intent(ScanningPageShow.this, MainActivity.class);
            startActivity(intent);
        });

        // TODO: access geolocation
    }

    /**
     * Display a preview
     * Using imageCapture to capture a photo
     * @param processCameraProvider
     */
    private void bindPreview(ProcessCameraProvider processCameraProvider){
        // Preview: accepts a surface for displaying a preview, like PreviewView
        Preview preview = new Preview.Builder().build();
        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        CameraSelector cameraSelector = new CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build();
        //Image capture: captures and saves a photo
        ImageCapture imageCapture = new ImageCapture.Builder().build();

        // Build imageAnalysis
        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                .setTargetResolution(new Size(1200, 700))
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build();
        imageAnalysis.setAnalyzer(cameraExecutor,imageAnalyzer);

        processCameraProvider.unbindAll();
        processCameraProvider.bindToLifecycle(this, cameraSelector, preview,imageCapture, imageAnalysis);
    }


    public class MyImageAnalyzer implements ImageAnalysis.Analyzer{
        TextView textView;
        public MyImageAnalyzer(TextView textView){
            this.textView = textView;
        }

        @Override
        public void analyze(@NonNull ImageProxy image) {
            scanBarcode(image);
        }
    }

    /**
     * Use Image, InputImage, BarcodeScanner to scan and recognize the code
     * Set the scanner options
     * @param image
     */
    private void scanBarcode(ImageProxy image){
        @SuppressLint("UnsafeOptInUsageError") Image image1 = image.getImage();
        assert image1!= null;

        // create an input image
        InputImage inputImage = InputImage.fromMediaImage(image1,
                image.getImageInfo().getRotationDegrees());

        BarcodeScannerOptions options =
                new BarcodeScannerOptions.Builder()
                        .setBarcodeFormats(
                                Barcode.FORMAT_QR_CODE,
                                Barcode.FORMAT_AZTEC
                        ).build();

        // BarcodeScanner for recognizing barcode
        BarcodeScanner scanner = BarcodeScanning.getClient(options);
        Task<List<Barcode>> task = scanner.process(inputImage)
                .addOnSuccessListener(barcodes -> {
                    geolocationDenied.setVisibility(View.INVISIBLE);
                    qrPoints.setVisibility(View.INVISIBLE);
                    scanCode.setVisibility(View.VISIBLE);
                    takePhoto.setVisibility(View.INVISIBLE);
                    takePhotoDenied.setVisibility(View.INVISIBLE);

                    readerBarcodeData(barcodes);
                    // complete successfully
                })
                .addOnFailureListener(e -> {
                    // failed with an exception
                })
                .addOnCompleteListener(task1 -> image.close());
    }

    /**
     * Show the point of the code the player scanned
     * Take photo if player allowed
     * Get location if player allowed
     *
     * Source: https://www.youtube.com/watch?v=YLUmfyGFjnU
     * @param barcodes
     */
    private void readerBarcodeData( List<Barcode> barcodes) {
        for (Barcode barcode : barcodes) {
            Rect bounds = barcode.getBoundingBox();
            Point[] corners = barcode.getCornerPoints();

            String rawValue = barcode.getRawValue();

            // Create a game code and get score of the scanning code
            GameCode gameCode = new GameCode(barcode,playerProfile,null,null );
            int score = gameCode.getScore();
            String hash = gameCode.getSha256hex();
            String username = playerProfile.getName();
            // TODO: add the qr into the database

            // Display the score on the screen
            qrPoints.setText("Points: " + score);
            qrPoints.setVisibility(View.VISIBLE);
            scanCode.setVisibility(View.INVISIBLE);
            takePhoto.setVisibility(View.VISIBLE);

            // TODO: change condition from (alertBuilder == null) to
            // TODO:      ( if barcode is not in database)
            if (alertBuilderPhoto == null) {
                alertBuilderPhoto = new AlertDialog.Builder(ScanningPageShow.this);
                alertBuilderPhoto.setMessage("Do you want to record a photo?")
                        .setPositiveButton("Yes", (dialogInterface, i) -> {
                            // set ONLY take photo visible
                            //goBack.setVisibility(View.INVISIBLE);
                            //getGeolocation.setVisibility(View.INVISIBLE);
                            Intent intentCapture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            try{
                                someActivityResultLauncher.launch(intentCapture);

                            }catch (ActivityNotFoundException e){
                                Toast.makeText(ScanningPageShow.this, "Your device cannot take photos", Toast.LENGTH_SHORT).show();
                            }

                            goBack.setVisibility(View.VISIBLE);
                            getGeolocation.setVisibility(View.VISIBLE);

                            // click on take photo imageView
                            /*takePhoto.setOnClickListener(view -> {
                                // set the goBack and getGeolocation back to visible for new scanning code
                                goBack.setVisibility(View.VISIBLE);
                                getGeolocation.setVisibility(View.VISIBLE);

                                // TODO: taking photo and store the photo in the database (which database)
                                Intent intentCapture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                startActivity(intentCapture);
                                if (intentCapture.resolveActivity(getPackageManager())!= null){
                                    someActivityResultLauncher.launch(intentCapture);
                                }

                                *//*Intent intent = new Intent(ScanningPageShow.this, MainActivity.class);
                                startActivity(intent);
                                Toast.makeText(ScanningPageShow.this,
                                        "Code has been added! You win: "+ score + " points",
                                        Toast.LENGTH_LONG).show();*//*


                            });*/

                        })
                        .setNegativeButton("No", (dialogInterface, i) -> {

                            Toast.makeText(ScanningPageShow.this,
                                    "Taking photo is denied", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(ScanningPageShow.this, MainActivity.class);
                            startActivity(intent);
                            Toast.makeText(ScanningPageShow.this,
                                    "Code has been added! You win: "+
                                            score + " points",
                                    Toast.LENGTH_LONG).show();

                        });
                alertDialogPhoto = alertBuilderPhoto.create();
                alertDialogPhoto.show();
            }

            // Show the getting geolocation dialog
            if (alertBuilderGeolocation == null) {
                alertBuilderGeolocation = new AlertDialog.Builder(ScanningPageShow.this);
                alertBuilderGeolocation.setMessage("Do you want to record your location?")
                        .setPositiveButton("Yes", (dialogInterface, i) -> {
                            // TODO: ask for access geolocation

                        })
                        .setNegativeButton("No", (dialogInterface, i) ->
                                Toast.makeText(ScanningPageShow.this,
                                        "Geolocation access denied",
                                        Toast.LENGTH_SHORT).show());
                alertDialogGeolocation = alertBuilderGeolocation.create();
                alertDialogGeolocation.show();

            }

            // get the value type of the barcode
            int valueType = barcode.getValueType();

            // See API reference for complete list of supported types
            switch (valueType) {
                case Barcode.TYPE_WIFI:
                    String ssid = barcode.getWifi().getSsid();
                    String password = barcode.getWifi().getPassword();
                    int type = barcode.getWifi().getEncryptionType();
                    break;
                case Barcode.TYPE_URL:
                    String title = barcode.getUrl().getTitle();
                    String url = barcode.getUrl().getUrl();
                    break;
            }
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
              Toast.makeText(ScanningPageShow.this,"Permission denied",
                      Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * request permission
     * @param permissions
     */
    private void requestPermissionsIfNecessary(String[] permissions) {
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
        } else { // all are granted
            return;
        }
    }
}