package com.vyy.sugaria.fragments;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.SharedElementCallback;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.transition.Transition;
import androidx.transition.TransitionInflater;

import android.text.InputFilter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.vyy.sugaria.R;
import com.vyy.sugaria.database.ChartDbHelper;
import com.vyy.sugaria.databinding.ChartEditTableBinding;
import com.vyy.sugaria.databinding.ChartSingleDayRowBinding;
import com.vyy.sugaria.databinding.FragmentChartDetailBinding;
import com.vyy.sugaria.filters.InputFilterMax;
import com.vyy.sugaria.filters.OnFocusChangeListenerMin;
import com.vyy.sugaria.models.ChartRowModel;
import com.vyy.sugaria.utils.CheckValueRange;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class ChartDetailFragment extends Fragment implements View.OnClickListener {

    public final static String[] STATE_KEYS = {"MORNING_EMPTY_STATE", "MORNING_FULL_STATE",
            "AFTERNOON_EMPTY_STATE", "AFTERNOON_FULL_STATE", "EVENING_EMPTY_STATE",
            "EVENING_FULL_STATE", "NIGHT_STATE"};

    private FragmentChartDetailBinding binding;
    private ChartEditTableBinding includedBinding;

    private Context mContext;
    private EditText[] editTexts;
    private ImageButton[] editImageButtons;
    private ChartRowModel newRow;
    private List<ChartRowModel> chartRows;

    private boolean addConfigurationFlag;
    private int position;

    public ChartDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        chartRows = new ChartDbHelper(mContext).getEveryone();
        final Bundle arguments = getArguments();
        if (savedInstanceState != null) {
            addConfigurationFlag = savedInstanceState.getBoolean("BUTTON_FLAG_SAVED");
            if (!addConfigurationFlag && chartRows != null) {
                position = savedInstanceState.getInt("POSITION_SAVED");
                newRow = chartRows.get(position);
            } else {
                checkTheDay(savedInstanceState.getInt("DAY_SAVED"),
                        savedInstanceState.getInt("MONTH_SAVED"),
                        savedInstanceState.getInt("YEAR_SAVED"));
            }
        } else {
            if (arguments != null) {
                addConfigurationFlag = arguments.getBoolean("button_flag", true);
                if (!addConfigurationFlag && chartRows != null) {
                    position = arguments.getInt("position", 0);
                    newRow = chartRows.get(position);
                } else {
                    Calendar calendar = Calendar.getInstance();
                    checkTheDay(calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.YEAR));
                }
            }
        }
    }

    @Override
    public View onCreateView (@NonNull LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState){
        binding = FragmentChartDetailBinding.inflate(inflater, container, false);
        includedBinding = binding.includedChartEditTable;
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bindToViews();
        formatLayout(addConfigurationFlag);
        fillTextViews();
        fillSingleRowHeader();
        setListeners();

        //This code block for a particular situation when an EditText input edited to blank and
        //device change orientation. In this situation, because the view inputs have not updated yet,
        //EditTexts and edit buttons will carry the pre edited state. We handle this bug here.
        if (savedInstanceState != null) {
            for (int i = 0; i < STATE_KEYS.length; i++) {
                setViewsState(savedInstanceState.getBoolean(STATE_KEYS[i]), editTexts[i],
                        editImageButtons[i]);
            }
        }
    }

    private void setViewsState(boolean isEnabled, EditText editText, ImageButton imageButton) {
        if (isEnabled) {
            editText.setEnabled(true);
            editText.setBackgroundResource(R.drawable.edit_text_measurement);
            imageButton.setVisibility(View.GONE);
        } else {
            editText.setEnabled(false);
            editText.setBackgroundResource(R.drawable.edit_text_measurement_passive);
            imageButton.setVisibility(View.VISIBLE);
        }
    }

    private void bindToViews() {
        editTexts = new EditText[]{includedBinding.editTextNumberMorningEmpty,
                includedBinding.editTextNumberMorningFull, includedBinding.editTextNumberAfternoonEmpty,
                includedBinding.editTextNumberAfternoonFull, includedBinding.editTextNumberEveningEmpty,
                includedBinding.editTextNumberEveningFull, includedBinding.editTextNumberNight};

        editImageButtons = new ImageButton[]{includedBinding.buttonMorningEmptyEdit,
                includedBinding.buttonMorningFullEdit, includedBinding.buttonAfternoonEmptyEdit,
                includedBinding.buttonAfternoonFullEdit, includedBinding.buttonEveningEmptyEdit,
                includedBinding.buttonEveningFullEdit, includedBinding.buttonNightEdit};
    }

    private void setListeners() {
        final View[] BUTTONS = new View[]{includedBinding.buttonAddRow, includedBinding.buttonUpdate,
                binding.imageButtonCalendar, includedBinding.buttonMorningEmptyEdit,
                includedBinding.buttonMorningFullEdit,  includedBinding.buttonAfternoonEmptyEdit,
                includedBinding.buttonAfternoonFullEdit,  includedBinding.buttonEveningEmptyEdit,
                includedBinding.buttonEveningFullEdit, includedBinding.buttonNightEdit};
        for (View v : BUTTONS) {
            v.setOnClickListener(this);
        }

        InputFilter[] inputFilters = new InputFilter[]{new InputFilterMax(500)};
        OnFocusChangeListenerMin onFocusChangeListenerMin;
        Resources resources = getResources();
        int minValue = resources.getInteger(R.integer.min_measurement_value);
        for (int i = 0; i < editTexts.length; i++) {
            editTexts[i].setFilters(inputFilters);
            if (i == 0 || i == 2 || i == 4) {
                onFocusChangeListenerMin = new OnFocusChangeListenerMin(mContext,
                        minValue,
                        resources.getInteger(R.integer.low_measurement_value_empty),
                        resources.getInteger(R.integer.high_measurement_value_empty));
            } else if (i == 1 || i == 3 || i == 5) {
                onFocusChangeListenerMin = new OnFocusChangeListenerMin(mContext,
                        minValue,
                        resources.getInteger(R.integer.low_measurement_value_full),
                        resources.getInteger(R.integer.high_measurement_value_full));
            } else {
                onFocusChangeListenerMin = new OnFocusChangeListenerMin(mContext,
                        minValue,
                        resources.getInteger(R.integer.low_measurement_value_night),
                        resources.getInteger(R.integer.high_measurement_value_night));
            }
            editTexts[i].setOnFocusChangeListener(onFocusChangeListenerMin);
        }
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.button_addRow || viewId == R.id.button_update) {
            lastCheckValues();
            startParent(viewId);
        } else if (viewId == R.id.imageButton_calendar) {
            showDatePickerDialog();
        } else if (viewId == R.id.button_morningEmpty_edit) {
            editTextStateChange(0, includedBinding.editTextNumberMorningEmpty, includedBinding.buttonMorningEmptyEdit);
        } else if (viewId == R.id.button_morningFull_edit) {
            editTextStateChange(0, includedBinding.editTextNumberMorningFull, includedBinding.buttonMorningFullEdit);
        } else if (viewId == R.id.button_afternoonEmpty_edit) {
            editTextStateChange(0, includedBinding.editTextNumberAfternoonEmpty, includedBinding.buttonAfternoonEmptyEdit);
        } else if (viewId == R.id.button_afternoonFull_edit) {
            editTextStateChange(0, includedBinding.editTextNumberAfternoonFull, includedBinding.buttonAfternoonFullEdit);
        } else if (viewId == R.id.button_eveningEmpty_edit) {
            editTextStateChange(0, includedBinding.editTextNumberEveningEmpty, includedBinding.buttonEveningEmptyEdit);
        } else if (viewId == R.id.button_eveningFull_edit) {
            editTextStateChange(0,includedBinding.editTextNumberEveningFull, includedBinding.buttonEveningFullEdit);
        } else if (viewId == R.id.button_night_edit) {
            editTextStateChange(0, includedBinding.editTextNumberNight, includedBinding.buttonNightEdit);
        }
    }

    //Check all values while exiting this ChartDetailFragment if there is any value outside our range.
    private void lastCheckValues() {
        for (EditText e : editTexts) {
            if(!TextUtils.isEmpty(e.getText())) {
                int value = Integer.parseInt(e.getText().toString());
                if (value < getResources().getInteger(R.integer.min_measurement_value)
                        || value > getResources().getInteger(R.integer.max_measurement_value)) {
                    e.setText("");
                }
            }
        }
    }

    private void startParent(final int buttonId) {
        boolean bool;
        if (buttonId == R.id.button_addRow) {
            bool = addToDataBase();
        } else {
            bool = updateDataBase();
        }
        if (bool)
           requireActivity().onBackPressed();
    }

    //Configure addition layout.
    private void formatLayout(Boolean configureFlag) {
        int visibilityFlag1;
        int visibilityFlag2;
        if (configureFlag) {
            visibilityFlag1 = View.VISIBLE;
            visibilityFlag2 = View.GONE;
        } else {
            visibilityFlag1 = View.GONE;
            visibilityFlag2 = View.VISIBLE;
        }
        // addition view.
        includedBinding.buttonAddRow.setVisibility(visibilityFlag1);
        // update view.
        includedBinding.buttonUpdate.setVisibility(visibilityFlag2);
    }

    private void checkTheDay(int dayOfMonth, int month, int year) {
        boolean rowExist = false;
        for (int i = 0; i < chartRows.size(); i++) {
            if (chartRows.get(i).getDayOfMonth() == dayOfMonth && chartRows.get(i).getMonth() == month
                    && chartRows.get(i).getYear() == year) {
                addConfigurationFlag = false;
                position = i;
                newRow = chartRows.get(i);
                rowExist = true;
                break;
            }
        }
        if (!rowExist) {
            addConfigurationFlag = true;
            newRow = new ChartRowModel();
            newRow.setRowId(-1);
            newRow.setDayOfMonth(dayOfMonth);
            newRow.setMonth(month);
            newRow.setYear(year);
        }
    }

    private void setCatalogRowModel() {
        newRow.setMorningEmpty(parseIntStringView(includedBinding.editTextNumberMorningEmpty));
        newRow.setMorningFull(parseIntStringView(includedBinding.editTextNumberMorningFull));
        newRow.setAfternoonEmpty(parseIntStringView(includedBinding.editTextNumberAfternoonEmpty));
        newRow.setAfternoonFull(parseIntStringView(includedBinding.editTextNumberAfternoonFull));
        newRow.setEveningEmpty(parseIntStringView(includedBinding.editTextNumberEveningEmpty));
        newRow.setEveningFull(parseIntStringView(includedBinding.editTextNumberEveningFull));
        newRow.setNight(parseIntStringView(includedBinding.editTextNumberNight));
    }

    private int parseIntStringView(EditText editText) {
        return editText.getText().toString().isEmpty() ? 0 : Integer.parseInt(editText.getText().toString());
    }

    private void fillSingleRowHeader() {
        ChartSingleDayRowBinding singleDayRowBinding = binding.includedSingleDayRow;
        ViewCompat.setTransitionName(singleDayRowBinding.singleDay, String.valueOf(newRow.getRowId()));
        Transition transition = TransitionInflater.from(mContext)
                .inflateTransition(R.transition.shared_image);
        setSharedElementEnterTransition(transition);
        setEnterSharedElementCallback(new SharedElementCallback() {
            @Override
            public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                super.onMapSharedElements(names, sharedElements);
                sharedElements.put(binding.includedSingleDayRow.singleDay.getTransitionName(),
                        binding.includedSingleDayRow.singleDay);
            }
        });

        TextView[] headerTextViews = new TextView[]{singleDayRowBinding.textViewDayMorningEmpty,
                singleDayRowBinding.textViewDayMorningFull, singleDayRowBinding.textViewDayAfternoonEmpty,
                singleDayRowBinding.textViewDayAfternoonFull, singleDayRowBinding.textViewDayEveningEmpty,
                singleDayRowBinding.textViewDayEveningFull, singleDayRowBinding.textViewDayNight};
        if (newRow != null) {
            singleDayRowBinding.textViewDayDay.setText(String.valueOf(newRow.getDayOfMonth()));
            String monthAndYearTogether = (getResources()
                    .getStringArray(R.array.Months))[newRow.getMonth()] + "\n" + newRow.getYear();
            singleDayRowBinding.textViewDayMonthYear.setText(monthAndYearTogether);

            final int[] newRowFields = {newRow.getMorningEmpty(), newRow.getMorningFull(),
                    newRow.getAfternoonEmpty(), newRow.getAfternoonFull(), newRow.getEveningEmpty(),
                    newRow.getEveningFull(), newRow.getNight()};

            //Chance background according to value range.
            int warningFlag;
            for(int i = 0; i < newRowFields.length; i++) {
                if (newRowFields[i] == 0) {
                    headerTextViews[i].setText("");
                    headerTextViews[i].setBackgroundResource(R.drawable.text_frame);
                } else {
                    headerTextViews[i].setText(String.valueOf(newRowFields[i]));
                    warningFlag = CheckValueRange.checkValueRange(mContext, i, newRowFields[i]);
                    if (warningFlag == 2){
                        headerTextViews[i].setBackgroundResource(R.drawable.text_frame_warning_range);
                    } else if (warningFlag == 3){
                        headerTextViews[i].setBackgroundResource(R.drawable.text_frame_margin_range);
                    } else {
                        headerTextViews[i].setBackgroundResource(R.drawable.text_frame_normal_range);
                    }
                }
            }
        }
    }

    private void fillTextViews() {
        final int[] newRowFields = {newRow.getMorningEmpty(), newRow.getMorningFull(),
                newRow.getAfternoonEmpty(), newRow.getAfternoonFull(), newRow.getEveningEmpty(),
                newRow.getEveningFull(), newRow.getNight()};

        for (int i = 0; i < newRowFields.length; i++) {
            editTextStateChange(newRowFields[i], editTexts[i], editImageButtons[i]);
        }
    }

    private void editTextStateChange(int value, EditText editText, ImageButton imageButton) {
        boolean b = (value == 0);
        setViewsState(b, editText, imageButton);
        if (!b) {
            editText.setText(String.valueOf(value));
        }
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(mContext,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        cleanTextViews();
                        checkTheDay(dayOfMonth, month, year);
                        formatLayout(addConfigurationFlag);
                        fillTextViews();
                        fillSingleRowHeader();
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private boolean addToDataBase() {
        setCatalogRowModel();
        try {
            if (newRow.getDayOfMonth() == 0 || newRow.getYear() == 0) {
                Toast.makeText(mContext, "Bir tarih seçin.", Toast.LENGTH_SHORT).show();
                return false;
            } else {

                if (includedBinding.editTextNumberMorningEmpty.getText().toString().isEmpty() &&
                        includedBinding.editTextNumberMorningFull.getText().toString().isEmpty()
                        && includedBinding.editTextNumberAfternoonEmpty.getText().toString().isEmpty()
                        && includedBinding.editTextNumberAfternoonFull.getText().toString().isEmpty()
                        && includedBinding.editTextNumberEveningEmpty.getText().toString().isEmpty()
                        && includedBinding.editTextNumberEveningFull.getText().toString().isEmpty()
                        && includedBinding.editTextNumberNight.getText().toString().isEmpty()) {
                    Toast.makeText(mContext, "Bir ölçüm girin.", Toast.LENGTH_SHORT).show();
                    return false;
                } else {
                    ChartDbHelper chartDbHelper = new ChartDbHelper(mContext);
                    return chartDbHelper.addOne(newRow);
                }
            }
        } catch (Exception e) {
            Toast.makeText(mContext, "Error adding data to database!", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private boolean updateDataBase() {
        setCatalogRowModel();
        ChartDbHelper chartDbHelper = new ChartDbHelper(mContext);
        if (newRow.getMorningEmpty() == 0 && newRow.getMorningFull() == 0
                && newRow.getAfternoonEmpty() == 0 && newRow.getAfternoonFull() == 0
                && newRow.getEveningEmpty() == 0 && newRow.getEveningFull() == 0
                && newRow.getNight() == 0) {
            return chartDbHelper.deleteOne(newRow);
        } else {
            return chartDbHelper.updateData(newRow);
        }
    }

    private void cleanTextViews() {
        for (EditText e : editTexts) {
            e.setText("");
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("BUTTON_FLAG_SAVED", addConfigurationFlag);
        if (!addConfigurationFlag) {
            outState.putInt("POSITION_SAVED", position);
        } else {
            outState.putInt("DAY_SAVED", newRow.getDayOfMonth());
            outState.putInt("MONTH_SAVED", newRow.getMonth());
            outState.putInt("YEAR_SAVED", newRow.getYear());
        }

        for (int i = 0; i < STATE_KEYS.length; i++) {
            outState.putBoolean(STATE_KEYS[i], editTexts[i].isEnabled());
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        chartRows = null;
        newRow = null;
        binding = null;
        includedBinding = null;
    }
}

