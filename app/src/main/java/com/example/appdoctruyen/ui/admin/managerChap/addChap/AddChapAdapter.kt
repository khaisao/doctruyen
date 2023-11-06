package com.example.appdoctruyen.ui.admin.managerChap.addChap

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.appdoctruyen.databinding.ItemCategoryBinding
import com.example.appdoctruyen.databinding.ItemImageChapBinding
import com.example.appdoctruyen.model.Category

class AddChapAdapter(
    val onClickEdit: (String) -> Unit,
    val onClickDelete: (String) -> Unit,
) :
    ListAdapter<String, AddChapAdapter.AddChapViewHolder>(ChapImageDiffUtil) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = AddChapViewHolder(
        ItemImageChapBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )


    override fun onBindViewHolder(holder: AddChapAdapter.AddChapViewHolder, position: Int) {
        holder.bindData(getItem(position))
    }

    inner class AddChapViewHolder(
        private val binding: ItemImageChapBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bindData(
            item: String
        ) {
            Log.d("asgawgwagawg", "bindData: $item")
            Glide.with(binding.root.context).load(item).into(binding.imageChap)
            binding.buttonXoa.setOnClickListener {
                onClickDelete.invoke(item)
            }
            binding.buttonSua.setOnClickListener {
                onClickEdit.invoke(item)
            }
        }
    }
}

object ChapImageDiffUtil : DiffUtil.ItemCallback<String>() {
    override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
        return false
    }
}