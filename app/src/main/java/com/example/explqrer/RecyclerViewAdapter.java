package com.example.explqrer;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {
    /* Site: stackoverflow.com
     * Link: https://stackoverflow.com/questions/28460300
     * Author: https://stackoverflow.com/users/3681880/suragch
     */
    private ArrayList<String> banner;
    private LayoutInflater mInflater;

    // data is passed into the constructor
    RecyclerViewAdapter(Context context, ArrayList<String> banner) {
        this.mInflater = LayoutInflater.from(context);
        this.banner = banner;
        Log.e("", "Adapter");
    }

    // inflates the row layout from xml when needed
    @Override
    @NonNull
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.e("", "onCreateViewHolder");
        View view = mInflater.inflate(R.layout.recyclerview, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String pts_and_scanned = banner.get(position);
        holder.textView.setText(pts_and_scanned);
        Log.e("", "BindView");
    }

    @Override
    public int getItemCount() {
        return banner.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView textView;
        public MyViewHolder(View view) {
            super(view);
            textView = view.findViewById(R.id.pts_and_scanned);

        }
    }
}
