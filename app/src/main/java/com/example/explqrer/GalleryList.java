package com.example.explqrer;

import android.graphics.Bitmap;

import java.util.ArrayList;

/**
 * Link: https://www.androidauthority.com/how-to-build-an-image-gallery-app-718976/
 * Author: Adam Sinicki
 * populates the Gallery images list
 */
public class GalleryList {

    public static ArrayList<GalleryListItem> updateGallery(String playerName){

        //creating an object of type DataHandler to access the data base
        DataHandler dataHandler = new DataHandler();

        ArrayList<String> userQRs = dataHandler.userQrs(playerName);
        System.out.println("size of userQRs: "+ userQRs.size());
        //to store all the images the users has
        ArrayList<Bitmap> images = new ArrayList<>();
        //to store the points of each image
        ArrayList<Long> imagePoints = new ArrayList<>();
        //loops through the userQRs and populates images array list and image points with the same position
        for (int i = 0; i < userQRs.size(); i++){
            images.add(dataHandler.downloadImage(userQRs.get(i)));
            imagePoints.add(dataHandler.hashPts(userQRs.get(i)));
        }

        //loop through all the imagePoints and imageIds populate galleryListItem objects with the image's point value and the image
        ArrayList<GalleryListItem> listOfImages = new ArrayList<>();
        for( int i = 0; i < images.size();i++){
            GalleryListItem galleryListItem = new GalleryListItem();
            galleryListItem.setImageId(images.get(i));
            galleryListItem.setImagePoints(imagePoints.get(i));
            listOfImages.add(galleryListItem);
        }

        return listOfImages;
    }
}
