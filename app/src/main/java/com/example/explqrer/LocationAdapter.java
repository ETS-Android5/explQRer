package com.example.explqrer;

import android.content.Context;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class LocationAdapter extends ArrayAdapter<GameCode.CodeLocation> {
    private ArrayList<GameCode.CodeLocation> locations;
    private Context context;
    private float distance;
    private double playerLongitude;
    private double playerLatitude;
    private int score;


    public LocationAdapter(Context context, ArrayList<GameCode.CodeLocation> locations, double playerLongitude, double playerLatitude){
        super(context,0,locations);
        this.context = context;
        this.locations = locations;
        this.playerLongitude = playerLongitude;
        this.playerLatitude = playerLatitude;
    }

    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if (view ==null){
            view = LayoutInflater.from(context).inflate(R.layout.location_content,parent,false);
        }

        GameCode.CodeLocation codeLocation = locations.get(position);


        TextView locationView = view.findViewById(R.id.location);
        TextView distanceView = view.findViewById(R.id.distance);

        Location location = codeLocation.getLocation(); // nearby code location
        score = GameCode.calculateScoreFromRaw(codeLocation.getHash());
        locationView.setText(String.valueOf(score));

        Location playerLocation = new Location("");// player location
        playerLocation.setLongitude(playerLongitude);
        playerLocation.setLatitude(playerLatitude);
        distance= location.distanceTo(playerLocation);
        distanceView.setText(String.format("%.2f",distance));

        return view;

    }

}
