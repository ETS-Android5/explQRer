package com.example.explqrer;


import static com.example.explqrer.R.id.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;

/**
 * Link: https://www.androidauthority.com/how-to-build-an-image-gallery-app-718976/
 * Author: Adam Sinicki
 * This is a class for a custom adapter for the recycler View
 */
public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {

    private ArrayList<GalleryListItem> galleryList;
    private Context context;

    /**
     * Constructor for the class
     * @param context
     *   context is where the adapter is being used
     * @param galleryList
     *  galleryList is the array list for the adapter
     */
    public GalleryAdapter(Context context, ArrayList<GalleryListItem> galleryList) {
        this.galleryList = galleryList;
        this.context = context;
        System.out.println("in adapter");
    }

    /**
     *sets up the ViewHolder for the recycler View
     * @param viewGroup
     *    where the viewHolder will be grouped
     * @param i
     * @return ViewHolder with the defined view parameters
     */
    @NonNull
    @Override
    public GalleryAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.image_cell_layout, viewGroup, false);
        return new ViewHolder(view);
    }

    /**
     *
     * @param viewHolder
     *      The ViewHolder to hold the
     * @param i
     *      The number of items per row
     */
    @Override
    public void onBindViewHolder(GalleryAdapter.ViewHolder viewHolder, int i) {
        viewHolder.image.setScaleType(ImageView.ScaleType.CENTER_CROP);
        System.out.println("user holder: "+ galleryList);
        System.out.println(galleryList.get(i).getImage());
        //viewHolder.image.setImageBitmap(Bitmap.createBitmap(context.getResources(),R.drawable.nature2);
        Uri image = Uri.fromFile(new File(galleryList.get(i).getImage()));
        System.out.println(image);
        viewHolder.image.setImageURI(image);
    }

    /**
     * This gets how many items are in the adapter
     * @return size of the galleryList of type int
     */
    @Override
    public int getItemCount() {
        return galleryList.size();
    }

    /**
     * This class creates the ViewHolder which makes it easier to iterate through images
     */
    public class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView image;
        public ViewHolder(View view) {
            super(view);

            image = view.findViewById(R.id.image);
        }
    }

}
