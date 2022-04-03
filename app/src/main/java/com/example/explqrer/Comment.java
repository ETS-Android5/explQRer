package com.example.explqrer;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Map;

/**
 * class representing comments
 */
public class Comment extends AppCompatActivity {

    Button add;
    AlertDialog dialog;
    LinearLayout layout;
    String hash;
    DataHandler dh;

    /**
     * Method for adding comments
     * @param savedInstanceState
     * This is the saving instance object for comment
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        Intent intent = getIntent();
        hash = intent.getStringExtra("hash");
        add = findViewById(R.id.add);
        layout = findViewById(R.id.container);
        dh = DataHandler.getInstance();
        dh.getComments(hash, new OnGetCommentsListener() {
            @Override
            public void getCommentsListener(ArrayList<Map<String, String>> comments) {
                if (comments != null){
                    for(Map<String,String> comment : comments){
                        for(String name: comment.keySet()){
                            addCard(name, comment.get(name));
                        }
                    }
                }
            }
        });
        buildDialog();

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });
    }

    /**
     * Method to show the pop up for accepting comments
     */
    private void buildDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog, null);

        final EditText name = view.findViewById(R.id.nameEdit);

        builder.setView(view);
        builder.setTitle("Comment")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        PlayerProfile player = MainActivity.getPlayer();
                        dh.addComment(hash, player.getName(),name.getText().toString());
                        addCard(player.getName(), name.getText().toString());
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        dialog = builder.create();
    }

    /**
     * Method to display the comments that have
     * already been made
     * @param name
     * Displaying comments
     */
    private void addCard(String name, String comment) {
        final View view = getLayoutInflater().inflate(R.layout.card, null);

        TextView commentView = view.findViewById(R.id.commentHolderCard);
        TextView nameView = view.findViewById(R.id.usernameHolderCard);
        commentView.setText(comment);
        nameView.setText(name);

        layout.addView(view);
    }
}
