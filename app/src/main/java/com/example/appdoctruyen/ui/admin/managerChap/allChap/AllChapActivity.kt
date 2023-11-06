package com.example.appdoctruyen.ui.admin.managerChap.allChap

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.appdoctruyen.databinding.ActivityAllChapBinding
import com.example.appdoctruyen.model.Chap
import com.example.appdoctruyen.ui.admin.managerChap.addChap.AddChapActivity
import com.example.appdoctruyen.util.CollectionName
import com.example.appdoctruyen.util.destroyLoadingDialog
import com.example.appdoctruyen.util.toastMessage
import com.google.firebase.firestore.FirebaseFirestore

class AllChapActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAllChapBinding

    private lateinit var adapter: AllChapAdapter

    private val db = FirebaseFirestore.getInstance()

    private var comicId: String? = null

    private var currentListChapName = arrayListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAllChapBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
        setOnClick()
    }

    private fun initView() {
        comicId = intent.getStringExtra("ComicId")
        adapter = AllChapAdapter(
            onClickEdit = {
                val intent = Intent(this, AddChapActivity::class.java)
                intent.putExtra("Chap", it)
                startActivity(intent)
            },
            onClickDelete = {
                showDialogDeleteChapConfirm(it)
            })
        binding.rvChap.adapter = adapter
        getAllChap()
    }

    private fun setOnClick() {
        binding.floatingAdd.setOnClickListener {
            val intent = Intent(this, AddChapActivity::class.java)
            intent.putExtra("ComicId", comicId)
            intent.putStringArrayListExtra("CurrentChapName", currentListChapName)
            startActivity(intent)
        }
        binding.imgBack.setOnClickListener {
            finish()
        }
    }

    private fun showDialogDeleteChapConfirm(chap: Chap) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Bạn có muốn xóa chap này không!")
            .setPositiveButton("Xóa") { dialog, id ->
                db.collection(CollectionName.CHAP).document(chap.id)
                    .delete().addOnSuccessListener {
                        toastMessage("Xoá thành công")
                    }.addOnFailureListener {
                        toastMessage("Xoát thất bại")
                    }.addOnCanceledListener {
                        toastMessage("Xoá thất bại")
                    }
            }
            .setNegativeButton(
                "Hủy"
            ) { dialog, id -> }

        builder.create()
        builder.show()
    }

    //Lấy tất cả chap của 1 truyện (comicId)
    private fun getAllChap() {
        db.collection(CollectionName.CHAP).whereEqualTo("comicId", comicId)
            .addSnapshotListener { value, error ->
                val listChap = mutableListOf<Chap>()
                listChap.clear()
                currentListChapName.clear()
                val document = value?.documents
                if (document != null) {
                    for (item in document) {
                        val chap = item.toObject(Chap::class.java)
                        if (chap != null) {
                            listChap.add(chap)
                            currentListChapName.add(chap.name)
                        }
                    }
                }
                adapter.submitList(listChap.sortedBy { it.createAt })
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        destroyLoadingDialog()
    }

}