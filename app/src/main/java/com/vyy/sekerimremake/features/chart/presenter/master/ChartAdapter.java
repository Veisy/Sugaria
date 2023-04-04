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
import java.util.Objects;

public class ChartAdapter extends RecyclerView.Adapter<ChartAdapter.ChartViewHolder> {

    private final Context mContext;
    private final List<ChartDayModel> chartDays;
    private final OnDayClickListener mOnDayListener;

    public ChartAdapter(Context mContext, List<ChartDayModel> chartDays, OnDayClickListener onDayClickListener) {
        this.mContext = mContext;
        this.chartDays = chartDays;
        this.mOnDayListener = onDayClickListener;
    }

    public class ChartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ChartDayBinding binding;
        OnDayClickListener onDayClickListener;

        public ChartViewHolder(@NonNull ChartDayBinding b, OnDayClickListener onDayClickListener) {
            super(b.getRoot());
            binding = b;
            this.onDayClickListener = onDayClickListener;
            binding.chartDay.setOnClickListener(this);
        }

        public void setTransitionName(String imageId) {
            ViewCompat.setTransitionName(binding.chartDay, imageId);
        }

        @Override
        public void onClick(View v) {
            onDayClickListener.onRowClick(chartDays.get(getAdapterPosition()), binding.chartDay);
        }
    }

    public interface OnDayClickListener {
        void onRowClick(ChartDayModel day, View view);
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
                .getStringArray(R.array.Months)[Integer.parseInt(Objects.requireNonNull(theDay.getMonth()))] + "\n" + theDay.getYear();
        holder.binding.textViewDayDayOfMonth.setText(theDay.getDayOfMonth());
        holder.binding.textViewDayMonthAndYear.setText(monthAndYearTogether);
        holder.setTransitionName(theDay.getId());
        final String[] theDayFields = {theDay.getMorningEmpty(), theDay.getMorningFull(),
                theDay.getAfternoonEmpty(), theDay.getAfternoonFull(), theDay.getEveningEmpty(),
                theDay.getEveningFull(), theDay.getNight()};
        final TextView[] holderTextViews = {holder.binding.textViewDayMorningEmpty,
                holder.binding.textViewDayMorningFull, holder.binding.textViewDayAfternoonEmpty,
                holder.binding.textViewDayAfternoonFull, holder.binding.textViewDayEveningEmpty,
                holder.binding.textViewDayEveningFull, holder.binding.textViewDayNight};

        for(int i = 0; i < theDayFields.length; i++) {
            String field = theDayFields[i];
            if (field == null || field.isEmpty() || field.equals("0")) {
                holderTextViews[i].setText("");
                holderTextViews[i].setBackgroundResource(R.drawable.text_frame);
            } else {
                holderTextViews[i].setText(field);
                //Chance background according to value range.
                int warningFlag = GlucoseLevelChecker.checkGlucoseLevel(mContext, i, Integer.parseInt(field));
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
