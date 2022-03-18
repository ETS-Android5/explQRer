package com.example.explqrer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.errorprone.annotations.Var;

public class IsPlayerQrFragment extends DialogFragment {
    private IsPlayerQrFragmentListener listener;

    private Button scanButton, profileButton, loginButton;


    public interface IsPlayerQrFragmentListener {
        void processDecision(ScanningPageActivity.RETURNS value, String rawValue);
    }
    
    public static IsPlayerQrFragment newInstance(String rawValue) {
        Bundle args = new Bundle();
        args.putSerializable("Value", rawValue);

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
        scanButton = view.findViewById(R.id.player_qr_alert_scan_button);
        scanButton.setOnClickListener(view1 -> {
            listener.processDecision(ScanningPageActivity.RETURNS.SCAN);
            getParentFragmentManager().beginTransaction().remove(IsPlayerQrFragment.this);
        });
        profileButton = view.findViewById(R.id.player_qr_alert_view_button);
        profileButton.setOnClickListener(view1 -> {
            listener.processDecision(ScanningPageActivity.RETURNS.SEE_PROFILE);
            getParentFragmentManager().beginTransaction().remove(IsPlayerQrFragment.this);
        });
        loginButton = view.findViewById(R.id.player_qr_alert_log_in_button);
        loginButton.setOnClickListener(view1 -> {
            listener.processDecision(ScanningPageActivity.RETURNS.LOG_IN);
            getParentFragmentManager().beginTransaction().remove(IsPlayerQrFragment.this);
        });


        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        return builder.setView(view)
                .setNegativeButton("Cancel", (dialogInterface, i) -> { })
                .create();
    }
}
