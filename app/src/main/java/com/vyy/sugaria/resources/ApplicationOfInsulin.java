package com.vyy.sugaria.resources;

import com.vyy.sugaria.R;

public class ApplicationOfInsulin {

    // Private constructor to prevent instantiation
    private ApplicationOfInsulin() {
        throw new UnsupportedOperationException();
    }

    private static final int ONE = R.drawable.c2_1_1;
    private static final int TWO = R.drawable.c2_1_2;
    private static final int THREE = R.drawable.c2_1_3;
    private static final int FOUR = R.drawable.c2_1_4;
    private static final int FIVE = R.drawable.c2_1_5;
    private static final int SIX = R.drawable.c2_1_6;
    private static final int SEVEN = R.drawable.c2_1_7;
    private static final int EIGHT = R.drawable.c2_1_8;
    private static final int NINE = R.drawable.c2_1_9;
    private static final int TEN = R.drawable.c2_1_10;
    private static final int ELEVEN = R.drawable.c2_1_11;
    private static final int TWELVE = R.drawable.c2_1_12;
    private static final int THIRTEEN = R.drawable.c2_2_1;
    private static final int FOURTEEN = R.drawable.c2_2_2;
    private static final int FIFTEEN = R.drawable.c2_3_1;

    private static final int[] IMAGES = {ONE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, TEN,
            ELEVEN, TWELVE, THIRTEEN, FOURTEEN, FIFTEEN};

    private static final int[] tabPositions = {0, 12, 14};

    public static int[] getIMAGES() {
        return IMAGES;
    }

    public static int[] getCategoryPositions() {
        return tabPositions;
    }
}
