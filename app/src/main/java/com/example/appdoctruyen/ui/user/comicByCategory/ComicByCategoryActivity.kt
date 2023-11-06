package com.example.appdoctruyen.ui.user.comicByCategory

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.appdoctruyen.databinding.ActivityComicByCategoryBinding
import com.example.appdoctruyen.model.Category
import com.example.appdoctruyen.model.Comic
import com.example.appdoctruyen.ui.user.comicInfo.ComicInfoActivity
import com.example.appdoctruyen.ui.user.home.UserAllComicAdapter
import com.example.appdoctruyen.util.CollectionName
import com.example.appdoctruyen.util.destroyLoadingDialog
import com.example.appdoctruyen.util.hiddenLoading
import com.example.appdoctruyen.util.showLoading
import com.example.appdoctruyen.util.toastMessage
import com.google.firebase.firestore.FirebaseFirestore

class ComicByCategoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityComicByCategoryBinding
    private val db = FirebaseFirestore.getInstance()
    private lateinit var adapterComic: UserAllComicAdapter

    private var category: Category? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityComicByCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
        setOnClick()
    }

    private fun initView() {
        category = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("Category", Category::class.java)
        } else {
            intent.getParcelableExtra("Category")
        }
        binding.textTenTheLoai.text = category?.name ?: ""
        adapterComic = UserAllComicAdapter(onItemClick = {
            val intent = Intent(this, ComicInfoActivity::class.java)
            intent.putExtra("Comic", it)
            startActivity(intent)
        })
        binding.listItemTruyen.adapter = adapterComic
        getAllComic()
    }

    private fun setOnClick() {
        binding.imgBack.setOnClickListener {
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        destroyLoadingDialog()
    }

    //Lấy tất cả truyện theo thể loại
    private fun getAllComic() {
        showLoading()
        db.collection(CollectionName.COMIC).whereEqualTo("categoryId", category?.id).get()
            .addOnSuccessListener { document ->
                val listComic = document.toObjects(Comic::class.java)
                adapterComic.submitList(listComic)
                hiddenLoading()

            }
            .addOnFailureListener {
                toastMessage("Lỗi: ${it.message}")
                hiddenLoading()
            }
            .addOnCanceledListener {
                hiddenLoading()
            }
    }
}