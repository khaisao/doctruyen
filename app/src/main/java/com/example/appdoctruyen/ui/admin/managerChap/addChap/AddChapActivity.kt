package com.example.appdoctruyen.ui.admin.managerChap.addChap

import android.Manifest
import android.app.Dialog
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.appdoctruyen.databinding.ActivityAddChapBinding
import com.example.appdoctruyen.databinding.DialogTlBinding
import com.example.appdoctruyen.model.Chap
import com.example.appdoctruyen.util.CollectionName
import com.example.appdoctruyen.util.StorageRef
import com.example.appdoctruyen.util.destroyLoadingDialog
import com.example.appdoctruyen.util.hiddenLoading
import com.example.appdoctruyen.util.showLoading
import com.example.appdoctruyen.util.toastMessage
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.storage

class AddChapActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddChapBinding
    private val listUriImage: MutableList<String> = mutableListOf()

    private lateinit var adapter: AddChapAdapter

    private var comicId: String? = null

    private val storage = Firebase.storage

    private val db = FirebaseFirestore.getInstance()

    private var chapUpload: Chap? = null

    private var isEditChap = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddChapBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
        setOnClick()
    }

    private val imageChooserAdd =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                listUriImage.add(uri.toString())
            }
            adapter.submitList(listUriImage)
            adapter.notifyDataSetChanged()
        }

    private var urlToEdit = ""

    private val imageChooserEdit =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                for (i in 0 until listUriImage.size) {
                    if (listUriImage[i] == urlToEdit) {
                        listUriImage[i] = uri.toString()
                        break
                    }
                }
            }
            adapter.submitList(listUriImage)
            adapter.notifyDataSetChanged()
        }


    private fun initView() {
        comicId = intent.getStringExtra("ComicId")
        adapter = AddChapAdapter(
            onClickEdit = {
                urlToEdit = it
                imageChooserEdit.launch("image/*")
            },
            onClickDelete = {
                listUriImage.remove(it)
                adapter.submitList(listUriImage)
                adapter.notifyDataSetChanged()
            })
        binding.rvAddChap.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvAddChap.adapter = adapter
        val chap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("Chap", Chap::class.java)
        } else {
            intent.getParcelableExtra("Chap")
        }
        if (chap != null) {
            chapUpload = chap
            setUpUiForEditChap(chap)
            isEditChap = true
        }
    }

    private fun setUpUiForEditChap(chap: Chap) {
        binding.textTieude.text = "Sửa chap truyện"
        listUriImage.addAll(chap.listImage)
        adapter.submitList(listUriImage)
        adapter.notifyDataSetChanged()
    }

    private fun setOnClick() {
        binding.floatingAdd.setOnClickListener {
            if (hasStoragePermissionImg()) {
                imageChooserAdd.launch("image/*")
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
        binding.textLuu.setOnClickListener {
            if (!isEditChap) {
                showDialogAddChap()
            } else {
                showConfirmEditDialog()
            }
        }
    }

    private fun showConfirmEditDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Bạn có muốn sửa chap này không!")
            .setPositiveButton("Sửa") { dialog, id ->
                if (listUriImage.isEmpty()) {
                    toastMessage("Vui lòng thêm ảnh")
                } else {
                    if (isAllFirebaseStorageUrl(listUriImage)) {
                        val chapEdit = Chap(
                            id = chapUpload?.id ?: "",
                            name = chapUpload?.name ?: "",
                            comicId = chapUpload?.comicId ?: "",
                            createAt = chapUpload?.createAt ?: System.currentTimeMillis(),
                            listImage = listUriImage
                        )
                        editChapToDb(chapEdit)
                    } else {
                        for (item in listUriImage) {
                            if (!isFirebaseStorageUrl(item)) {
                                uploadAndReplaceUrl(item, "")
                            }
                        }
                    }
                }
            }
            .setNegativeButton(
                "Hủy"
            ) { dialog, id -> }

        builder.create()
        builder.show()
    }

    private fun uploadAndReplaceUrl(uriStringFileUpload: String, chapName: String) {
        showLoading()
        val storageRef = storage.reference
        val fileRef =
            storageRef.child("${StorageRef.CHAP_IMAGE}/${System.currentTimeMillis()}.jpg")
        val uploadTask = fileRef.putFile(Uri.parse(uriStringFileUpload))
        uploadTask.addOnFailureListener {
            toastMessage("Có lỗi: ${it.message}")
            hiddenLoading()
        }.addOnSuccessListener { taskSnapshot ->
            val downloadUrlTask = taskSnapshot.storage.downloadUrl
            downloadUrlTask.addOnSuccessListener { uri ->
                val downloadUrl = uri.toString()
                for (i in 0 until listUriImage.size) {
                    if (listUriImage[i] == uriStringFileUpload) {
                        listUriImage[i] = downloadUrl
                    }
                }

                if (isAllFirebaseStorageUrl(listUriImage) && !isEditChap) {
                    addChapToDb(chapName)
                }
                if (isAllFirebaseStorageUrl(listUriImage) && isEditChap) {
                    chapUpload?.let {
                        editChapToDb(it)
                    }
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
    }

    //Kiểm tra xem tên chap đã tồn tại chưa
    private fun isExitChapName(
        chapName: String,
    ): Boolean {
        var isExit = false
        val currentListChapName = intent.getStringArrayListExtra("CurrentChapName")
        if (currentListChapName != null) {
            for (item in currentListChapName) {
                if (item == chapName) {
                    isExit = true
                }
            }
        }
        return isExit
    }

    //Thêm chap vào database
    private fun addChapToDb(chapName: String) {
        val document = db.collection(CollectionName.CHAP).document()
        val chap = Chap(
            id = document.id,
            name = chapName,
            comicId = comicId ?: "",
            createAt = System.currentTimeMillis(),
            listImage = listUriImage
        )
        document.set(chap)
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

    //Sửa chap  truyện
    private fun editChapToDb(chap: Chap) {
        showLoading()
        val document = db.collection(CollectionName.CHAP).document(chap.id)
        val chap = Chap(
            id = chap.id,
            name = chap.name,
            comicId = chap.comicId,
            createAt = chap.createAt,
            listImage = listUriImage
        )
        document.set(chap)
            .addOnSuccessListener {
                toastMessage("Sửa thành công!")
                hiddenLoading()
                finish()
            }
            .addOnFailureListener { e ->
                toastMessage("Error: ${e.message}")
                hiddenLoading()
            }
            .addOnCanceledListener {
                hiddenLoading()
            }
    }

    private fun showDialogAddChap() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val addChapDialogBinding = DialogTlBinding.inflate(dialog.layoutInflater)
        dialog.setContentView(addChapDialogBinding.root)
        val window = dialog.window ?: return
        window.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val layoutParams = window.attributes
        window.attributes = layoutParams
        dialog.setCancelable(true)
        addChapDialogBinding.textView.text = "Lưu chap truyện"
        addChapDialogBinding.editAddtl.hint = "Nhập tên chap"
        addChapDialogBinding.buttonThem.setOnClickListener {
            val chapName = addChapDialogBinding.editAddtl.text.toString().trim { it <= ' ' }
            if (chapName.isEmpty()) {
                toastMessage("Vui lòng nhập tên chap!")
            } else {
                if (listUriImage.isEmpty()) {
                    toastMessage("Vui lòng thêm ảnh")
                } else {
                    if (!isExitChapName(chapName)) {
                        for (item in listUriImage) {
                            if (!isFirebaseStorageUrl(item)) {
                                uploadAndReplaceUrl(item, chapName)
                            }
                        }
                    } else {
                        toastMessage("Tên chap đã tồn tại")
                    }
                }
            }
        }
        addChapDialogBinding.buttonHuy.setOnClickListener { dialog.dismiss() }
        dialog.show()
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

    //Kiểm tra xem text có phải là đường link của firebase storage chưa
    private fun isFirebaseStorageUrl(url: String): Boolean {
        return url.contains("firebasestorage.googleapis.com")
    }

    //Trả về true nếu tất cả string của 1 list toàn là link của firestorage
    private fun isAllFirebaseStorageUrl(listImage: List<String>): Boolean {
        var isAllValueFirebaseUrl = true
        for (item in listImage) {
            if (!isFirebaseStorageUrl(item)) {
                isAllValueFirebaseUrl = false
            }
        }
        return isAllValueFirebaseUrl
    }
}