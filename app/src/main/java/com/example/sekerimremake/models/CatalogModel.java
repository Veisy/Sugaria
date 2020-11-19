package com.example.sekerimremake.models;

public class CatalogModel {
    private final String title;
    private final int image;

    public CatalogModel(String title, int image) {
        this.title = title;
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public int getImage() {
        return image;
    }
}
