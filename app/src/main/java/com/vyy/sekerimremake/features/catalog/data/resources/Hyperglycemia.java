package com.vyy.sekerimremake.features.catalog.data.resources;

import com.vyy.sekerimremake.R;

public class Hyperglycemia {
    // Private constructor to prevent instantiation
    private Hyperglycemia() {
        throw new UnsupportedOperationException();
    }
    private final static int FIRST = R.drawable.c7_1;
    private final static int SECOND = R.drawable.c7_2;
    private final static int THIRD = R.drawable.c7_3;
    private final static int FOUR = R.drawable.c7_4;
    private final static int FIVE = R.drawable.c7_5;
    private final static int SIX = R.drawable.c7_6;
    private final static int SEVEN = R.drawable.c7_7;
    private final static int EIGHT = R.drawable.c7_8;
    private final static int NINE = R.drawable.c7_9;

    private static int[] IMAGES = {FIRST, SECOND, THIRD, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE};

    public static int[] getIMAGES() {
        return IMAGES;
    }
}
