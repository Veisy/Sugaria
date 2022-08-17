package com.vyy.sekerimremake.features.catalog.domain.model;

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
