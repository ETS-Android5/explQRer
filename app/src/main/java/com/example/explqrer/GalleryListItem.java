package com.example.explqrer;

import android.graphics.Bitmap;

/**
 * Link: https://www.androidauthority.com/how-to-build-an-image-gallery-app-718976/
 * Author: Adam Sinicki
 * This class defines GalleryListItem objects they have images
 * imageId, is the ID of the actual image
 */
public class GalleryListItem {

    private Bitmap image;
    private String hashCode;

    /**
     * This gets the bitmap image of the image stored in the GalleryListItem object
     * @return imageId
     *      Which is the bitmap image stored in the GalleryListItem of an image
     */
    public Bitmap getImage(){
        return image;
    }

    /**
     * This sets the attribute image of the GalleryListItem object
     * @param bitmapImage
     *      Which is the URL of an image
     */
    public void setImage(Bitmap bitmapImage){
        this.image = bitmapImage;
    }

    /**
     * get the hashCode of the GalleryListItem object
     * @return hashCode
     */
    public String getHashCode(){
        return this.hashCode;
    }

    /**
     * sets the hashCode of the object to the param provided
     * @param hashCode
     *   the hash of the QR of the object
     */
    public void setHashCode(String hashCode){
        this.hashCode = hashCode;
    }


}
