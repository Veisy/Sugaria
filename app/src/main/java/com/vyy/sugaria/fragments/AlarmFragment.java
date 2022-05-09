package com.vyy.sugaria.fragments;

import android.app.TimePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.AlarmClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TimePicker;
import android.widget.Toast;

import com.vyy.sugaria.databinding.FragmentAlarmBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;


public class AlarmFragment extends Fragment {

    private FragmentAlarmBinding binding;
    private Context mContext;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAlarmBinding.inflate(inflater, container, false);
        // Inflate the layout for this fragment
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.floatingActionButtonAddAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ArrayList<Integer> daysOfWeek = new ArrayList<>(Arrays
                        .asList(Calendar.SUNDAY, Calendar.MONDAY,
                                Calendar.TUESDAY, Calendar.WEDNESDAY,
                                Calendar.THURSDAY,Calendar.FRIDAY,Calendar.SATURDAY));
                setAlarm(daysOfWeek);
            }
        });
    }

    private void setAlarm(final ArrayList<Integer> daysOfWeek) {
        Calendar calendar = Calendar.getInstance();
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        int currentMinute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(mContext,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        Intent intent = new Intent(AlarmClock.ACTION_SET_ALARM);
                        intent.putExtra(AlarmClock.EXTRA_MESSAGE, "Kan şekeri alarm");
                        intent.putExtra(AlarmClock.EXTRA_HOUR, hourOfDay);
                        intent.putExtra(AlarmClock.EXTRA_MINUTES, minute);
                        intent.putExtra(AlarmClock.EXTRA_DAYS, daysOfWeek);
                        intent.putExtra(AlarmClock.EXTRA_SKIP_UI, true);
                        try {
                            mContext.startActivity(intent);
                        } catch (ActivityNotFoundException e) {
                            Toast.makeText(mContext, "Alarm uygulaması bulunamadı.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, currentHour, currentMinute, true);
        timePickerDialog.show();
    }
}