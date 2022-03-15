package com.example.explqrer;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnTokenCanceledListener;
import com.google.mlkit.vision.barcode.common.Barcode;
import java.io.Serializable;

public class CodeScannedFragment extends DialogFragment {
    private CodeScannerFragmentListener listener;

    private EditText description;
    private SwitchCompat locationToggle;
    private Button takePictureButton;
    private ImageView pictureTaken;


    public interface CodeScannerFragmentListener {
        void processQR(GameCode code);
    }

    public static CodeScannedFragment newInstance(String code, String username) {
        Bundle args = new Bundle();
        args.putSerializable ("Code", code);
        args.putSerializable("Name", username);

        CodeScannedFragment fragment = new CodeScannedFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof CodeScannerFragmentListener) {
            listener = (CodeScannerFragmentListener) context;
        } else {
            throw new RuntimeException(context
            + " must implement CodeScannedFragmentListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity())
                .inflate(R.layout.code_scanned_fragment_layout, null);
        description = view.findViewById(R.id.code_scanned_description_text);
        locationToggle = view.findViewById(R.id.code_scanned_record_location);
        takePictureButton = view.findViewById(R.id.code_scanned_take_picture);
        pictureTaken = view.findViewById(R.id.code_scanned_image_taken);
        FusedLocationProviderClient fusedLocationClient;
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());

        String barcode = (String) getArguments().get("Code");
        String username = (String) getArguments().get("Name");
        GameCode code = new GameCode(barcode, username, null,null);

        ActivityResultLauncher<Intent> pictureActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData()!= null) {
                        Intent data = result.getData();
                        Bundle extras = data.getExtras();
                        Bitmap image = (Bitmap) extras.get("data");
                        if (image != null){
                            code.setPhoto(image);   // added: set photo taken
                            pictureTaken.setImageResource(android.R.drawable.checkbox_on_background);
                            takePictureButton.setOnClickListener(null);
                        }
                    }
                });
        takePictureButton.setOnClickListener(view1 -> {
            Intent intentCapture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            try {
                pictureActivityResultLauncher.launch(intentCapture);
            } catch (ActivityNotFoundException ignored){ }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        return builder.setView(view)
                .setPositiveButton("Add Code", (dialogInterface, i) -> {
                    if (description.getText().toString().isEmpty()) {
                        code.setDescription("No Description");
                    }else{
                        code.setDescription(description.getText().toString());
                    }


                    if (locationToggle.isChecked()) {
                        ((MainActivity) getActivity()).requestPermissionsIfNecessary(new String[] {
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                        });
                        if (ActivityCompat.checkSelfPermission(getContext(),
                                Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
                            ActivityCompat.requestPermissions(getActivity(),
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
                        }).addOnSuccessListener(getActivity(), location -> {
                            if (location != null) {
                                code.setLocation(location);
                            }
                        });
                    }

                    listener.processQR(code);
                })
                .setTitle("Code worth: " + code.getScore() + " points!")
                .create();

    }
}
