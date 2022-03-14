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
    private TextView alreadyScanned;
    private ImageView goBack;
//    AlertDialog.Builder alertBuilderPhoto, alertBuilderGeolocation;
//    AlertDialog alertDialogPhoto, alertDialogGeolocation;

    // view for qr code
    private ListenableFuture cameraProviderFuture;
    private ExecutorService  cameraExecutor;
    private PreviewView previewView;
    private MyImageAnalyzer imageAnalyzer;

    // object get from previous intent
    private PlayerProfile playerProfile;

    private DataHandler dataHandler;
    private boolean isScanning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanning_page_show);

        // get the player from main activity
        playerProfile = (PlayerProfile) getIntent().getSerializableExtra("playerProfile");
        dataHandler = new DataHandler();
        // get ImageView and Textview for later use
        alreadyScanned = findViewById(R.id.already_scanned_text);
        goBack = findViewById(R.id.go_back);

        // request permission
        requestPermissionsIfNecessary(new String[]{
                Manifest.permission.CAMERA
        });

        previewView = findViewById(R.id.previewView);
        this.getWindow().setFlags(1024,1024);

        cameraExecutor = Executors.newSingleThreadExecutor();
        cameraProviderFuture = ProcessCameraProvider.getInstance(ScanningPageShow.this);
        imageAnalyzer = new MyImageAnalyzer(alreadyScanned);


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
        }, ContextCompat.getMainExecutor(ScanningPageShow.this));

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
        cameraExecutor.shutdown();
        dataHandler.uploadImage(code, playerProfile.getName());
        Intent intent = new Intent();
        intent.putExtra("Code", code);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void dismissed() {
        isScanning = false;
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
                        .build();

        // BarcodeScanner for recognizing barcode
        BarcodeScanner scanner = BarcodeScanning.getClient(options);
        // complete successfully
        Task<List<Barcode>> task = scanner.process(inputImage)
                .addOnSuccessListener(this::readerBarcodeData)
//                .addOnFailureListener(e -> {
//                    // failed with an exception
//                })
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
            String rawValue = barcode.getRawValue();
            if (rawValue == null || playerProfile.hasCode(rawValue)) {
                continue;
            }
            alreadyScanned.setVisibility(View.INVISIBLE);
            CodeScannedFragment codeScannedFragment = CodeScannedFragment
                    .newInstance(rawValue, playerProfile.getName());
            codeScannedFragment.show(getSupportFragmentManager(), "CODE_SCANNED");
            isScanning = true;
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
        }  // all are granted

    }
}