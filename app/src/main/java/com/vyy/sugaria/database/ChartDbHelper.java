package com.vyy.sugaria.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.vyy.sugaria.models.ChartRowModel;

import java.util.ArrayList;
import java.util.List;

public class ChartDbHelper extends SQLiteOpenHelper {

    private static final String TAG = "ChartDbHelper";

    private static final String DATABASE_NAME = "BloodGlucoseChart.db";
    private static final int DATABASE_VERSION = 1;
    private static final String CHART_TABLE = "CHART_TABLE";
    private static final String ROW_ID = "ROW_ID";
    private static final String DATE_DAY = "DATE_DAY";
    private static final String DATE_MONTH = "DATE_MONTH";
    private static final String DATE_YEAR = "DATE_YEAR";
    private static final String MORNING_EMPTY = "MORNING_EMPTY";
    private static final String MORNING_FULL = "MORNING_FULL";
    private static final String AFTERNOON_EMPTY = "AFTERNOON_EMPTY";
    private static final String AFTERNOON_FULL = "AFTERNOON_FULL";
    private static final String EVENING_EMPTY = "EVENING_EMPTY";
    private static final String EVENING_FULL = "EVENING_FULL";
    private static final String NIGHT = "NIGHT";

    public ChartDbHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableStatement = "CREATE TABLE "+CHART_TABLE+ " (" +ROW_ID+
                " INTEGER PRIMARY KEY AUTOINCREMENT, " +DATE_DAY+ " INTEGER, " +DATE_MONTH+
                " INTEGER, " +DATE_YEAR+ " INTEGER, " +MORNING_EMPTY+ " INTEGER, " +MORNING_FULL+
                " INTEGER, " +AFTERNOON_EMPTY+ " INTEGER, " +AFTERNOON_FULL+ " INTEGER, " +
                "" +EVENING_EMPTY+ " INTEGER, " +EVENING_FULL+ " INTEGER, " +NIGHT+ " INTEGER)";
        db.execSQL(createTableStatement);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + CHART_TABLE);
        onCreate(db);
    }

    public boolean addOne(ChartRowModel chartRowModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = putContentValues(chartRowModel);

        final long insert = db.insert(CHART_TABLE, null, cv);
        db.close();
        return insert != -1;
    }

    public boolean deleteOne(ChartRowModel chartRowModel) {
        // find customerModel in the database. If it is found , delete it and return true.
        // if it is not found, return false.
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        final int delete = sqLiteDatabase.delete(CHART_TABLE, "ROW_ID=" + chartRowModel.getRowId(), null);

        return delete != -1;
    }

    public boolean updateData(ChartRowModel chartRowModel){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = putContentValues(chartRowModel);

        long result = db.update(CHART_TABLE, cv, "ROW_ID="+chartRowModel.getRowId(), null);
        return result != -1;

    }

    private ContentValues putContentValues(ChartRowModel chartRowModel) {
        ContentValues cv = new ContentValues();
        cv.put(DATE_DAY, chartRowModel.getDayOfMonth());
        cv.put(DATE_MONTH, chartRowModel.getMonth());
        cv.put(DATE_YEAR, chartRowModel.getYear());
        cv.put(MORNING_EMPTY, chartRowModel.getMorningEmpty());
        cv.put(MORNING_FULL, chartRowModel.getMorningFull());
        cv.put(AFTERNOON_EMPTY, chartRowModel.getAfternoonEmpty());
        cv.put(AFTERNOON_FULL, chartRowModel.getAfternoonFull());
        cv.put(EVENING_EMPTY, chartRowModel.getEveningEmpty());
        cv.put(EVENING_FULL, chartRowModel.getEveningFull());
        cv.put(NIGHT, chartRowModel.getNight());

        return cv;
    }

    public List<ChartRowModel> getEveryone() {
        List<ChartRowModel> returnList = new ArrayList<>();
        String queryString = "SELECT * FROM " +CHART_TABLE+ " ORDER BY " +DATE_YEAR+ " ASC, "
                +DATE_MONTH+ " ASC, " +DATE_DAY+ " ASC";
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();

        Cursor cursor = sqLiteDatabase.rawQuery(queryString, null);
        if (cursor.moveToFirst()) {
            //loop through the cursor(result set) and create new customer objects. Put them into the return list.
            do {
                int rowId = cursor.getInt(0);
                int dateDay = cursor.getInt(1);
                int dateMonth = cursor.getInt(2);
                int dateYear = cursor.getInt(3);
                int morningEmpty = cursor.getInt(4);
                int morningFull = cursor.getInt(5);
                int afternoonEmpty = cursor.getInt(6);
                int afternoonFull = cursor.getInt(7);
                int eveningEmpty = cursor.getInt(8);
                int eveningFull = cursor.getInt(9);
                int night = cursor.getInt(10);


                ChartRowModel newDayRow = new ChartRowModel(rowId, dateDay, dateMonth, dateYear,morningEmpty,
                        morningFull, afternoonEmpty, afternoonFull, eveningEmpty, eveningFull, night);
                returnList.add(newDayRow);
            } while (cursor.moveToNext());
        }

        Log.d(TAG, "getEveryone: "+ returnList.size());
        cursor.close();
        sqLiteDatabase.close();
        return returnList;
    }

}

