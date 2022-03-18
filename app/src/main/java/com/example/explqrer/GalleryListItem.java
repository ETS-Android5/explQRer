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

    /**
     * This gets the ID of the image stored in the ImageListItem object
     * @return imageId
     *      Which is the ID stored in the ImageListItem of an image
     */
    public Bitmap getImage(){
        return image;
    }

    /**
     * This sets the image ID into the ImageListItem object
     * @param bitmapImage
     *      Which is the URL of an image
     */
    public void setImageId(Bitmap bitmapImage){
        this.image = bitmapImage;
    }
}
