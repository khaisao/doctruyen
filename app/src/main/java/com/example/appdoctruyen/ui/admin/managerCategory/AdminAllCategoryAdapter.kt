package com.example.appdoctruyen.ui.admin.managerCategory

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.appdoctruyen.databinding.ItemCategoryBinding
import com.example.appdoctruyen.model.Category

class AllCategoryAdapter(
    val onClickEdit: (Category) -> Unit,
    val onClickDelete: (Category) -> Unit,
) :
    ListAdapter<Category, AllCategoryAdapter.CategoryViewHolder>(CategoryDiffUtil) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = CategoryViewHolder(
        ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )


    override fun onBindViewHolder(holder: AllCategoryAdapter.CategoryViewHolder, position: Int) {
        holder.bindData(getItem(position))
    }

    inner class CategoryViewHolder(
        private val binding: ItemCategoryBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindData(
            item: Category
        ) {
            binding.textNameTl.text = item.name
            binding.imageEdit.setOnClickListener {
                onClickEdit.invoke(item)
            }
            binding.imageDelete.setOnClickListener {
                onClickDelete.invoke(item)
            }

        }
    }
}

object CategoryDiffUtil : DiffUtil.ItemCallback<Category>() {
    override fun areItemsTheSame(oldItem: Category, newItem: Category): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Category, newItem: Category): Boolean {
        return oldItem == newItem
    }
}