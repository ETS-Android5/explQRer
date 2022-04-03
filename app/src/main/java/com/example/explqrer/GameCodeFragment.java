package com.example.explqrer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;

public class GameCodeFragment extends DialogFragment implements OnGetCodeListener {
    private static final String HASH = "Hash";
    private static final String LOCATION = "Location";
    private static final String CODE = "Code";
    private Bitmap codeImage;
    private Button deleteButton;
    private ImageButton commentButton;
    private Button locationButton;
    private ImageView fragmentImageView;
    private TextView fragmentDescriptionView;
    private CardView cardView;

    private Location location;
    private String codeDescription;
    private int codePoints;
    private String completeDescription;
    private String hash;

    public interface GameCodeFragmentHost {
        void createFragment(String hash);
        PlayerProfile getPlayer();
    }


    public static GameCodeFragment newInstance(GameCode.CodeLocation codeLocation) {
        Bundle args = new Bundle();
        args.putSerializable(HASH, codeLocation.hash);
        args.putParcelable(LOCATION, codeLocation.location);
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

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity())
                .inflate(R.layout.fragment_game_code, null);
        fragmentImageView = view.findViewById(R.id.gamecode_image);
        fragmentDescriptionView = view.findViewById(R.id.gamecode_description);
        cardView = view.findViewById(R.id.fragment_card);
        commentButton = view.findViewById(R.id.comment_button);

        GameCode code;
        try {
            code = (GameCode) getArguments().getSerializable(CODE);
            hash = code.getSha256hex();
            location = code.getLocation();
            codeImage = code.getPhoto();
            codeDescription = code.getDescription();
            codePoints = code.getScore();
            completeDescription = codePoints + " pts; " + codeDescription;
            Handler handler = new Handler();
            handler.postDelayed(() -> {
                Bitmap scaledImage = Bitmap.createScaledBitmap(codeImage, fragmentImageView.getWidth(),
                        fragmentImageView.getHeight(), false);
                fragmentImageView.setImageBitmap(scaledImage);
                fragmentDescriptionView.setText(completeDescription);
            }, 300);
        } catch (Exception ignored) {
            DataHandler.getInstance().getCode(getArguments().getString(HASH), this);
            cardView.setVisibility(View.GONE);
            fragmentDescriptionView.setText("Loading...");
        }


        commentButton.setOnClickListener(view1 -> {
            Intent intent = new Intent(view1.getContext(),Comment.class);
            intent.putExtra("hash", hash);
            startActivity(intent);
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        return builder.setView(view)
                .setPositiveButton("Hello", null)
                .create();
    }


    @Override
    public void onGetCode(GameCode code) {
        location = code.getLocation();
        hash = code.getSha256hex();
        codeImage = code.getPhoto();
        codeDescription = code.getDescription();
        codePoints = code.getScore();
        completeDescription = codePoints + " pts; " + codeDescription;
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            if (codeImage != null) {
                cardView.setVisibility(View.VISIBLE);
                Bitmap scaledImage = Bitmap.createScaledBitmap(codeImage, 410, 400, false);
                fragmentImageView.setImageBitmap(scaledImage);
            }
            fragmentDescriptionView.setText(completeDescription);
        }, 300);
    }
}
