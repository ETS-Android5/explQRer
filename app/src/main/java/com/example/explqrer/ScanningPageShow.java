package com.example.explqrer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.Image;
import android.os.Bundle;
import android.util.Size;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Citation 1: https://www.youtube.com/watch?v=iryHXuwuJ3Q
 * Author: Android Coding Time
 * Much of this section was taken from the tutorial provided in this video
 *
 * Citation 2: https://developers.google.com/ml-kit/vision/barcode-scanning/android
 */

public class ScanningPageShow extends AppCompatActivity
        implements CodeScannedFragment.CodeScannerFragmentListener {
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
    private DataHandler dataHandler;
    private boolean isScanning = false;
    private long pts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanning_page_show);

        // get the player from main activity
        playerProfile = (PlayerProfile) getIntent().getSerializableExtra("playerProfile");
        dataHandler = new DataHandler();
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
                }
                ProcessCameraProvider processCameraProvider =
                        (ProcessCameraProvider) cameraProviderFuture.get();
                bindPreview(processCameraProvider);

            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        },ContextCompat.getMainExecutor(ScanningPageShow.this));

        // click go Back ( If just scan 1, then only need this when there is no code scanning)
        goBack.setOnClickListener(view -> finish());
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

    @Override
    public void processQR(GameCode code) {
        // TODO: STUFF!!
        // Add to database???
        //dataHandler.addQR(code.getSha256hex(),code.);
        // Back to mainActivity
        dataHandler.uploadImage(code.getSha256hex(),playerProfile.getName(),code.getPhoto(),code.getScore());
        Intent intent = new Intent();
        intent.putExtra("Code", code);
        setResult(RESULT_OK, intent);
        finish();
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
        if (isScanning) { return; }
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

            // Display the score on the screen
            qrPoints.setVisibility(View.VISIBLE);
            scanCode.setVisibility(View.INVISIBLE);
            takePhoto.setVisibility(View.VISIBLE);


            /*if (dataHandler.hasScannedBefore(hash,username)) {
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
            if (dataHandler.hasScannedBefore(hash,username)) {
                alertBuilderGeolocation = new AlertDialog.Builder(ScanningPageShow.this);
                alertBuilderGeolocation.setMessage("Do you want to record your location?")
                        .setPositiveButton("Yes", (dialogInterface, i) -> {
                        })
                        .setNegativeButton("No", (dialogInterface, i) ->
                                Toast.makeText(ScanningPageShow.this,
                                        "Geolocation access denied",
                                        Toast.LENGTH_SHORT).show());
                alertDialogGeolocation = alertBuilderGeolocation.create();
                alertDialogGeolocation.show();

            }*/

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

            CodeScannedFragment codeScannedFragment = CodeScannedFragment
                    .newInstance(barcode.getRawValue(), playerProfile.getName());
            codeScannedFragment.show(getSupportFragmentManager(), "CODE_SCANNED");
            isScanning = true;
            previewView.setController(null);
            // TODO: turn scanning off eventually.
            break;

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