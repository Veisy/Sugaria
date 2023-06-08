package com.vyy.sekerimremake.features.chart.presenter.master

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.vyy.sekerimremake.R
import com.vyy.sekerimremake.databinding.ChartDayBinding
import com.vyy.sekerimremake.features.chart.domain.model.ChartDayModel
import com.vyy.sekerimremake.features.chart.utils.GlucoseLevelChecker
import java.util.*

class ChartAdapter(private val onRowClick: (ChartDayModel, View) -> Unit) :
    ListAdapter<ChartDayModel, ChartAdapter.ChartViewHolder>(ChartComparator()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChartViewHolder {
        val binding: ChartDayBinding =
            ChartDayBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChartViewHolder(binding, onRowClick)
    }

    override fun onBindViewHolder(holder: ChartViewHolder, position: Int) {
        val currentDay = getItem(position)
        holder.bind(currentDay)
    }

    inner class ChartViewHolder(
        private val binding: ChartDayBinding, onRowClick: (ChartDayModel, View) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            itemView.setOnClickListener {
                onRowClick(getItem(adapterPosition), binding.chartDay)
            }
        }

        fun bind(currentDay: ChartDayModel) {
            val monthAndYearTogether: String =
                itemView.context.resources.getStringArray(R.array.Months)[Objects.requireNonNull<String>(
                    currentDay.month
                ).toInt()] + "\n" + currentDay.year
            binding.apply {
                root.transitionName = currentDay.id
                textViewDayDayOfMonth.text = currentDay.dayOfMonth
                textViewDayMonthAndYear.text = monthAndYearTogether
                val theDayFields = arrayOf(
                    currentDay.morningEmpty,
                    currentDay.morningFull,
                    currentDay.afternoonEmpty,
                    currentDay.afternoonFull,
                    currentDay.eveningEmpty,
                    currentDay.eveningFull,
                    currentDay.night
                )
                val holderTextViews = arrayOf(
                    textViewDayMorningEmpty,
                    textViewDayMorningFull,
                    textViewDayAfternoonEmpty,
                    textViewDayAfternoonFull,
                    textViewDayEveningEmpty,
                    textViewDayEveningFull,
                    textViewDayNight
                )

                //Chance background according to value range.
                var warningFlag: Int
                for (i in theDayFields.indices) {
                    val field = theDayFields[i]
                    if (field == null || field.isEmpty() || field == "0") {
                        holderTextViews[i].text = ""
                        holderTextViews[i].setBackgroundResource(R.drawable.text_frame)
                    } else {
                        holderTextViews[i].text = field
                        warningFlag = GlucoseLevelChecker.checkGlucoseLevel(
                            itemView.context, i, field.toInt()
                        )
                        when (warningFlag) {
                            2 -> {
                                holderTextViews[i].setBackgroundResource(R.drawable.text_frame_warning_range)
                            }
                            3 -> {
                                holderTextViews[i].setBackgroundResource(R.drawable.text_frame_margin_range)
                            }
                            else -> {
                                holderTextViews[i].setBackgroundResource(R.drawable.text_frame_normal_range)
                            }
                        }
                    }
                }
            }
        }
    }

    class ChartComparator : DiffUtil.ItemCallback<ChartDayModel>() {
        override fun areItemsTheSame(oldItem: ChartDayModel, newItem: ChartDayModel): Boolean =
            oldItem === newItem

        override fun areContentsTheSame(oldItem: ChartDayModel, newItem: ChartDayModel): Boolean =
            oldItem.id == newItem.id

    }
}