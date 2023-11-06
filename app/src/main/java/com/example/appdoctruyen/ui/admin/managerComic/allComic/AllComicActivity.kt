package com.example.appdoctruyen.ui.admin.managerComic.allComic

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import com.example.appdoctruyen.databinding.ActivityManagerComicBinding
import com.example.appdoctruyen.model.Comic
import com.example.appdoctruyen.ui.admin.managerComic.addComic.AddComicActivity
import com.example.appdoctruyen.util.CollectionName
import com.example.appdoctruyen.util.destroyLoadingDialog
import com.example.appdoctruyen.util.toastMessage
import com.google.firebase.firestore.FirebaseFirestore


class AllComicActivity : AppCompatActivity() {

    private lateinit var binding: ActivityManagerComicBinding

    private lateinit var adapter: AllComicAdapter

    private val db = FirebaseFirestore.getInstance()

    private var originList = emptyList<Comic>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManagerComicBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
        setOnClick()
    }

    private fun initView() {
        adapter = AllComicAdapter(
            onClickEdit = { navigateToEditComic(it) },
            onClickDelete = { showDialogDeleteComic(it) })
        binding.rvComic.adapter = adapter
        getAllComic()
        //Tìm kiếm
        binding.timkiem.doOnTextChanged { text, start, before, count ->
            if (text.isNullOrBlank()) {
                adapter.submitList(originList)
            } else {
                adapter.submitList(originList.filter {
                    it.name.lowercase().contains(text.toString().lowercase())
                })
            }

        }
    }

    private fun navigateToEditComic(comic: Comic) {
        val intent = Intent(this, AddComicActivity::class.java)
        intent.putExtra("Comic", comic)
        startActivity(intent)
    }

    private fun showDialogDeleteComic(comic: Comic) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Bạn có muốn xóa truyện này không!")
            .setPositiveButton("Xóa") { dialog, id ->
                db.collection(CollectionName.COMIC).document(comic.id).delete()
                    .addOnSuccessListener {
                        toastMessage("Xóa thành công")
                    }.addOnFailureListener {
                        toastMessage("Xóa thất bại")
                    }
            }
            .setNegativeButton(
                "Hủy"
            ) { dialog, id -> }
        builder.create()
        builder.show()
    }

    private fun setOnClick() {
        binding.floatingAdd.setOnClickListener {
            if (hasStoragePermissionImg()) {
                val intent = Intent(this, AddComicActivity::class.java)
                startActivity(intent)
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ), 111
                )

            }
        }
        binding.imageBack.setOnClickListener {
            finish()
        }
    }

    //Lấy dữ liệu tất cả truyện
    private fun getAllComic() {
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
                adapter.submitList(listComic)
            }
    }

    //Kiểm tra xem đã có quyền đọc chưa
    private fun hasStoragePermissionImg(): Boolean {
        val read =
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
        return read == PackageManager.PERMISSION_GRANTED
    }

    override fun onDestroy() {
        super.onDestroy()
        destroyLoadingDialog()
    }

}