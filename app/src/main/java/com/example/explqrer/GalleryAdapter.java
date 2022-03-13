package com.example.explqrer;

import static com.example.explqrer.R.id.image;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

/**
 * This is a class for a custome adapter for the recycler View
 */
public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {

    private ArrayList<ImageListItem> galleryList;
    private Context context;

    /**
     * Constructor for the class
     * @param context
     *   context is where the adapter is being used
     * @param galleryList
     *  galleryList is the array list for the adapter
     */
    public GalleryAdapter(Context context, ArrayList<ImageListItem> galleryList) {
        this.galleryList = galleryList;
        this.context = context;
    }

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
        viewHolder.image.setImageResource((galleryList.get(i).getImageId()));
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
