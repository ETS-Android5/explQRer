package com.example.explqrer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class CommentsAdaptor extends ArrayAdapter<Comments> {
    private ArrayList<Comments> comments;
    private Context context;

    public CommentsAdaptor(Context context, ArrayList<Comments> comments) {
        super(context, 0,  comments);
        this.comments = comments;
        this.context = context;
    }

    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View view = convertView;

        if (view == null) {
            // Fix this during integration
            view = LayoutInflater.from(context).inflate(R.layout.content, parent, false);
        }
        Comments comment = comments.get(position);

        // Fix this during integration
        TextView userName = view.findViewById(R.id.city_text);
        TextView commented = view.findViewById(R.id.province_text);

        userName.setText(comment.getUsername());
        commented.setText(comment.getComment());

        return view;

    }
}
