package com.example.explqrer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import org.w3c.dom.Text;

public class PlayerDisplayActivity extends AppCompatActivity
        implements OnGetPlayerListener, GameCodeFragment.GameCodeFragmentHost {
    private PlayerProfile player;
    private TextView usernameHolder;
    private TextView pointsHolder;
    private TextView scannedHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_display);
        Intent intent = getIntent();
        String username = intent.getStringExtra("playerName");

        usernameHolder = findViewById(R.id.usernameHolder);
        pointsHolder = findViewById(R.id.pointsHolder);
        scannedHolder = findViewById(R.id.scannedHolder);
        DataHandler dh = DataHandler.getInstance();

        dh.getPlayer(username, this);
    }

    @Override
    public void createFragment(String hash) {
        GameCodeFragment gameCodeFragment = GameCodeFragment.newInstance(player.getCode(hash));
        gameCodeFragment.show(getSupportFragmentManager(), "GAME_CODE");
    }

    @Override
    public PlayerProfile getPlayer() {
        return player;
    }

    @Override
    public void getPlayerListener(PlayerProfile player) {
        this.player = player;
        usernameHolder.setText(player.getName());
        String points = "Points: " + player.getPoints();
        pointsHolder.setText(points);
        String scanned = "Scanned: " + player.getNumCodes();
        scannedHolder.setText(scanned);
        GalleryBuilder.populateGallery(player,findViewById(R.id.imageHolder),
                getApplicationContext(), this);
    }
}