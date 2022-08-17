package com.vyy.sekerimremake.features.catalog.data.resources;

import com.vyy.sekerimremake.R;

public class Chiropody {
    // Private constructor to prevent instantiation
    private Chiropody() {
        throw new UnsupportedOperationException();
    }
    private static int ONE = R.drawable.c8_1;
    private static int TWO = R.drawable.c8_2;
    private static int THREE = R.drawable.c8_3;
    private static int FOUR = R.drawable.c8_4;
    private static int FIVE = R.drawable.c8_5;
    private static int SIX = R.drawable.c8_6;
    private static int SEVEN = R.drawable.c8_7;
    private static int EIGHT = R.drawable.c8_8;
    private static int NINE = R.drawable.c8_9;
    private static int TEN = R.drawable.c8_10;

    private static int[] IMAGES = {ONE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, TEN};

    public static int[] getIMAGES() {
        return IMAGES;
    }

}
