package com.example.explqrer;

import android.content.Context;
import android.graphics.Bitmap;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class GalleryBuilder {
    /**
     * This populates the gallery with a GalleryAdapter
     *  Link: https://www.androidauthority.com/how-to-build-an-image-gallery-app-718976/
     *  Author: Adam Sinicki
     */
    public static void populateGallery(PlayerProfile player, RecyclerView galleryRecyclerView, Context context){

        //set up the RecyclerView for the gallery
        galleryRecyclerView.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context,4);
        galleryRecyclerView.setLayoutManager(gridLayoutManager);

        ArrayList<GalleryListItem> listOfImages = updateGallery(player);

        GalleryAdapter galleryListAdapter = new GalleryAdapter(context,listOfImages);
        System.out.println("before adapter");
        galleryRecyclerView.setAdapter(galleryListAdapter);
    }

    public static ArrayList<GalleryListItem> updateGallery(PlayerProfile player){

        HashMap<GameCode,GameCode> qrCodes = player.getCodes();
        Set<GameCode> qrCodesSet = qrCodes.keySet();
        System.out.println("user list: "+ qrCodesSet);

        //loop through all the imagePoints and imageIds populate galleryListItem objects with the image's point value and the image
        ArrayList<GalleryListItem> listOfImages = new ArrayList<>();
        //loop through the QRs scanned by the player
        for(GameCode qr : qrCodesSet ){
            GalleryListItem galleryListItem = new GalleryListItem();
            //get image of the qr scanned and set the images
            galleryListItem.setImageId(qr.getPhoto());
            listOfImages.add(galleryListItem);
        }
        System.out.println("before listof images");
        return listOfImages;
    }

    public void generateFragment(String codeHash, Bitmap codeImage, int codePts, String codeDescription) {
        GameCodeFragment gameCodeFragment = GameCodeFragment.newInstance(codeHash,codeImage,codePts,codeDescription);
        gameCodeFragment.show(getSupportFragmentManager(),"GAMECODE");
    }
}
