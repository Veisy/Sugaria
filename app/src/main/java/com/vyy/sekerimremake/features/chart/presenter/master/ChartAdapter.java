package com.vyy.sekerimremake.features.chart.presenter.master;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.vyy.sekerimremake.R;
import com.vyy.sekerimremake.databinding.ChartDayBinding;
import com.vyy.sekerimremake.features.chart.domain.model.ChartDayModel;
import com.vyy.sekerimremake.features.chart.utils.GlucoseLevelChecker;

import java.util.List;

public class ChartAdapter extends RecyclerView.Adapter<ChartAdapter.ChartViewHolder> {

    private final Context mContext;
    private final List<ChartDayModel> chartDays;
    private final OnDayClickListener mOnDayListener;

    public ChartAdapter(Context mContext, List<ChartDayModel> chartDays, OnDayClickListener onDayClickListener) {
        this.mContext = mContext;
        this.chartDays = chartDays;
        this.mOnDayListener = onDayClickListener;
    }

    public static class ChartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ChartDayBinding binding;
        OnDayClickListener onDayClickListener;

        public ChartViewHolder(@NonNull ChartDayBinding b, OnDayClickListener onDayClickListener) {
            super(b.getRoot());
            binding = b;
            this.onDayClickListener = onDayClickListener;
            binding.chartDay.setOnClickListener(this);
        }

        public void setTransitionName(int imageId) {
            ViewCompat.setTransitionName(binding.chartDay, String.valueOf(imageId));
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            onDayClickListener.onRowClick(position, binding.chartDay);
        }
    }

    public interface OnDayClickListener {
        void onRowClick(int position, View view);
    }

    @NonNull
    @Override
    public ChartAdapter.ChartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ChartViewHolder(ChartDayBinding.inflate(LayoutInflater.from(mContext),
                parent, false)
                , mOnDayListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ChartViewHolder holder, int position) {
        ChartDayModel theDay = chartDays.get(position);
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
                warningFlag = GlucoseLevelChecker.checkGlucoseLevel(mContext, i, theDayFields[i]);
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
        return chartDays.size();
    }
}
