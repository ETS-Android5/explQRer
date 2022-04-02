package com.example.explqrer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;

public class PointsRank extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener{

    ArrayList<ScannedRankLeaderboard> scannedRankLeaderboards = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_points_rank);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_leaderboard);
        bottomNavigationView.setSelectedItemId(R.id.points_rank);
        bottomNavigationView.setOnItemSelectedListener((NavigationBarView.OnItemSelectedListener) this);

        RecyclerView recyclerView = findViewById(R.id.PR_recyclerView);

        Context context = this;
        DataHandler dh = DataHandler.getInstance();
        dh.getPtsLeaderBoard(new OnGetPtsLeaderBoardListener() {
            @Override
            public void getPtsLeaderBoardListener(ArrayList<String> leaderboard) {
                ArrayList<ScannedRankLeaderboard> scannedRankLeaderboards = new ArrayList<>();
                int rank = 1;
                System.out.println(leaderboard.toArray());
                for(String name : leaderboard){
                    System.out.println(name);
                    String rankString = rank++ +"";
                    scannedRankLeaderboards.add(new ScannedRankLeaderboard(rankString, name));
                }
//                setUpScannedRankLeaderboard(leaderboard);
                RecyclerViewAdapterLeaderBoard adapter = new RecyclerViewAdapterLeaderBoard(context, scannedRankLeaderboards);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            }
        });
    }

    /**
     * Called when a navigation item is selected
     * @param item the selected item
     * @return True if the selection was processed successfully
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.scanned_rank:
                // Display scanned leaderboard
                Intent scannedIntent = new Intent(this, ScannedRank.class);
                startActivity(scannedIntent);
                return true;

            case R.id.points_rank:
                // Display points leaderboard
                return true;

            case R.id.unique_rank:
                // Display unique leaderboard
                Intent uniqueIntent = new Intent(this, UniqueRank.class);
                startActivity(uniqueIntent);
                return true;
        }
        return false;
    }
}