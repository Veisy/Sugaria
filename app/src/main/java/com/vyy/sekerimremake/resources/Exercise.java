package com.vyy.sekerimremake.resources;

import com.vyy.sekerimremake.R;

public class Exercise {
    // Private constructor to prevent instantiation
    private Exercise() {
        throw new UnsupportedOperationException();
    }
    private static int ONE = R.drawable.c5_1;
    private static int TWO = R.drawable.c5_2;
    private static int THREE = R.drawable.c5_3;
    private static int FOUR = R.drawable.c5_4;
    private static int FIVE = R.drawable.c5_5;
    private static int SIX = R.drawable.c5_6;
    private static int SEVEN = R.drawable.c5_7;

    private static int[] IMAGES = {ONE, TWO, THREE, FOUR, FIVE, SIX, SEVEN};

    public static int[] getIMAGES() {
        return IMAGES;
    }
}
