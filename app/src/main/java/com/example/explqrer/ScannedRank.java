package com.example.explqrer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;

/**
 * all the methods to create the Leaderboard
 */

public class ScannedRank extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanned_rank);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_leaderboard);
        bottomNavigationView.setSelectedItemId(R.id.scanned_rank);
        bottomNavigationView.setOnItemSelectedListener((NavigationBarView.OnItemSelectedListener) this);

        RecyclerView recyclerView = findViewById(R.id.SR_recyclerView);


        Context context = this;
        DataHandler dh = new DataHandler();
        dh.getQrLeaderBoard(new OnGetQrLeaderBoardListener() {
            @Override
            public void getQrLeaderBoardListener(ArrayList<String> leaderboard) {
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
                return true;

            case R.id.points_rank:
                // Display points leaderboard
                Intent pointsIntent = new Intent(this, PointsRank.class);
                pointsIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(pointsIntent);

                return true;

            case R.id.unique_rank:
                // Display unique leaderboard
                Intent uniqueIntent = new Intent(this, UniqueRank.class);
                uniqueIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(uniqueIntent);
                return true;
        }
        return false;
    }
}