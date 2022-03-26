package com.example.explqrer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class GameCodeFragment extends DialogFragment {
    private static final String HASH = "Hash";
    private static final String LOCATION = "Location";
    GameCodeFragmentListener listener;

    public interface GameCodeFragmentListener {
        void openLocation(GameCode.CodeLocation codeLocation);
        void openComments(GameCode code);
    }

    public static GameCodeFragment newInstance(GameCode.CodeLocation codeLocation) {
        Bundle args = new Bundle();
        args.putSerializable(HASH, codeLocation.hash);
        args.putParcelable(LOCATION, codeLocation.location);
        GameCodeFragment fragment = new GameCodeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof GameCodeFragmentListener) {
            listener = (GameCodeFragmentListener) context;
        } else {
            throw new RuntimeException(context +
                    " must implement GameCodeFragmentListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity())
                .inflate(R.layout.fragment_game_code, null);
        String hash = (String) getArguments().get(HASH);
        Location location = (Location) getArguments().get(LOCATION);




        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        return builder.setView(view)
                .setPositiveButton("Hello", null)
                .create();
    }
}
