package com.example.sekerimremake.resources;

import com.example.sekerimremake.R;

public class MeasurementItems {
    // Private constructor to prevent instantiation
    private MeasurementItems() {
        throw new UnsupportedOperationException();
    }
    private final static int FIRST = R.drawable.c1_1;
    private final static int SECOND = R.drawable.c1_2;
    private final static int THIRD = R.drawable.c1_3;

    private static int[] IMAGES = {FIRST, SECOND, THIRD};

    public static int[] getIMAGES() {
        return IMAGES;
    }

}
