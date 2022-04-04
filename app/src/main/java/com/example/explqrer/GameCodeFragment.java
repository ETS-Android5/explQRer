package com.example.explqrer;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnTokenCanceledListener;
import com.google.android.gms.tasks.Task;
import java.util.HashMap;

public class GameCodeFragment extends DialogFragment implements OnGetCodeListener {
    private static final String HASH = "Hash";
    private static final String LOCATION = "Location";
    private static final String CODE = "Code";
    private Bitmap codeImage;
    private ImageButton deleteButton;
    private ImageButton commentButton;
    private ImageButton locationButton;
    private ImageView fragmentImageView;
    private TextView fragmentDescriptionView;
    private CardView cardView;

    private Location location;
    private Location playerLocation;
    private String codeDescription;
    private int codePoints;
    private String completeDescription;
    private PlayerProfile player;
    private String hash;
    private GameCode code = null;

    public static GameCodeFragment newInstance(GameCode.CodeLocation codeLocation) {
        Bundle args = new Bundle();
        args.putSerializable(HASH, codeLocation.getHash());
        args.putParcelable(LOCATION, codeLocation.getLocation());
        GameCodeFragment fragment = new GameCodeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static GameCodeFragment newInstance(GameCode code) {
        Bundle args = new Bundle();
        args.putSerializable(CODE, code);
        GameCodeFragment fragment = new GameCodeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public interface GameCodeFragmentHost {
        void createFragment(String hash);

        PlayerProfile getPlayer();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity())
                .inflate(R.layout.fragment_game_code, null);
        fragmentImageView = view.findViewById(R.id.gamecode_image);
        fragmentDescriptionView = view.findViewById(R.id.gamecode_description);
        cardView = view.findViewById(R.id.fragment_card);
        commentButton = view.findViewById(R.id.comment_button);
        locationButton = view.findViewById(R.id.qr_location_button);
        deleteButton = view.findViewById(R.id.delete_button);
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) { }
        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        fusedLocationProviderClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, new CancellationToken() {
            @NonNull
            @Override
            public CancellationToken onCanceledRequested(@NonNull OnTokenCanceledListener onTokenCanceledListener) {
                return null;
            }

            @Override
            public boolean isCancellationRequested() {
                return false;
            }
        }).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                playerLocation = task.getResult();
                if (playerLocation != null) {
                    updateDistance();
                }
            }
        });


        player = MainActivity.getPlayer();
        try {
            code = (GameCode) getArguments().getSerializable(CODE);
            hash = code.getSha256hex();
            location = code.getLocation();
            codeImage = code.getPhoto();
            codeDescription = code.getDescription();
            codePoints = code.getScore();
            completeDescription = codePoints + " pts; \n" + (codeDescription != null ? codeDescription: "");
            fragmentDescriptionView.setText(completeDescription);
            if (player.getCode(hash) == null) {
                deleteButton.setVisibility(View.GONE);
            }
            if (codeImage != null) {
                Handler handler = new Handler();
                handler.postDelayed(() -> {
                    Bitmap scaledImage = Bitmap.createScaledBitmap(codeImage, fragmentImageView.getWidth(),
                            fragmentImageView.getHeight(), false);
                    fragmentImageView.setImageBitmap(scaledImage);
                }, 300);
            } else {
                cardView.setVisibility(View.GONE);
            }
            setListeners();
        } catch (Exception ignored) {
            DataHandler.getInstance().getCode(getArguments().getString(HASH), this);
            cardView.setVisibility(View.GONE);
            fragmentDescriptionView.setText("Loading...");
        }



        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        return builder.setView(view)
                .setPositiveButton("close", null)
                .create();
    }

    @SuppressLint("SetTextI18n")
    private void updateDistance() {
        if (location != null && playerLocation != null) {
            fragmentDescriptionView.setText(completeDescription + "\nDistance from current position: " + (int) location.distanceTo(playerLocation) + "m");
        }
    }

    @Override
    public void onGetCode(GameCode gotCode) {
        code = gotCode;
        location = code.getLocation();
        hash = code.getSha256hex();
        codeImage = code.getPhoto();
        codeDescription = code.getDescription();
        codePoints = code.getScore();
        completeDescription = codePoints + " pts; \n" + (codeDescription != null ? codeDescription: "");
        if (player.getCode(hash) == null) {
            deleteButton.setVisibility(View.GONE);
        }
        fragmentDescriptionView.setText(completeDescription);
        if (codeImage != null) {
            cardView.setVisibility(View.VISIBLE);
            Handler handler = new Handler();
            handler.postDelayed(() -> {
                Bitmap scaledImage = Bitmap.createScaledBitmap(codeImage, fragmentImageView.getWidth(),
                        fragmentImageView.getHeight(), false);
                fragmentImageView.setImageBitmap(scaledImage);
            }, 300);
        }
        updateDistance();
    }

    private void setListeners() {
        GameCode finalCode = code;

        commentButton.setOnClickListener(view1 -> {
            Intent intent = new Intent(view1.getContext(), Comment.class);
            intent.putExtra("hash", hash);
            startActivity(intent);
        });

        HashMap<String,GameCode> codes= player.getCodes();
        if (!codes.containsKey(code.getSha256hex())){
            deleteButton.setVisibility(View.GONE);
        }
        deleteButton.setOnClickListener(v -> {
            View view12 = LayoutInflater.from(getContext()).inflate(R.layout.confirm_delete_qr_prompt, null);
            AlertDialog.Builder deletePrompt = new AlertDialog.Builder(GameCodeFragment.this.getActivity());
            deletePrompt.setView(view12);
            deletePrompt.setCancelable(true)
                    .setTitle("Wait!")
                    .setMessage("Are you sure you want to delete it?")
                    .setNegativeButton("No,Don't!", null)
                    .setPositiveButton("Yes,Delete!", (dialog, which) -> {
                        if (finalCode != null) {
                            //GalleryAdapter galleryAdapter = GalleryAdapter();
                            System.out.println("final: " + player.getCodes());
                            //galleryAdapter.removeImage(finalCode);
                            player.removeCode(finalCode);
                            System.out.println("final after : " + player.getCodes());
                            GalleryAdapter galleryAdapter = GalleryAdapter.getInstance();
                            System.out.println("this is after adapter delete before: " + galleryAdapter.getItemCount() + "is here");
                            galleryAdapter.removeImage(finalCode);
                            galleryAdapter.notifyDataSetChanged();
                            System.out.println("this is after adapter delete: " + galleryAdapter.getItemCount());
                            DataHandler dataHandler = DataHandler.getInstance();
                            dataHandler.updatePlayerJson(player);
                            dataHandler.updatePts(player.getName(), -finalCode.getScore());
                            dataHandler.updateScanned(player.getName(), -1);
                            dataHandler.updateUniqueScanned(player.getName(), -1);
                            MainActivity.refresh();
                            UserProfileActivity.refresh();
                            GameCodeFragment.this.dismiss();
                        }
                    });


            Dialog dialog = deletePrompt.create();
            dialog.show();
        });

        locationButton.setOnClickListener(view12 -> {
            if (finalCode == null) {
                return;
            }
            if (getActivity() instanceof MapActivity) {
                ((MapActivity) getActivity()).setCamera(finalCode.getLocation());
                this.dismiss();
            } else {
                Intent intent = new Intent(getActivity(), MapActivity.class);
                intent.putExtra("QR Location", finalCode.getLocation());
                startActivity(intent);
            }
        });
    }
}