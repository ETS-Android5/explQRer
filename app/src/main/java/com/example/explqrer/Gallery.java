package com.example.explqrer;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;

public class Gallery extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery_layout);

        RecyclerView recyclerView = findViewById(R.id.image_gallery);
        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(),4);
        recyclerView.setLayoutManager(layoutManager);
        Integer[] imagePoints = {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29};
        Integer[] imageIds;
        imageIds = new Integer[]{R.drawable.nature1, R.drawable.nature2, R.drawable.nature3,
                R.drawable.nature4,
                R.drawable.nature5,
                R.drawable.nature6,
                R.drawable.nature7,
                R.drawable.nature8,
                R.drawable.nature9,
                R.drawable.nature10,
                R.drawable.nature11,
                R.drawable.nature12,
                R.drawable.nature13,
                R.drawable.nature14,
                R.drawable.nature15,
                R.drawable.nature16,
                R.drawable.nature17,
                R.drawable.nature18,
                R.drawable.nature19,
                R.drawable.nature20,
                R.drawable.nature21,
                R.drawable.nature22,
                R.drawable.nature23,
                R.drawable.nature24,
                R.drawable.nature25,
                R.drawable.nature26,
                R.drawable.nature27,
                R.drawable.nature28,
                R.drawable.nature29
                };
        /*  R.drawable.nature6,
                R.drawable.nature7,
                R.drawable.nature8,
                R.drawable.nature9,
                R.drawable.nature10,
                R.drawable.nature11,
                R.drawable.nature12,
                R.drawable.nature13,
                R.drawable.nature14,
                R.drawable.nature15,
                R.drawable.nature16,
                R.drawable.nature17,
                R.drawable.nature18,
                R.drawable.nature19*/




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
