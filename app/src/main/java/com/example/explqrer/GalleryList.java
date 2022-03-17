package com.example.explqrer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 * Link: https://www.androidauthority.com/how-to-build-an-image-gallery-app-718976/
 * Author: Adam Sinicki
 * populates the Gallery images list
 */
public class GalleryList extends AppCompatActivity {

    public static ArrayList<GalleryListItem> updateGallery(PlayerProfile player){

        //creating an object of type DataHandler to access the data base
        DataHandler dataHandler = new DataHandler();
//
//        ArrayList<String> userQRs = dataHandler.userQrs(playerName);
        HashMap<GameCode,GameCode> qrCodes = player.getCodes();
        Set<GameCode> qrCodesSet = qrCodes.keySet();
        System.out.println("user list: "+ qrCodesSet);
//        System.out.println("size of userQRs: "+ qrCodes.size());
        //to store all the images the users has
        ArrayList<Bitmap> images = new ArrayList<>();
        //to store the points of each image
        ArrayList<Long> imagePoints = new ArrayList<>();
        //loops through the userQRs and populates images array list and image points with the same position

        //loop through all the imagePoints and imageIds populate galleryListItem objects with the image's point value and the image
        ArrayList<GalleryListItem> listOfImages = new ArrayList<>();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        for(GameCode qr : qrCodesSet ){
            File img = dataHandler.downloadImage(qr.getSha256hex());
            GalleryListItem galleryListItem = new GalleryListItem();
            System.out.println("path: "+ img.getAbsolutePath());

            galleryListItem.setImageId(img.getAbsolutePath());
            listOfImages.add(galleryListItem);
        }
        System.out.println("before listof images");
        return listOfImages;
    }
}