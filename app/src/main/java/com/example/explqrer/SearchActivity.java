package com.example.explqrer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.user_recyclerview);
        Button search = (Button) findViewById(R.id.search_Button);
        EditText searchBar = (EditText) findViewById(R.id.search_Bar);

        Context context = this;

        // Get the text from the search bar
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String searchText = searchBar.getText().toString();
                new DataHandler().searchPlayers(searchText, new OnSearchPlayersListener() {
                    @Override
                    public void searchPlayersListener(ArrayList<String> players) {
                        ArrayList<ScannedRankLeaderboard> scannedRankLeaderboards = new ArrayList<>();
                        int rank = 1;
                        for(String name : players){
                            String rankString = rank++ +"";
                            scannedRankLeaderboards.add(new ScannedRankLeaderboard(rankString, name));
                        }
                        RecyclerViewAdapterLeaderBoard adapter = new RecyclerViewAdapterLeaderBoard(context, scannedRankLeaderboards);
                        recyclerView.setAdapter(adapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(context));
                    }
                });
            }
        });
    }
}