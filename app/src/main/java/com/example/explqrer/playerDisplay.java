package com.example.explqrer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import org.w3c.dom.Text;

public class playerDisplay extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_display);
        Intent intent = getIntent();
        String username = intent.getStringExtra("playerName");

        TextView usernameHolder = findViewById(R.id.usernameHolder);
        TextView pointsHolder = findViewById(R.id.pointsHolder);
        TextView scannedHolder = findViewById(R.id.scannedHolder);
        DataHandler dh = DataHandler.getInstance();

        dh.getPlayer(username, new OnGetPlayerListener() {
            @Override
            public void getPlayerListener(PlayerProfile player) {
                usernameHolder.setText(player.getName());
                String points = "Points: " + player.getPoints();
                pointsHolder.setText(points);
                String scanned = "Scanned: " + player.getNumCodes();
                scannedHolder.setText(scanned);
                GalleryBuilder.populateGallery(player,findViewById(R.id.imageHolder),getApplicationContext());
            }
        });
    }
}