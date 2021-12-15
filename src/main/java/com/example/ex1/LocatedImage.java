package com.example.ex1;

import javafx.scene.image.Image;

/**
 * Wrapper class for Image
 * LocatedImage objects are created only if you want to store an Image object image path
 */
public class LocatedImage extends Image {
    private final String path;

    public LocatedImage(String path){
        super(path);
        this.path = path;
    }

    public String getPath(){
        return path;
    }

}
