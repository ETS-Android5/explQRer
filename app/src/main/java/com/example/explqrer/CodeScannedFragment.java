package com.example.explqrer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.DialogFragment;
import com.google.mlkit.vision.barcode.common.Barcode;
import java.io.Serializable;

public class CodeScannedFragment extends DialogFragment {
    private CodeScannerFragmentListener listener;

    private TextView scoreText;
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
                            pictureTaken.setImageResource(android.R.drawable.checkbox_on_background);
                            takePictureButton.setOnClickListener(null);
                        }
                    }
                });

        takePictureButton.setOnClickListener(view1 -> {
            Intent intentCapture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            try{
                pictureActivityResultLauncher.launch(intentCapture);
            } catch (ActivityNotFoundException ignored){ }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        return builder.setView(view)
                .setPositiveButton("Add Code", (dialogInterface, i) -> {
                    code.setDescription(description.getText().toString());
                    if (code.getDescription().isEmpty()) {
                        code.setDescription("No Description");
                    }
                    if (locationToggle.isChecked()) {
                        // TODO: Record Location
                    }
                    listener.processQR(code);
                })
                .setTitle("Code worth: " + code.getScore() + " points!")
                .create();

    }
}
