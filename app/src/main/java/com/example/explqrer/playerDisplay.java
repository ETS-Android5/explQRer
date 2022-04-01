package com.example.explqrer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class playerDisplay extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_display);
        Intent intent = getIntent();
        String username = intent.getStringExtra("playerName");

        TextView usernameHolder = findViewById(R.id.usernameHolder);
        usernameHolder.setText(username);
        DataHandler dh = DataHandler.getInstance();

        dh.getPlayer(username, new OnGetPlayerListener() {
            @Override
            public void getPlayerListener(PlayerProfile player) {
                usernameHolder.setText(player.getName());
            }
        });
    }
}