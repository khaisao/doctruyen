package com.example.appdoctruyen.ui.user.comicInfo

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.appdoctruyen.databinding.ItemChapDetailBinding
import com.example.appdoctruyen.model.Chap

class UserAllChapAdapter(
    val onItemClick: (Chap) -> Unit
) :
    ListAdapter<Chap, UserAllChapAdapter.AllChapViewHolder>(ChapDiffUtil) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = AllChapViewHolder(
        ItemChapDetailBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )


    override fun onBindViewHolder(holder: AllChapViewHolder, position: Int) {
        holder.bindData(getItem(position))
    }

    inner class AllChapViewHolder(
        private val binding: ItemChapDetailBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindData(
            item: Chap
        ) {
            binding.tvChapName.text = item.name
            binding.root.setOnClickListener {
                onItemClick.invoke(item)
            }
        }
    }
}

object ChapDiffUtil : DiffUtil.ItemCallback<Chap>() {
    override fun areItemsTheSame(oldItem: Chap, newItem: Chap): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Chap, newItem: Chap): Boolean {
        return oldItem == newItem
    }
}