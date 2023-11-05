package com.example.appdoctruyen.ui.admin.managerChap.addChap

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.appdoctruyen.R
import com.example.appdoctruyen.databinding.ActivityAddChapBinding
import com.example.appdoctruyen.databinding.ActivityAllChapBinding
import com.example.appdoctruyen.util.destroyLoadingDialog

class AddChapActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddChapBinding
    private val listUriImage: MutableList<String> = mutableListOf()

    private lateinit var adapter: AddChapAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddChapBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
        setOnClick()
    }

    private val imageChooser =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                listUriImage.add(uri.toString())
            }
            adapter.submitList(listUriImage)
            adapter.notifyDataSetChanged()
        }

    private fun initView() {
        adapter = AddChapAdapter(onClickEdit = {}, onClickDelete = {})
        binding.rvAddChap.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvAddChap.adapter = adapter
        val abc = listOf<String>("content://com.android.providers.media.documents/document/image%3A1000005848","content://com.android.providers.media.documents/document/image%3A1000005848")
        adapter.submitList(abc)
    }

    private fun setOnClick() {
        binding.floatingAdd.setOnClickListener {
            if (hasStoragePermissionImg()) {
                imageChooser.launch("image/*")
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
        binding.imgBack.setOnClickListener {
            finish()
        }
    }

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