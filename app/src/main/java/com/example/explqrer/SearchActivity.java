package com.example.explqrer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SearchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.user_recyclerview);
        Button search = (Button) findViewById(R.id.search_Button);
        EditText searchBar = (EditText) findViewById(R.id.search_Bar);

        // Get the text from the search bar
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String searchText = searchBar.getText().toString();

            }
        });
    }
}