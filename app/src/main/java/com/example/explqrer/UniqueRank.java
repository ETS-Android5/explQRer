package com.example.explqrer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;

public class UniqueRank extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener{

    ArrayList<ScannedRankLeaderboard> scannedRankLeaderboards = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unique_rank);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_leaderboard);
        bottomNavigationView.setSelectedItemId(R.id.unique_rank);
        bottomNavigationView.setOnItemSelectedListener((NavigationBarView.OnItemSelectedListener) this);

        RecyclerView recyclerView = findViewById(R.id.UR_recyclerView);

        Context context = this;
        DataHandler dh = DataHandler.getInstance();
        dh.getUniqueLeaderBoard(new OnGetUniqueLeaderBoardListener() {
            @Override
            public void getUniqueLeaderBoardListener(ArrayList<String> leaderboard) {
                findViewById(R.id.progressBar4).setVisibility(View.GONE);
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
        overridePendingTransition(0,0);
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
                scannedIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(scannedIntent);
                return true;

            case R.id.points_rank:
                // Display points leaderboard
                Intent pointsIntent = new Intent(this, PointsRank.class);
                pointsIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(pointsIntent);
                return true;

            case R.id.unique_rank:
                // Display unique leaderboard
                return true;
        }
        return false;
    }
}