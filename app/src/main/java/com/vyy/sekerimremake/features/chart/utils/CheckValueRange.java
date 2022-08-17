package com.vyy.sekerimremake.features.chart.utils;

import android.content.Context;
import android.content.res.Resources;

import com.vyy.sekerimremake.R;

public class CheckValueRange {

    // Private constructor to prevent instantiation
    private CheckValueRange() {
        throw new UnsupportedOperationException();
    }

    public static int checkValueRange(Context context, int i, int value) {
        Resources resources = context.getResources();
        int margin = resources.getInteger(R.integer.measurement_margin);
        if ((i == 0 || i == 2 || i == 4)) {
            if ((value <= resources
                    .getInteger(R.integer.low_measurement_value_empty)
                    || value >= resources.getInteger(R.integer.high_measurement_value_empty))) {
                return 2;
            } else if ((value <= resources
                    .getInteger(R.integer.low_measurement_value_empty) + margin
                    || value >= resources.getInteger(R.integer.high_measurement_value_empty) - margin)) {
                return 3;
            } else {
                return 1;
            }
        } else if ((i == 1 || i == 3 || i == 5)) {
            if ((value <= resources
                    .getInteger(R.integer.low_measurement_value_full)
                    || value >= resources.getInteger(R.integer.high_measurement_value_full))) {
                return 2;
            } else if ((value <= resources
                    .getInteger(R.integer.low_measurement_value_full) + margin
                    || value >= resources.getInteger(R.integer.high_measurement_value_full) - margin)) {
                return 3;
            } else {
                return 1;
            }
        } else {
            if ((value <= resources
                    .getInteger(R.integer.low_measurement_value_night)
                    || value >= resources.getInteger(R.integer.high_measurement_value_night))) {
                return 2;
            } else if ((value <= resources
                    .getInteger(R.integer.low_measurement_value_night) + margin
                    || value >= resources.getInteger(R.integer.high_measurement_value_night) - margin)) {
               return 3;
            } else {
               return 1;
            }
        }


    }
}
