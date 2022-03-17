package com.example.explqrer;

import java.util.Map;

public interface OnGetQrsListener {
    void onQrFilled(Map<String, Object> map);

    void onError(Exception taskException);
}
