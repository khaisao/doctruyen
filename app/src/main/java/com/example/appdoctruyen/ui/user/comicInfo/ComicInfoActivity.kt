package com.example.appdoctruyen.ui.user.comicInfo

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.appdoctruyen.databinding.ActivityDetailComicBinding
import com.example.appdoctruyen.model.Category
import com.example.appdoctruyen.model.Chap
import com.example.appdoctruyen.model.Comic
import com.example.appdoctruyen.model.ComicStatus
import com.example.appdoctruyen.ui.user.detailChap.DetailChapActivity
import com.example.appdoctruyen.util.CollectionName
import com.example.appdoctruyen.util.destroyLoadingDialog
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ComicInfoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailComicBinding
    private var comic: Comic? = null
    private val db = FirebaseFirestore.getInstance()

    private lateinit var adapter: UserAllChapAdapter

    private val listChap = mutableListOf<Chap>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailComicBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
        setOnClick()
    }

    private fun initView() {
        comic = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("Comic", Comic::class.java)
        } else {
            intent.getParcelableExtra("Comic")
        }
        if (comic != null) {
            binding.textTenTruyen.text = comic!!.name
            binding.textTime.text = formatDate(comic!!.createAt)
            if (comic!!.status == ComicStatus.DONE.status) {
                binding.textTinhtrang.text = "Kết thúc"
            }
            if (comic!!.status == ComicStatus.IN_PROGRESS.status) {
                binding.textTinhtrang.text = "Đang tiến hành"
            }
            binding.textGioithieu.text = comic!!.introduce
            Glide.with(this).load(comic!!.thumbnailUrl).into(binding.imageTruyen)
            getAllChap()
            getCategory()
        }
        adapter = UserAllChapAdapter(onItemClick = {
            val intent = Intent(this, DetailChapActivity::class.java)
            intent.putExtra("ChapId", it.id)
            intent.putExtra("ComicId", it.comicId)
            startActivity(intent)
        })
        binding.listItem.adapter = adapter

    }

    private fun setOnClick() {
        binding.imgBack.setOnClickListener {
            finish()
        }
    }

    private fun formatDate(timestamp: Long): String {
        val date = Date(timestamp)
        val sdf = SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.US)
        return sdf.format(date)
    }

    private fun getAllChap() {
        db.collection(CollectionName.CHAP).whereEqualTo("comicId", comic?.id)
            .addSnapshotListener { value, error ->
                listChap.clear()
                val document = value?.documents
                if (document != null) {
                    for (item in document) {
                        val chap = item.toObject(Chap::class.java)
                        if (chap != null) {
                            listChap.add(chap)
                        }
                    }
                }
                binding.textSlchap.text = listChap.size.toString() + " Chap"
                adapter.submitList(listChap.sortedBy { it.createAt })

            }
    }

    private fun getCategory() {
        db.collection(CollectionName.CATEGORY).document(comic?.categoryId ?: "")
            .get().addOnSuccessListener { document ->
                val category = document.toObject(Category::class.java)
                if (category != null) {
                    binding.textTenTheLoai.text = category.name
                }

            }
    }

    override fun onDestroy() {
        super.onDestroy()
        destroyLoadingDialog()
    }
}