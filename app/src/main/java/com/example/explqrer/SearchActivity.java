package com.example.explqrer;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {
    private static SearchActivity instance;

    public static void refresh() {
        instance.recreate();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        instance = this;
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.user_recyclerview);
        Button search = (Button) findViewById(R.id.search_Button);
        EditText searchBar = (EditText) findViewById(R.id.search_Bar);

        Context context = this;

        // Get the text from the search bar
        search.setOnClickListener(view -> {
            String searchText = searchBar.getText().toString();
            findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
            DataHandler.getInstance().searchPlayers(searchText, players -> {
                findViewById(R.id.progressBar).setVisibility(View.GONE);
                ArrayList<ScannedRankLeaderboard> scannedRankLeaderboards = new ArrayList<>();
                int rank = 1;
                for (String name : players) {
                    String rankString = rank++ + "";
                    scannedRankLeaderboards.add(new ScannedRankLeaderboard(rankString, name));
                }
                RecyclerViewAdapterLeaderBoard adapter = new RecyclerViewAdapterLeaderBoard(context, scannedRankLeaderboards);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            });
        });
    }
}