package com.example.appdoctruyen.ui.user.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.appdoctruyen.databinding.ItemCategoryBinding
import com.example.appdoctruyen.databinding.ItemCategoryHomeBinding
import com.example.appdoctruyen.model.Category
import com.example.appdoctruyen.model.Comic
import com.example.appdoctruyen.ui.admin.managerCategory.CategoryDiffUtil

class UserAllCategoryAdapter(
    val onItemClick: (Category) -> Unit
) :
    ListAdapter<Category, UserAllCategoryAdapter.CategoryViewHolder>(CategoryDiffUtil) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = CategoryViewHolder(
        ItemCategoryHomeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )


    override fun onBindViewHolder(
        holder: UserAllCategoryAdapter.CategoryViewHolder,
        position: Int
    ) {
        holder.bindData(getItem(position))
    }

    inner class CategoryViewHolder(
        private val binding: ItemCategoryHomeBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindData(
            item: Category
        ) {
            binding.textTenTheLoai.text = item.name
            binding.root.setOnClickListener {
                onItemClick.invoke(item)
            }
        }
    }
}
