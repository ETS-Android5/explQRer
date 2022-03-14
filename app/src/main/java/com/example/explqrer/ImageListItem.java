package com.example.explqrer;

import java.util.ArrayList;

/**
 * Link: https://www.androidauthority.com/how-to-build-an-image-gallery-app-718976/
 * Author: Adam Sinicki
 * This class defines ImageListItem objects they have imagePoints and imageId
 * imagePoints, is the points value of the image
 * imageId, is the ID of the actual image
 */
public class ImageListItem {

    private Integer imagePoints;
    private Integer imageId;

    /**
     * This gets the points of the ImageListItem object
     * @return integer imagePoints
     *      Which is the points of the image
     */
    public Integer getImagePoints(){
        return imagePoints;
    }

    /**
     * This adds points of and image to the ImageListItem object
     * @param points
     *      this is the points of the image to add
     */
    public void setImagePoints(Integer points){
        this.imagePoints = points;
    }

    /**
     * This gets the ID of the image stored in the ImageListItem object
     * @return imageId
     *      Which is the ID stored in the ImageListItem of an image
     */
    public Integer getImageId(){
        return imageId;
    }

    /**
     * This sets the image ID into the ImageListItem object
     * @param imageUrl
     *      Which is the URL of an image
     */
    public void setImageId(Integer imageUrl){
        this.imageId = imageUrl;
    }
}
