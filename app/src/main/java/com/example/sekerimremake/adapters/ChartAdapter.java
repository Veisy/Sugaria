package com.example.sekerimremake.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sekerimremake.R;
import com.example.sekerimremake.databinding.ChartSingleDayRowBinding;
import com.example.sekerimremake.models.ChartRowModel;
import com.example.sekerimremake.utils.CheckValueRange;

import java.util.List;

public class ChartAdapter extends RecyclerView.Adapter<ChartAdapter.ChartViewHolder> {

    private final Context mContext;
    private final List<ChartRowModel> chartRows;
    private final OnRowClickListener mOnRowListener;

    public ChartAdapter(Context mContext, List<ChartRowModel> chartRows, OnRowClickListener onRowClickListener) {
        this.mContext = mContext;
        this.chartRows = chartRows;
        this.mOnRowListener = onRowClickListener;
    }

    public static class ChartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ChartSingleDayRowBinding binding;
        OnRowClickListener onRowClickListener;

        public ChartViewHolder(@NonNull ChartSingleDayRowBinding b, OnRowClickListener onRowClickListener) {
            super(b.getRoot());
            binding = b;
            this.onRowClickListener = onRowClickListener;
            binding.singleDay.setOnClickListener(this);
        }

        public void setTransitionName(int imageId) {
            ViewCompat.setTransitionName(binding.singleDay, String.valueOf(imageId));
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            onRowClickListener.onRowClick(position, binding.singleDay);
        }
    }

    public interface OnRowClickListener {
        void onRowClick(int position, View view);
    }

    @NonNull
    @Override
    public ChartAdapter.ChartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ChartViewHolder(ChartSingleDayRowBinding.inflate(LayoutInflater.from(mContext),
                parent, false)
                , mOnRowListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ChartViewHolder holder, int position) {
        ChartRowModel theDay = chartRows.get(position);
        String monthAndYearTogether = mContext.getResources()
                .getStringArray(R.array.Months)[theDay.getMonth()] + "\n" + theDay.getYear();
        holder.binding.textViewDayDay.setText(String.valueOf(theDay.getDayOfMonth()));
        holder.binding.textViewDayMonthYear.setText(monthAndYearTogether);
        holder.setTransitionName(theDay.getRowId());
        final int[] theDayFields = {theDay.getMorningEmpty(), theDay.getMorningFull(),
                theDay.getAfternoonEmpty(), theDay.getAfternoonFull(), theDay.getEveningEmpty(),
                theDay.getEveningFull(), theDay.getNight()};
        final TextView[] holderTextViews = {holder.binding.textViewDayMorningEmpty,
                holder.binding.textViewDayMorningFull, holder.binding.textViewDayAfternoonEmpty,
                holder.binding.textViewDayAfternoonFull, holder.binding.textViewDayEveningEmpty,
                holder.binding.textViewDayEveningFull, holder.binding.textViewDayNight};

        //Chance background according to value range.
        int warningFlag;
        for(int i = 0; i < theDayFields.length; i++) {
            if (theDayFields[i] == 0) {
                holderTextViews[i].setText("");
                holderTextViews[i].setBackgroundResource(R.drawable.text_frame);
            } else {
                holderTextViews[i].setText(String.valueOf(theDayFields[i]));
                warningFlag = CheckValueRange.checkValueRange(mContext, i, theDayFields[i]);
                if (warningFlag == 2){
                    holderTextViews[i].setBackgroundResource(R.drawable.text_frame_warning_range);
                } else if (warningFlag == 3){
                    holderTextViews[i].setBackgroundResource(R.drawable.text_frame_margin_range);
                } else {
                    holderTextViews[i].setBackgroundResource(R.drawable.text_frame_normal_range);
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return chartRows.size();
    }
}
