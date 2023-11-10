package com.example.appdoctruyen.ui.admin.managerComic.addComic

import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.appdoctruyen.R
import com.example.appdoctruyen.databinding.ActivityAddComicBinding
import com.example.appdoctruyen.model.Category
import com.example.appdoctruyen.model.Comic
import com.example.appdoctruyen.model.ComicStatus
import com.example.appdoctruyen.util.CollectionName
import com.example.appdoctruyen.util.StorageRef
import com.example.appdoctruyen.util.destroyLoadingDialog
import com.example.appdoctruyen.util.hiddenLoading
import com.example.appdoctruyen.util.showLoading
import com.example.appdoctruyen.util.toastMessage
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.storage

class AddComicActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddComicBinding
    private val db = FirebaseFirestore.getInstance()
    private lateinit var adapter: AllCategorySpinnerAdapter
    private val storage = Firebase.storage

    private var imageUri: Uri? = null
    private var comicUpload: Comic? = null
    private var isEditComic = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddComicBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
        setOnClick()
    }

    //CHọn ảnh, nếu chọn thành công thì load vào imageViewAnh
    private val imageChooser =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                imageUri = uri
                Glide.with(this).load(imageUri).into(binding.imageViewAnh)
            }
        }

    private fun setOnClick() {
        binding.imageViewAnh.setOnClickListener {
            imageChooser.launch("image/*")
        }
        binding.buttonHuy.setOnClickListener {
            finish()
        }

        binding.buttonThem.setOnClickListener {
            val name = binding.editTextTenTruyen.text.toString()
            var status = ComicStatus.NOT_SET.status
            if (binding.radioButton.isChecked) {
                status = ComicStatus.IN_PROGRESS.status
            }
            if (binding.radioButton2.isChecked) {
                status = ComicStatus.DONE.status
            }
            val categorySelectedItem = binding.spinner.selectedItem as Category

            val categoryId = categorySelectedItem.id
            val introduce = binding.editTextGioiThieu.text.toString()
            comicUpload = Comic(
                id = comicUpload?.id ?: "",
                name = name,
                createAt = comicUpload?.createAt ?: System.currentTimeMillis(),
                status = status,
                categoryId = categoryId,
                introduce = introduce,
                thumbnailUrl = comicUpload?.thumbnailUrl ?: ""
            )
            //Nếu thông tin đã hợp lệ thì lưu vào database
            if (isValidComic(comicUpload!!)) {
                saveComicToDb(imageUri)
            } else {
                toastMessage("Vui lòng điền đủ thông tin")
            }
        }
    }

    //Lưu truyện vào database (lưu ảnh vào firestore, lưu thành công thì mới lưu tryêjn vào database)
    private fun saveComicToDb(uri: Uri?) {
        showLoading()
        if (!isEditComic) {
            if (uri != null) {
                val storageRef = storage.reference
                val fileRef =
                    storageRef.child("${StorageRef.COMIC_THUMBNAIL}/${System.currentTimeMillis()}.jpg")
                val uploadTask = fileRef.putFile(uri)
                uploadTask.addOnFailureListener {
                    toastMessage("Có lỗi: ${it.message}")
                    hiddenLoading()
                }.addOnSuccessListener { taskSnapshot ->
                    val downloadUrlTask = taskSnapshot.storage.downloadUrl
                    downloadUrlTask.addOnSuccessListener { uri ->
                        val downloadUrl = uri.toString()
                        comicUpload?.thumbnailUrl = downloadUrl
                        comicUpload?.let {
                            addComicToDb(it)
                        }
                    }
                        .addOnFailureListener {
                            toastMessage("Có lỗi: ${it.message}")
                            hiddenLoading()
                        }.addOnCanceledListener {
                            hiddenLoading()
                        }

                }.addOnCanceledListener {
                    hiddenLoading()
                }
            } else {
                hiddenLoading()
                toastMessage("Vui lòng điền đủ thông tin")
            }
        } else {
            if (uri != null) {
                val storageRef = storage.reference
                val fileRef =
                    storageRef.child("${StorageRef.COMIC_THUMBNAIL}/${System.currentTimeMillis()}.jpg")
                val uploadTask = fileRef.putFile(uri)
                uploadTask.addOnFailureListener {
                    toastMessage("Có lỗi: ${it.message}")
                    hiddenLoading()
                }.addOnSuccessListener { taskSnapshot ->
                    val downloadUrlTask = taskSnapshot.storage.downloadUrl
                    downloadUrlTask.addOnSuccessListener { uri ->
                        val downloadUrl = uri.toString()
                        comicUpload?.thumbnailUrl = downloadUrl
                        comicUpload?.let {
                            editComicToDb(it)
                        }
                    }
                        .addOnFailureListener {
                            toastMessage("Có lỗi: ${it.message}")
                            hiddenLoading()
                        }.addOnCanceledListener {
                            hiddenLoading()
                        }

                }.addOnCanceledListener {
                    hiddenLoading()
                }
            } else {
                comicUpload?.let { editComicToDb(it) }
            }
        }

    }

    //Lưu truyện vào database
    private fun addComicToDb(comicInput: Comic) {
        showLoading()
        val document = db.collection(CollectionName.COMIC).document()
        comicInput.id = document.id
        document.set(comicInput)
            .addOnSuccessListener {
                toastMessage("Thêm thành công!")
                hiddenLoading()
                finish()
            }
            .addOnFailureListener { e ->
                toastMessage("Error: ${e.message}")
                hiddenLoading()
            }
    }

    //Sửa truyện
    private fun editComicToDb(comicInput: Comic) {
        showLoading()
        val document = db.collection(CollectionName.COMIC).document(comicInput.id)
        document.set(comicInput)
            .addOnSuccessListener {
                toastMessage("Thêm thành công!")
                hiddenLoading()
                finish()
            }
            .addOnFailureListener { e ->
                toastMessage("Error: ${e.message}")
                hiddenLoading()
            }
    }

    //Kiểm tra xem đã nhập đầy đủ thông tin chưa
    private fun isValidComic(comic: Comic): Boolean {
        return comic.name.isNotEmpty() &&
                comic.status != ComicStatus.NOT_SET.status &&
                comic.categoryId.isNotEmpty() &&
                comic.introduce.isNotEmpty()
    }

    private fun initView() {
        val comic = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("Comic", Comic::class.java)
        } else {
            intent.getParcelableExtra("Comic")
        }
        if (comic != null) {
            comicUpload = comic
            setUpUiForEditComic(comic)
            isEditComic = true
        }

        getAllCategory()
    }

    //Nếu ấn vào sửa truyện từ màn trường, thì sẽ setup UI
    private fun setUpUiForEditComic(comic: Comic) {
        binding.ten.text = "Sửa truyện tranh"
        binding.buttonThem.text = "Sửa"
        Glide.with(this).load(comic.thumbnailUrl).into(binding.imageViewAnh)
        if (comic.status == ComicStatus.IN_PROGRESS.status) {
            binding.radioButton.isChecked = true
        } else {
            binding.radioButton2.isChecked = true
        }
        binding.editTextGioiThieu.setText(comic.introduce)
        binding.editTextTenTruyen.setText(comic.name)
        db.collection(CollectionName.CATEGORY).document(comic.categoryId).get()
            .addOnSuccessListener { document ->
                val category = document.toObject(Category::class.java)
                if (category != null) {
                    binding.spinner.prompt = category.name
                }
            }
    }

    //Lấy tất cả thể loại và hiển thị lên spinner
    private fun getAllCategory(): List<Category> {
        val categoryList = mutableListOf<Category>()
        showLoading()
        db.collection(CollectionName.CATEGORY)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result) {
                        val category = document.toObject(Category::class.java)
                        categoryList.add(category)
                    }
                    adapter = AllCategorySpinnerAdapter(
                        context = this,
                        layoutResource = R.layout.dropdown_item,
                        category = categoryList
                    )
                    binding.spinner.adapter = adapter
                    if (!isEditComic) {
                        binding.spinner.setSelection(0)
                    } else {
                        var index = 0
                        for (i in 0 until categoryList.size) {
                            if (categoryList[i].id == (comicUpload?.categoryId ?: "")) {
                                index = i
                                break
                            }
                        }
                        binding.spinner.setSelection(index)

                    }
                }
                hiddenLoading()
            }
            .addOnFailureListener {
                hiddenLoading()
            }
            .addOnCanceledListener {
                hiddenLoading()
            }

        return categoryList
    }

    override fun onDestroy() {
        super.onDestroy()
        destroyLoadingDialog()
    }
}