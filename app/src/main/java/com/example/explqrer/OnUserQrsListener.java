package com.example.explqrer;

import java.util.ArrayList;

public interface OnUserQrsListener {
    void onUserQrsFilled(ArrayList<String> qrList);

    void onError(Exception taskException);
}
