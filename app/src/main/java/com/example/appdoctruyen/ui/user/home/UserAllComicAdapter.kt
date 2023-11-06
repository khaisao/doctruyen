package com.example.appdoctruyen.ui.user.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.appdoctruyen.databinding.ItemComicHomeBinding
import com.example.appdoctruyen.model.Category
import com.example.appdoctruyen.model.Chap
import com.example.appdoctruyen.model.Comic
import com.example.appdoctruyen.ui.admin.managerComic.allComic.ComicDiffUtil
import com.example.appdoctruyen.util.CollectionName
import com.google.firebase.firestore.FirebaseFirestore

class UserAllComicAdapter(
    val onItemClick: (Comic) -> Unit
) :
    ListAdapter<Comic, UserAllComicAdapter.ComicViewHolder>(ComicDiffUtil) {

    private val db = FirebaseFirestore.getInstance()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = ComicViewHolder(
        ItemComicHomeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )


    override fun onBindViewHolder(holder: ComicViewHolder, position: Int) {
        holder.bindData(getItem(position))
    }

    inner class ComicViewHolder(
        private val binding: ItemComicHomeBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindData(
            item: Comic
        ) {
            Glide.with(binding.root.context).load(item.thumbnailUrl).into(binding.imageViewAnh)
            binding.textName.text = item.name
            binding.root.setOnClickListener {
                onItemClick.invoke(item)
            }
            getAllChap(item, callBack = {
                if (it.isNotEmpty()) {
                    binding.textChap.text = it.last().name
                }
            })

        }
    }

    private fun getAllChap(comic: Comic, callBack: (List<Chap>) -> Unit) {
        db.collection(CollectionName.CHAP).whereEqualTo("comicId", comic.id).get()
            .addOnSuccessListener { document ->
                val listChap = document.toObjects(Chap::class.java)
                callBack.invoke(listChap.sortedBy { it.createAt })
            }

    }
}
