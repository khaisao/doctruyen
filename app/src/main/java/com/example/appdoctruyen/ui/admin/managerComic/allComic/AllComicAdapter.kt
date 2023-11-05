package com.example.appdoctruyen.ui.admin.managerComic.allComic

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.appdoctruyen.databinding.ItemComicBinding
import com.example.appdoctruyen.model.Category
import com.example.appdoctruyen.model.Comic
import com.example.appdoctruyen.ui.admin.managerChap.allChap.AllChapActivity
import com.example.appdoctruyen.util.CollectionName
import com.google.firebase.firestore.FirebaseFirestore

class AllComicAdapter(
    val onClickEdit: (Comic) -> Unit,
    val onClickDelete: (Comic) -> Unit,
) :
    ListAdapter<Comic, AllComicAdapter.ComicViewHolder>(CategoryDiffUtil) {

    private val db = FirebaseFirestore.getInstance()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = ComicViewHolder(
        ItemComicBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )


    override fun onBindViewHolder(holder: ComicViewHolder, position: Int) {
        holder.bindData(getItem(position))
    }

    inner class ComicViewHolder(
        private val binding: ItemComicBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindData(
            item: Comic
        ) {
            Glide.with(binding.root.context).load(item.thumbnailUrl).into(binding.imageTruyen)
            binding.textTenTruyen.text = item.name
            binding.imageDelete.setOnClickListener {
                onClickDelete.invoke(item)
            }
            binding.imageEdit.setOnClickListener {
                onClickEdit.invoke(item)
            }
            db.collection(CollectionName.CATEGORY).document(item.categoryId)
                .get().addOnSuccessListener { document ->
                    val category = document.toObject(Category::class.java)
                    if (category != null) {
                        binding.textTheLoai.text = category.name
                    }
                }

            binding.root.setOnClickListener {
                val intent = Intent(binding.root.context, AllChapActivity::class.java)
                intent.putExtra("ComicId", item.id)
                binding.root.context.startActivity(intent)
            }
        }
    }
}

object CategoryDiffUtil : DiffUtil.ItemCallback<Comic>() {
    override fun areItemsTheSame(oldItem: Comic, newItem: Comic): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Comic, newItem: Comic): Boolean {
        return oldItem == newItem
    }
}