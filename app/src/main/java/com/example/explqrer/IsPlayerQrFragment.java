package com.example.explqrer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class IsPlayerQrFragment extends DialogFragment {
    private IsPlayerQrFragmentListener listener;

    static enum RETURNS {SCAN, SEE_PROFILE, LOG_IN}
    public interface IsPlayerQrFragmentListener {
        void processDecision(RETURNS value);
    }
    
    public static IsPlayerQrFragment newInstance(String username) {
        Bundle args = new Bundle();
        args.putSerializable("Name", username);

        IsPlayerQrFragment fragment = new IsPlayerQrFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof IsPlayerQrFragmentListener) {
            listener = (IsPlayerQrFragmentListener) context;
        } else {
            throw new RuntimeException(context
                    + " must implement IsPlayerQrFragmentListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity())
                .inflate(R.layout.is_player_qr_fragment_layout, null);


        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        return builder.setView(view)
                .setNegativeButton("Cancel", (dialogInterface, i) -> { })
                .create();
    }
}
