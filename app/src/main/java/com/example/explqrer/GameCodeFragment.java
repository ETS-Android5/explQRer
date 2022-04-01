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

public class GameCodeFragment extends DialogFragment {
    private static final String HASH = "Hash";
    private static final String LOCATION = "Location";
    private Bitmap codeImage;
    private Button deleteButton;
    private Button commentButton;
    private Button locationButton;


    public static GameCodeFragment newInstance(GameCode.CodeLocation codeLocation, Bitmap codeImage, int codePts,String codeDesciption) {
        Bundle args = new Bundle();
        args.putSerializable(HASH, codeLocation.hash);
        args.putParcelable(LOCATION, codeLocation.location);
        args.putParcelable("Image", codeImage);
        args.putInt("Points", codePts);
        args.putString("Description", codeDesciption);
        GameCodeFragment fragment = new GameCodeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity())
                .inflate(R.layout.fragment_game_code, null);

        String hash = (String) getArguments().get(HASH);
        Location location = (Location) getArguments().get(LOCATION);
        Bitmap codeImage = getArguments().getParcelable("Image");
        String codeDescription = getArguments().getString("Description");
        int codePoints = getArguments().getInt("Points");
        String completeDescription = codePoints+ " pts; "+ codeDescription;


        ImageView fragmentImageView = view.findViewById(R.id.gamecode_image);
        TextView fragmentDescriptionView = view.findViewById(R.id.gamecode_description);


        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Bitmap scaledImage = Bitmap.createScaledBitmap(codeImage, fragmentImageView.getWidth(), fragmentImageView.getHeight(), false);
                fragmentImageView.setImageBitmap(scaledImage);
                fragmentDescriptionView.setText(completeDescription);
            }
        }, 300);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        return builder.setView(view)
                .setPositiveButton("Hello", null)
                .create();
    }
}
