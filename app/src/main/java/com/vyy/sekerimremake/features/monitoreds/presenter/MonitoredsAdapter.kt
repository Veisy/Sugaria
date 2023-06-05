package com.vyy.sekerimremake.features.monitoreds.presenter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.vyy.sekerimremake.R
import com.vyy.sekerimremake.databinding.MonitoredsListItemBinding
import com.vyy.sekerimremake.features.settings.domain.model.MonitoredModel

class MonitoredsAdapter(private val onMonitoredClicked: (Int) -> Unit) :
    ListAdapter<MonitoredModel, MonitoredsAdapter.MonitoredViewHolder>(MonitoredsComparator()) {

    var selectedPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MonitoredViewHolder {
        val binding: MonitoredsListItemBinding = MonitoredsListItemBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return MonitoredViewHolder(binding, onMonitoredClicked)
    }

    override fun onBindViewHolder(holder: MonitoredViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(position, current.name)
    }

    fun selectMonitored(position: Int) {
        val oldPosition = selectedPosition
        selectedPosition = position
        notifyItemChanged(oldPosition)
        notifyItemChanged(selectedPosition)
    }

    inner class MonitoredViewHolder(
        private val binding: MonitoredsListItemBinding,
        onWordClicked: (Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            itemView.setOnClickListener {
                onWordClicked(adapterPosition)
            }
        }

        fun bind(position: Int, text: String?) {
            val background: Int = if (position == selectedPosition) {
                R.drawable.button_back
            } else {
                R.drawable.button_previous
            }
            binding.textViewMonitored.setBackgroundResource(background)
            binding.textViewMonitored.text = text
        }
    }

    class MonitoredsComparator : DiffUtil.ItemCallback<MonitoredModel>() {
        override fun areItemsTheSame(oldItem: MonitoredModel, newItem: MonitoredModel): Boolean =
            oldItem === newItem

        override fun areContentsTheSame(oldItem: MonitoredModel, newItem: MonitoredModel): Boolean =
            oldItem.uid == newItem.uid

    }
}