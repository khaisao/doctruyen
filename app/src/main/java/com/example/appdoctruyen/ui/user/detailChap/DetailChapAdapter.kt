package com.example.appdoctruyen.ui.user.detailChap

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.appdoctruyen.databinding.ItemChapDetailBinding
import com.example.appdoctruyen.databinding.ItemDetailChapImageBinding
import com.example.appdoctruyen.model.Chap

class DetailChapAdapter :
    ListAdapter<String, DetailChapAdapter.AllChapViewHolder>(DetailChapImage) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = AllChapViewHolder(
        ItemDetailChapImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )


    override fun onBindViewHolder(holder: AllChapViewHolder, position: Int) {
        holder.bindData(getItem(position))
    }

    inner class AllChapViewHolder(
        private val binding: ItemDetailChapImageBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindData(
            item: String
        ) {
            Glide.with(binding.root.context).load(item).into(binding.ivMain)
        }
    }
}

object DetailChapImage : DiffUtil.ItemCallback<String>() {
    override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
        return false
    }
}