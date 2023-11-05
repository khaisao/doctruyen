package com.example.appdoctruyen.ui.admin.managerChap.allChap

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.appdoctruyen.databinding.ItemCategoryBinding
import com.example.appdoctruyen.model.Chap

class AllChapAdapter(
    val onClickEdit: (Chap) -> Unit,
    val onClickDelete: (Chap) -> Unit,
) :
    ListAdapter<Chap, AllChapAdapter.AllChapViewHolder>(ChapDiffUtil) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = AllChapViewHolder(
        ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )


    override fun onBindViewHolder(holder: AllChapViewHolder, position: Int) {
        holder.bindData(getItem(position))
    }

    inner class AllChapViewHolder(
        private val binding: ItemCategoryBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindData(
            item: Chap
        ) {


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