package com.example.explqrer;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerViewAdapterLeaderBoard extends RecyclerView.Adapter<RecyclerViewAdapterLeaderBoard.MyViewHolder>{
    Context context;
    ArrayList<ScannedRankLeaderboard> scannedRankLeaderboards;

    public RecyclerViewAdapterLeaderBoard(Context context, ArrayList<ScannedRankLeaderboard> scannedRankLeaderboards){
        this.context = context;
        this.scannedRankLeaderboards = scannedRankLeaderboards;
    }

    @NonNull
    @Override
    public RecyclerViewAdapterLeaderBoard.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // This is where you inflate the layout
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recyclerview_leaderboard, parent, false);
        return new RecyclerViewAdapterLeaderBoard.MyViewHolder(view,this.context);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapterLeaderBoard.MyViewHolder holder, int position) {
        // Assign values to the views we created in the recyclerview_leaderboard layout file
        // based on the position of the recycler view
        holder.playerRank.setText(scannedRankLeaderboards.get(position).getPlayerRank());
        holder.playerName.setText(scannedRankLeaderboards.get(position).getPlayerName());

    }

    @Override
    public int getItemCount() {
        // Number of items you want displayed
        return scannedRankLeaderboards.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView playerRank, playerName;

        public MyViewHolder(@NonNull View itemView,Context context) {
            super(itemView);
             playerRank = itemView.findViewById(R.id.playerRank);
             playerName = itemView.findViewById(R.id.playerName);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String name = playerName.getText().toString();
                    Intent myIntent = new Intent(context, PlayerDisplayActivity.class);
                    myIntent.putExtra("playerName",name);
                    context.startActivity(myIntent);
                }
            });
        }
    }
}
