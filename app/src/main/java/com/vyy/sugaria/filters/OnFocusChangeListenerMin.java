package com.vyy.sugaria.filters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.vyy.sugaria.R;

public class OnFocusChangeListenerMin implements View.OnFocusChangeListener {
    private final Context context;
    private final int min, low, high;

    public OnFocusChangeListenerMin(Context context, int min, int low, int high) {
        this.context = context;
        this.min = min;
        this.low = low;
        this.high = high;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if(!hasFocus) {
            String valString = ((EditText)v).getText().toString();
            if(!TextUtils.isEmpty(valString)){
                int valueInt = Integer.parseInt(valString);
                Resources resources = context.getResources();
                if(valueInt <= min){
                    ((EditText) v).setText("");
                    Toast.makeText(context, resources.getString(R.string.invalid_input), Toast.LENGTH_SHORT).show();
                } else {
                    if (valueInt <= low || valueInt >= high) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        String title = resources.getString(R.string.warning);
                        String message;
                        if (valueInt <= low) {
                            title = title + resources.getString(R.string.low_glucose_warning_title);
                            message = resources.getString(R.string.low_glucose_warning);
                        } else {
                            title = title + resources.getString(R.string.high_glucose_warning_title);
                            message = resources.getString(R.string.high_glucose_warning);
                        }
                        builder.setTitle(title).setMessage(message);
                        builder.setNeutralButton(R.string.okay, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                }

            }
        }
    }
}
