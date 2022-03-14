package com.example.explqrer;

import static com.example.explqrer.R.id.image;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.ActionMode;
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
 * This is a class for a custom adapter for the recycler View
 */
public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {

    private final ArrayList<ImageListItem> galleryList;
    private final Context context;
    MainViewModel mainViewModel;
    TextView tv_empty;
    boolean isEnable = false;
    boolean isSelectAll = false;
    ArrayList<String> selectList = new ArrayList<>();

    /**
     * Constructor for the class
     *
     * @param context     context is where the adapter is being used
     * @param galleryList galleryList is the array list for the adapter
     */
    public GalleryAdapter(Context context, ArrayList<ImageListItem> galleryList) {
        this.galleryList = galleryList;
        this.context = context;
    }

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
        viewHolder.image.setImageResource((galleryList.get(i).getImageId()));

        viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                // check condition
                if (!isEnable) {
                    // when action mode is not enable
                    // initialize action mode
                    ActionMode.Callback callback = new ActionMode.Callback() {
                        @Override
                        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                            // initialize menu inflater
                            MenuInflater menuInflater = mode.getMenuInflater();
                            // inflate menu
                            menuInflater.inflate(R.menu.select_items, menu);
                            // return true
                            return true;
                        }

                        @Override
                        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                            // when action mode is prepare
                            // set isEnable true
                            isEnable = true;
                            // create method
                            ClickItem(viewHolder);
                            // set observer on getText method
                            mainViewModel.getText().observe((LifecycleOwner) context
                                    , new Observer<String>() {
                                        @Override
                                        public void onChanged(String s) {
                                            // when text change
                                            // set text on action mode title
                                            mode.setTitle(String.format("%s Selected", s));
                                        }
                                    });
                            // return true
                            return true;
                        }

                        @SuppressLint({"NonConstantResourceId", "NotifyDataSetChanged"})
                        @Override
                        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                            // when click on action mode item
                            // get item  id
                            int id = item.getItemId();
                            // use switch condition
                            switch (id) {
                                case R.id.menu_delete:
                                    // when click on delete
                                    // use for loop
                                    for (String s : selectList) {
                                        // remove selected item list
                                        galleryList.remove(s);
                                    }
                                    // check condition
                                    if (galleryList.size() == 0) {
                                        // when array list is empty
                                        // visible text view
                                        tv_empty.setVisibility(View.VISIBLE);
                                    }
                                    // finish action mode
                                    mode.finish();
                                    break;

                                case R.id.menu_select_all:
                                    // when click on select all
                                    // check condition
                                    if (selectList.size() == galleryList.size()) {
                                        // when all item selected
                                        // set isselectall false
                                        isSelectAll = false;
                                        // create select array list
                                        selectList.clear();
                                    } else {
                                        // when  all item unselected
                                        // set isSelectALL true
                                        isSelectAll = true;
                                        // clear select array list
                                        selectList.clear();
                                        // add value in select array list
                                        selectList.addAll(galleryList);
                                    }
                                    // set text on view model
                                    mainViewModel.setText(String.valueOf(selectList.size()));
                                    // notify adapter
                                    notifyDataSetChanged();
                                    break;
                            }
                            // return true
                            return true;
                        }

                        @SuppressLint("NotifyDataSetChanged")
                        @Override
                        public void onDestroyActionMode(ActionMode actionMode) {
                            // when action mode is destroy
                            // set isEnable false
                            isEnable = false;
                            // set isSelectAll false
                            isSelectAll = false;
                            // clear select array list
                            selectList.clear();
                            // notify adapter
                            notifyDataSetChanged();
                        }
                    };
                    // start action mode
                    ((AppCompatActivity) v.getContext()).startActionMode(callback);
                } else {
                    // when action mode is already enable
                    // call method
                    ClickItem(viewHolder);
                }
                // return true
                return true;
            }
        });
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // check condition
                if (isEnable) {
                    // when action mode is enable
                    // call method
                    ClickItem(viewHolder);
                } else {
                    // when action mode is not enable
                    // display toast
                    Toast.makeText(context, "You Clicked" + galleryList.get(viewHolder.getAdapterPosition()),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        // check condition
        if (isSelectAll) {
            // when value selected
            // visible all check boc image
            viewHolder.checkbox.setVisibility(View.VISIBLE);
            //set background color
            viewHolder.itemView.setBackgroundColor(Color.LTGRAY);
        } else {
            // when all value unselected
            // hide all check box image
            viewHolder.checkbox.setVisibility(View.GONE);
            // set background color
            viewHolder.itemView.setBackgroundColor(Color.TRANSPARENT);
        }
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

            image = (ImageView) view.findViewById(R.id.image);
        }
    }
}
