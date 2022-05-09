package com.vyy.sugaria.filters;

import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;

public class InputFilterMax implements InputFilter {
    private static final String TAG = "InputFilterMax";
    private final int max;

    public InputFilterMax(int max) {
        this.max = max;
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        try {
            String replacement = source.subSequence(start, end).toString();
            String newVal = dest.toString().substring(0, dstart) + replacement +dest.toString().substring(dend);
            int input = Integer.parseInt(newVal);
            if (input<=max)
                return null;
        } catch (NumberFormatException nfe) {
            Log.d(TAG, "filter: " + nfe);
        }
        return "";
    }
}
