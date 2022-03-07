package com.example.explqrer;

import androidx.annotation.NonNull;
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
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ScanningPageShow extends AppCompatActivity {
    private final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;

    private ListenableFuture cameraProviderFuture;
    private ExecutorService  cameraExecutor;
    private PreviewView previewView;
    private MyImageAnalyzer imageAnalyzer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanning_page_show);

        // request permission
        requestPermissionsIfNecessary(new String[]{
                // if you need to show the current location, uncomment the line below
                Manifest.permission.CAMERA


        });

        previewView = findViewById(R.id.previewView);
        this.getWindow().setFlags(1024,1024);

        cameraExecutor = Executors.newSingleThreadExecutor();
        cameraProviderFuture = ProcessCameraProvider.getInstance(ScanningPageShow.this);
        imageAnalyzer = new MyImageAnalyzer(getSupportFragmentManager());

        cameraProviderFuture.addListener(new Runnable() {
            @Override
            public void run() {
                try {
                    if ( ActivityCompat.checkSelfPermission( ScanningPageShow.this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
                        ActivityCompat.requestPermissions(ScanningPageShow.this, new String[]{Manifest.permission.CAMERA}, 1);
                    }else {

                        ProcessCameraProvider processCameraProvider = (ProcessCameraProvider) cameraProviderFuture.get();
                        bindPreview(processCameraProvider);
                    }
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        },ContextCompat.getMainExecutor(this));

    }

    private void bindPreview(ProcessCameraProvider processCameraProvider){
        // Preview: accepts a surface for displaying a preview, like PreviewView
        Preview preview = new Preview.Builder().build();
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
        private FragmentManager fragmentManager;

        public MyImageAnalyzer( FragmentManager fragmentManager){
            this.fragmentManager = fragmentManager;
        }


        @Override
        public void analyze(@NonNull ImageProxy image) {
            scanBarcode(image);
        }
    }

    private void scanBarcode(ImageProxy image){
        @SuppressLint("UnsafeOptInUsageError") Image image1 = image.getImage();
        assert image1!= null;

        // create an input image
        InputImage inputImage = InputImage.fromMediaImage(image1, image.getImageInfo().getRotationDegrees());
        BarcodeScannerOptions options =
                new BarcodeScannerOptions.Builder()
                        .setBarcodeFormats(
                                Barcode.FORMAT_QR_CODE,
                                Barcode.FORMAT_AZTEC
                        ).build();

        // BarcodeScanner for recognizing barcode
        BarcodeScanner scanner = BarcodeScanning.getClient(options);
        Task<List<Barcode>> task = scanner.process(inputImage)
                .addOnSuccessListener(new OnSuccessListener<List<Barcode>>() {
                    @Override
                    public void onSuccess(List<Barcode> barcodes) {
                        readerBarcodeData(barcodes);
                        // complete successfully
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // failed with an exception
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<List<Barcode>>() {
                    @Override
                    public void onComplete(@NonNull Task<List<Barcode>> task) {
                        image.close();
                    }
                });


    }

    private void readerBarcodeData( List<Barcode> barcodes) {
        for (Barcode barcode : barcodes) {
            Rect bounds = barcode.getBoundingBox();
            Point[] corners = barcode.getCornerPoints();

            String rawValue = barcode.getRawValue();

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

    // this is the one on github
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode== REQUEST_PERMISSIONS_REQUEST_CODE &&grantResults.length>0){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                return;
            }else{
                Toast.makeText(ScanningPageShow.this,"Permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void requestPermissionsIfNecessary(String[] permissions) {
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted
                permissionsToRequest.add(permission);
            }
        }
        // more than one permission is not granted
        if (permissionsToRequest.size() > 0) {
            ActivityCompat.requestPermissions(this, permissionsToRequest.toArray(new String[0]),REQUEST_PERMISSIONS_REQUEST_CODE);
        } else { // all are granted
            return;
        }
    }




}