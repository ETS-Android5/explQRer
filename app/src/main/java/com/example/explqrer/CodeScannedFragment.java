package com.example.explqrer;

import android.annotation.SuppressLint;
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

public class CodeScannedFragment extends DialogFragment implements OnScannedByListener {
    private CodeScannerFragmentListener listener;

    private EditText description;
    private SwitchCompat locationToggle;
    private Button takePictureButton;
    private ImageView pictureTaken;
    private TextView rawText, scannedBy;


    public interface CodeScannerFragmentListener {
        void processQR(GameCode code, Boolean recordLocation);
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
                .inflate(R.layout.fragment_code_scanned, null);
        String barcode = (String) getArguments().get("Code");
        String username = (String) getArguments().get("Name");
        GameCode code = new GameCode(barcode, username, null,null);
        DataHandler.getInstance().scannedBy(code.getSha256hex(), this);
        description = view.findViewById(R.id.code_scanned_description_text);
        locationToggle = view.findViewById(R.id.code_scanned_record_location);
        takePictureButton = view.findViewById(R.id.code_scanned_take_picture);
        pictureTaken = view.findViewById(R.id.code_scanned_image_taken);
        rawText = view.findViewById(R.id.scanned_raw_value_text);
        scannedBy = view.findViewById(R.id.scanned_by_number);

        if (barcode.length() > 35) {
            rawText.setText("Contents: " + barcode.substring(0,34)+ "...");
        } else {
            rawText.setText("Contents: " + barcode);
        }

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
                    listener.processQR(code, locationToggle.isChecked());
                })
                .setTitle("Code worth: " + code.getScore() + " points!")
                .create();

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void scannedByListener(int scannedBy) {
        switch (scannedBy) {
            case -1:
            case 0:
                this.scannedBy.setText("Never scanned before!");
                break;
            case 1:
                this.scannedBy.setText("Scanned by: 1 other player");
                break;
            default:
                this.scannedBy.setText("Scanned by: " + scannedBy + " other players");
                break;
        }
    }
}
