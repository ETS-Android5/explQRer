package com.example.explqrer;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class Gallery extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery);

        RecyclerView recyclerView = findViewById(R.id.image_gallery);
        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(),4);
        recyclerView.setLayoutManager(layoutManager);
        Integer[] imagePoints = {1,4,5,7,3};
        Integer imageIds[] = {R.drawable.nature1,R.drawable.nature2,R.drawable.nature3,R.drawable.nature4,R.drawable.nature5};


        ArrayList<ImageListItem> listOfImages = new ArrayList<>();
        for( int i = 0; i < imageIds.length;i++){
            ImageListItem imageListItem = new ImageListItem();
            imageListItem.setImageId(imageIds[i]);
            imageListItem.setImagePoints(imagePoints[i]);
            listOfImages.add(imageListItem);
        }

        GalleryAdapter imageListAdapter = new GalleryAdapter(getApplicationContext(),listOfImages);
        recyclerView.setAdapter(imageListAdapter);
    }

}
