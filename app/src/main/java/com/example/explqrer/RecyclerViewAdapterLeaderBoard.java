package com.example.explqrer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

             PlayerProfile currentPlayer = MainActivity.getPlayer();
//             currentPlayer.setAsAdmin();
             itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!currentPlayer.isAdmin()){
                        viewPlayer(context);
                    }
                    else{
                        // Create the object of
                        // AlertDialog Builder class
                        AlertDialog.Builder builder
                                = new AlertDialog
                                .Builder(context);

                        // Set the message show for the Alert time
                        builder.setMessage("Do you want to delete or view");

                        // Set Alert Title
                        builder.setTitle("Admin Function!");

                        // Set Cancelable false
                        // for when the user clicks on the outside
                        // the Dialog Box then it will remain show
                        builder.setCancelable(false);

                        // Set the positive button with yes name
                        // OnClickListener method is use of
                        // DialogInterface interface.

                        builder
                                .setPositiveButton(
                                        "Delete",
                                        new DialogInterface
                                                .OnClickListener() {

                                            @Override
                                            public void onClick(DialogInterface dialog,
                                                                int which)
                                            {
                                                String name = playerName.getText().toString();
                                                DataHandler dh = DataHandler.getInstance();
                                                dh.deletePlayer(name);
                                            }
                                        });

                        // Set the Negative button with No name
                        // OnClickListener method is use
                        // of DialogInterface interface.
                        builder
                                .setNegativeButton(
                                        "View",
                                        new DialogInterface
                                                .OnClickListener() {

                                            @Override
                                            public void onClick(DialogInterface dialog,
                                                                int which)
                                            {
                                                viewPlayer(context);
                                            }
                                        });

                        // Create the Alert dialog
                        AlertDialog alertDialog = builder.create();

                        // Show the Alert Dialog box
                        alertDialog.show();
                    }
                }
            });
        }

        void viewPlayer(Context context){
            String name = playerName.getText().toString();
            Intent myIntent = new Intent(context,PlayerDisplayActivity.class);
            myIntent.putExtra("playerName",name);
            context.startActivity(myIntent);
        }
    }
}
