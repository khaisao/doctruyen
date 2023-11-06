package com.example.appdoctruyen.ui.user.detailChap

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.appdoctruyen.databinding.ActivityDetailChapBinding
import com.example.appdoctruyen.model.Chap
import com.example.appdoctruyen.util.CollectionName
import com.example.appdoctruyen.util.destroyLoadingDialog
import com.example.appdoctruyen.util.hiddenLoading
import com.example.appdoctruyen.util.showLoading
import com.example.appdoctruyen.util.toastMessage
import com.google.firebase.firestore.FirebaseFirestore

class DetailChapActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailChapBinding
    private var comicId: String? = null
    private var chapId: String? = null
    private val db = FirebaseFirestore.getInstance()
    private lateinit var adapter: DetailChapAdapter
    private var listChap = listOf<Chap>()

    private var currentIndexChap = 0
    private var listChapSize = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailChapBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
        setOnClick()
    }

    private fun initView() {
        comicId = intent.getStringExtra("ComicId")
        chapId = intent.getStringExtra("ChapId")
        adapter = DetailChapAdapter()
        binding.listItemImg.adapter = adapter
        getAllChap()
    }

    private fun setOnClick() {
        binding.imgBack.setOnClickListener {
            finish()
        }
        binding.buttonSau.setOnClickListener {
            currentIndexChap++
            val currentChap = listChap[currentIndexChap]
            adapter.submitList(currentChap.listImage)
            setUpUiForButtonNextAndPrevious()
            binding.tenchap.text = currentChap.name
        }
        binding.buttonTruoc.setOnClickListener {
            currentIndexChap--
            val currentChap = listChap[currentIndexChap]
            adapter.submitList(currentChap.listImage)
            setUpUiForButtonNextAndPrevious()
            binding.tenchap.text = currentChap.name
        }
    }

    private fun getAllChap() {
        showLoading()
        db.collection(CollectionName.CHAP).whereEqualTo("comicId", comicId).get()
            .addOnSuccessListener { document ->
                listChap = document.toObjects(Chap::class.java).sortedBy { it.createAt }
                listChapSize = listChap.size
                val currentChapSelect = listChap.find { it.id == chapId }
                if (currentChapSelect != null) {
                    adapter.submitList(currentChapSelect.listImage)
                    binding.tenchap.text = currentChapSelect.name
                    currentIndexChap = listChap.indexOf(currentChapSelect)
                }
                setUpUiForButtonNextAndPrevious()
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

    private fun setUpUiForButtonNextAndPrevious() {
        if (listChapSize == 1) {
            binding.buttonTruoc.isVisible = false
            binding.buttonSau.isVisible = false
        } else if (currentIndexChap == listChapSize - 1 && listChapSize > 0) {
            binding.buttonTruoc.isVisible = true
            binding.buttonSau.isVisible = false
        } else if (currentIndexChap == 0 && listChapSize > 0) {
            binding.buttonTruoc.isVisible = false
            binding.buttonSau.isVisible = true
        } else {
            binding.buttonTruoc.isVisible = true
            binding.buttonSau.isVisible = true
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        destroyLoadingDialog()
    }
}

