package com.example.appdoctruyen.ui.user.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.widget.doOnTextChanged
import com.example.appdoctruyen.databinding.ActivityHomeBinding
import com.example.appdoctruyen.model.Category
import com.example.appdoctruyen.model.Comic
import com.example.appdoctruyen.ui.user.comicByCategory.ComicByCategoryActivity
import com.example.appdoctruyen.ui.user.comicInfo.ComicInfoActivity
import com.example.appdoctruyen.util.CollectionName
import com.example.appdoctruyen.util.destroyLoadingDialog
import com.example.appdoctruyen.util.hiddenLoading
import com.example.appdoctruyen.util.showLoading
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding

    private lateinit var adapterComic: UserAllComicAdapter
    private lateinit var adapterCategory: UserAllCategoryAdapter
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val db = FirebaseFirestore.getInstance()

    private var originList = emptyList<Comic>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
        setOnClick()
    }

    private fun initView() {
        adapterComic = UserAllComicAdapter(onItemClick = {
            val intent = Intent(this, ComicInfoActivity::class.java)
            intent.putExtra("Comic", it)
            startActivity(intent)
        })
        adapterCategory = UserAllCategoryAdapter(onItemClick = {
            val intent = Intent(this,ComicByCategoryActivity::class.java)
            intent.putExtra("Category",it)
            startActivity(intent)
        })
        binding.rvComic.adapter = adapterComic
        binding.rvCategory.adapter = adapterCategory
        getAllComic()
        getAllCategory()
        binding.timkiem.doOnTextChanged { text, start, before, count ->
            if (text.isNullOrBlank()) {
                adapterComic.submitList(originList)
            } else {
                adapterComic.submitList(originList.filter {
                    it.name.lowercase().contains(text.toString().lowercase())
                })
            }
        }
        binding.textName.text = auth.currentUser?.displayName ?: ""
    }

    private fun setOnClick() {
    }

    private fun getAllComic() {
        showLoading()
        db.collection(CollectionName.COMIC)
            .addSnapshotListener { value, error ->
                val listComic = mutableListOf<Comic>()
                listComic.clear()
                val document = value?.documents
                if (document != null) {
                    for (item in document) {
                        val category = item.toObject(Comic::class.java)
                        if (category != null) {
                            listComic.add(category)
                        }
                    }
                }
                originList = listComic
                adapterComic.submitList(listComic)
                hiddenLoading()
            }
    }

    private fun getAllCategory() {
        showLoading()
        db.collection(CollectionName.CATEGORY)
            .addSnapshotListener { value, error ->
                val listCategory = mutableListOf<Category>()
                listCategory.clear()
                val document = value?.documents
                if (document != null) {
                    for (item in document) {
                        val category = item.toObject(Category::class.java)
                        if (category != null) {
                            listCategory.add(category)
                        }
                    }
                }
                adapterCategory.submitList(listCategory)
                hiddenLoading()
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        destroyLoadingDialog()
    }


}