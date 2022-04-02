package com.example.explqrer;

import android.content.Context;
import android.graphics.Bitmap;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

public class GalleryBuilder {
    /**
     * This populates the gallery with a GalleryAdapter
     *  Link: https://www.androidauthority.com/how-to-build-an-image-gallery-app-718976/
     *  Author: Adam Sinicki
     */
    public static void populateGallery(PlayerProfile player, RecyclerView galleryRecyclerView,
                                       Context context, GameCodeFragment.GameCodeFragmentHost host){

        //set up the RecyclerView for the gallery
        galleryRecyclerView.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context,4);
        galleryRecyclerView.setLayoutManager(gridLayoutManager);

        ArrayList<GalleryListItem> listOfImages = updateGallery(player);

        GalleryAdapter galleryListAdapter = new GalleryAdapter(context, listOfImages, host);
        galleryRecyclerView.setAdapter(galleryListAdapter);
    }

    public static ArrayList<GalleryListItem> updateGallery(PlayerProfile player){

        HashMap<String,GameCode> codes = player.getCodes();
        Collection<GameCode> qrCodes = codes.values();

        //loop through all the imagePoints and imageIds populate galleryListItem objects with the image's point value and the image
        ArrayList<GalleryListItem> listOfImages = new ArrayList<>();
        //loop through the QRs scanned by the player
        for(GameCode qr : qrCodes ){
            GalleryListItem galleryListItem = new GalleryListItem();
            //get image of the qr scanned and set the images
            galleryListItem.setImage(qr.getPhoto());
            galleryListItem.setHashCode(qr.getSha256hex());
            listOfImages.add(galleryListItem);
        }
        return listOfImages;
    }

}
