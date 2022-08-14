package com.vyy.sekerimremake.models;

public class ChartRowModel {
    private int rowId, dayOfMonth, month, year, morningEmpty, morningFull, afternoonEmpty,
            afternoonFull, eveningEmpty, eveningFull, night;

    public ChartRowModel() {
    }

    public ChartRowModel(int rowId, int dayOfMonth, int month, int year,  int morningEmpty,
                         int morningFull, int afternoonEmpty, int afternoonFull, int eveningEmpty,
                         int eveningFull, int night) {
        this.rowId = rowId;
        this.dayOfMonth = dayOfMonth;
        this.month = month;
        this.year = year;
        this.morningEmpty = morningEmpty;
        this.morningFull = morningFull;
        this.afternoonEmpty = afternoonEmpty;
        this.afternoonFull = afternoonFull;
        this.eveningEmpty = eveningEmpty;
        this.eveningFull = eveningFull;
        this.night = night;
    }

    public int getRowId() {
        return rowId;
    }

    public int getDayOfMonth() {
        return dayOfMonth;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }

    public int getMorningEmpty() {
        return morningEmpty;
    }

    public int getMorningFull() {
        return morningFull;
    }

    public int getAfternoonEmpty() {
        return afternoonEmpty;
    }

    public int getAfternoonFull() {
        return afternoonFull;
    }

    public int getEveningEmpty() {
        return eveningEmpty;
    }

    public int getEveningFull() {
        return eveningFull;
    }

    public int getNight() {
        return night;
    }

    public void setRowId(int rowId) {
        this.rowId = rowId;
    }

    public void setDayOfMonth(int dayOfMonth) {
        this.dayOfMonth = dayOfMonth;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public void setYear(int year) {
        this.year = year;
    }
    public void setMorningEmpty(int morningEmpty) {
        this.morningEmpty = morningEmpty;
    }

    public void setMorningFull(int morningFull) {
        this.morningFull = morningFull;
    }

    public void setAfternoonEmpty(int afternoonEmpty) {
        this.afternoonEmpty = afternoonEmpty;
    }

    public void setAfternoonFull(int afternoonFull) {
        this.afternoonFull = afternoonFull;
    }

    public void setEveningEmpty(int eveningEmpty) {
        this.eveningEmpty = eveningEmpty;
    }

    public void setEveningFull(int eveningFull) {
        this.eveningFull = eveningFull;
    }

    public void setNight(int night) {
        this.night = night;
    }
}
