package com.vyy.sekerimremake.features.catalog.data.resources;

import com.vyy.sekerimremake.R;

public class OralAntidiabeticDrugs {
    // Private constructor to prevent instantiation
    private OralAntidiabeticDrugs() {
        throw new UnsupportedOperationException();
    }
    private final static int FIRST = R.drawable.c4_1;
    private final static int SECOND = R.drawable.c4_2;

    private static int[] IMAGES = {FIRST, SECOND};

    public static int[] getIMAGES() {
        return IMAGES;
    }
}
