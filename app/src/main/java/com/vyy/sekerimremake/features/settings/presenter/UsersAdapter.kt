package com.vyy.sekerimremake.features.settings.presenter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.vyy.sekerimremake.R
import com.vyy.sekerimremake.databinding.UserListItemBinding
import com.vyy.sekerimremake.features.settings.domain.model.UserModel

class UsersAdapter(private val onUserClicked: (Int) -> Unit) :
    ListAdapter<UserModel, UsersAdapter.UserViewHolder>(UsersComparator()) {

    var selectedPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding: UserListItemBinding = UserListItemBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding, onUserClicked)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(position, current.name, current.userName)
    }

    fun selectUser(position: Int) {
        if (selectedPosition == position) return
        val oldPosition = selectedPosition
        selectedPosition = position
        notifyItemChanged(oldPosition)
        notifyItemChanged(selectedPosition)
    }

    inner class UserViewHolder(
        private val binding: UserListItemBinding,
        onUserClicked: (Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                onUserClicked(adapterPosition)
            }
        }

        fun bind(position: Int, name: String?, userName: String?) {
            binding.apply {
                val background = when {
                    selectedPosition < 0 -> {
                        checkBoxIsSelected.apply {
                            visibility = View.INVISIBLE
                            isEnabled = false
                            isChecked = true
                        }
                        R.color.color_white
                    }
                    position == selectedPosition -> {
                        checkBoxIsSelected.apply {
                            visibility = View.VISIBLE
                            isEnabled = true
                            isChecked = true
                        }
                        R.color.color_light_yellow
                    }
                    else -> {
                        checkBoxIsSelected.apply {
                            visibility = View.VISIBLE
                            isEnabled = true
                            isChecked = false
                        }
                        R.color.color_white
                    }
                }
                constraintLayoutUserListItem.setBackgroundResource(background)
                textViewUserDisplayName.text = name
                textViewUserName.visibility =
                    if (userName.isNullOrEmpty()) View.GONE else View.VISIBLE.apply {
                        val userNameWithAtSign = "@${userName}"
                        textViewUserName.text = userNameWithAtSign
                    }
            }
        }
    }

    class UsersComparator : DiffUtil.ItemCallback<UserModel>() {
        override fun areItemsTheSame(oldItem: UserModel, newItem: UserModel): Boolean =
            oldItem === newItem

        override fun areContentsTheSame(oldItem: UserModel, newItem: UserModel): Boolean =
            oldItem.uid == newItem.uid

    }
}