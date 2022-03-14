package com.example.explqrer;

import static com.example.explqrer.R.id.image;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

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
     *
     * @param context     context is where the adapter is being used
     * @param galleryList galleryList is the array list for the adapter
     */
    public GalleryAdapter(Context context, ArrayList<GalleryListItem> galleryList) {
        this.galleryList = galleryList;
        this.context = context;
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
        // initialize view Model
        mainViewModel = ViewModelProvider((FragmentActivity) context).get(MainViewModel.class);
        return new ViewHolder(view);
    }

    private <E> List ViewModelProvider(FragmentActivity context) {
        return null;
    }

    /**
     * @param viewHolder The ViewHolder to hold the
     * @param i          The number of items per row
     */
    @Override
    public void onBindViewHolder(GalleryAdapter.ViewHolder viewHolder, int i) {
        viewHolder.image.setScaleType(ImageView.ScaleType.CENTER_CROP);
        viewHolder.image.setImageBitmap(galleryList.get(i).getImage());
    }

    private void ClickItem(ViewHolder holder) {

        // get selected item value
        String s = galleryList.get(holder.getAdapterPosition());
        // check condition
        if (holder.checkbox.getVisibility() == View.GONE) {
            // when item not selected
            // visible check box image
            holder.checkbox.setVisibility(View.VISIBLE);
            // set background color
            holder.itemView.setBackgroundColor(Color.LTGRAY);
            // add value in select array list
            selectList.add(s);
        } else {
            // when item selected
            // hide check box image
            holder.checkbox.setVisibility(View.GONE);
            // set background color
            holder.itemView.setBackgroundColor(Color.TRANSPARENT);
            // remove value from select arrayList
            selectList.remove(s);

        }
        // set text on view model
        mainViewModel.setText(String.valueOf(selectList.size()));
    }


    /**
     * This gets how many items are in the adapter
     *
     * @return size of the galleryList of type int
     */
    @Override
    public int getItemCount() {
        return galleryList.size();
    }

    /**
     * This class creates the ViewHolder which makes it easier to iterate through images
     */
    static class ViewHolder extends RecyclerView.ViewHolder {
        public View checkbox;
        private final ImageView image;

        public ViewHolder(View view) {
            super(view);

            image = view.findViewById(R.id.image);
        }
    }
}
