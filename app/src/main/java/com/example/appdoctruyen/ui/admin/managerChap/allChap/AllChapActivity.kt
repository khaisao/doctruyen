package com.example.appdoctruyen.ui.admin.managerChap.allChap

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.appdoctruyen.databinding.ActivityAllChapBinding
import com.example.appdoctruyen.model.Chap
import com.example.appdoctruyen.ui.admin.managerChap.addChap.AddChapActivity
import com.example.appdoctruyen.util.CollectionName
import com.example.appdoctruyen.util.destroyLoadingDialog
import com.google.firebase.firestore.FirebaseFirestore

class AllChapActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAllChapBinding

    private lateinit var adapter: AllChapAdapter

    private val db = FirebaseFirestore.getInstance()

    private var comicId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAllChapBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
        setOnClick()
    }

    private fun initView() {
        comicId = intent.getStringExtra("ComicId")
        adapter = AllChapAdapter(onClickEdit = {}, onClickDelete = {})
        binding.rvChap.adapter = adapter
        getAllChap()
    }

    private fun setOnClick() {
        binding.floatingAdd.setOnClickListener {
            val intent = Intent(this, AddChapActivity::class.java)
            startActivity(intent)
        }
    }

    private fun getAllChap() {
        db.collection(CollectionName.CHAP).addSnapshotListener { value, error ->
            val listChap = mutableListOf<Chap>()
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
            adapter.submitList(listChap)

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        destroyLoadingDialog()
    }

}