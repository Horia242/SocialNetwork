package com.example.ex2;

import javafx.scene.image.Image;

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
