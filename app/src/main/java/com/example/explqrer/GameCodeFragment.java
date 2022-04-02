package com.example.explqrer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class GameCodeFragment extends DialogFragment implements OnGetCodeListener {
    private static final String HASH = "Hash";
    private static final String LOCATION = "Location";
    private static final String CODE = "Code";
    private Bitmap codeImage;
    private Button deleteButton;
    private Button commentButton;
    private Button locationButton;

    private Location location;
    private String codeDescription;
    private int codePoints;
    private String completeDescription;

    public interface GameCodeFragmentHost {
        void createFragment(String hash);
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

        GameCode code = null;
        try {
            code = (GameCode) getArguments().getSerializable(CODE);String hash = (String) getArguments().get(HASH);
            location = code.getLocation();
            codeImage = code.getPhoto();
            codeDescription = code.getDescription();
            codePoints = code.getScore();
            completeDescription = codePoints + " pts; " + codeDescription;

        } catch (Exception ignored) {
            DataHandler.getInstance().getCode(getArguments().getString(HASH), this);
        }

        ImageView fragmentImageView = view.findViewById(R.id.gamecode_image);
        TextView fragmentDescriptionView = view.findViewById(R.id.gamecode_description);


        Handler handler = new Handler();
        handler.postDelayed(() -> {
            Bitmap scaledImage = Bitmap.createScaledBitmap(codeImage, fragmentImageView.getWidth(),
                    fragmentImageView.getHeight(), false);
            fragmentImageView.setImageBitmap(scaledImage);
            fragmentDescriptionView.setText(completeDescription);
        }, 300);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        return builder.setView(view)
                .setPositiveButton("Hello", null)
                .create();
    }


    @Override
    public void onGetCode(GameCode code) {
        location = code.getLocation();
        codeImage = code.getPhoto();
        codeDescription = code.getDescription();
        codePoints = code.getScore();
        completeDescription = codePoints + " pts; " + codeDescription;
    }
}
