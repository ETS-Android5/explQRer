package com.example.explqrer;

import android.location.Location;

import java.util.ArrayList;

public interface OnGetNearByQrsListener {
    void getNearbyQrs(ArrayList<Location> locations);
}
