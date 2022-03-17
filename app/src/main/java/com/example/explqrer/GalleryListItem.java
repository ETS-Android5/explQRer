package com.example.explqrer;

import android.graphics.Bitmap;

import java.util.ArrayList;

/**
 * Link: https://www.androidauthority.com/how-to-build-an-image-gallery-app-718976/
 * Author: Adam Sinicki
 * This class defines ImageListItem objects they have imagePoints and imageId
 * imagePoints, is the points value of the image
 * imageId, is the ID of the actual image
 */
public class GalleryListItem {

    private Long imagePoints;
    private String image;

    /**
     * This gets the points of the ImageListItem object
     * @return integer imagePoints
     *      Which is the points of the image
     */
    public Long getImagePoints(){
        return imagePoints;
    }

    /**
     * This adds points of and image to the ImageListItem object
     * @param points
     *      this is the points of the image to add
     */
    public void setImagePoints(Long points){
        this.imagePoints = points;
    }

    /**
     * This gets the ID of the image stored in the ImageListItem object
     * @return imageId
     *      Which is the ID stored in the ImageListItem of an image
     */
    public String getImage(){
        return image;
    }

    /**
     * This sets the image ID into the ImageListItem object
     * @param imagePath
     *      Which is the URL of an image
     */
    public void setImageId(String imagePath){
        this.image = imagePath;
    }
}
