package com.example.appdoctruyen.ui.admin.managerCategory

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.appdoctruyen.databinding.ActivityManagerCategoryBinding
import com.example.appdoctruyen.databinding.DialogTlBinding
import com.example.appdoctruyen.model.Category
import com.example.appdoctruyen.util.CollectionName
import com.example.appdoctruyen.util.destroyLoadingDialog
import com.example.appdoctruyen.util.hiddenLoading
import com.example.appdoctruyen.util.showLoading
import com.example.appdoctruyen.util.toastMessage
import com.google.firebase.firestore.FirebaseFirestore

class AllCategoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityManagerCategoryBinding

    private val db = FirebaseFirestore.getInstance()

    private lateinit var addCategoryDialog: Dialog
    private lateinit var editCategoryDialog: Dialog

    private lateinit var adapter: AllCategoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManagerCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
        setOnClick()
    }

    private fun initView() {
        adapter =
            AllCategoryAdapter(
                onClickEdit = { showDialogEditCategory(it) },
                onClickDelete = { deleteCategory(it) })
        binding.rvCategory.adapter = adapter
        getAllCategory()
    }

    private fun setOnClick() {
        binding.imgBack.setOnClickListener {
            finish()
        }
        binding.floatingAdd.setOnClickListener {
            showDialogAddCategory()
        }
    }

    private fun showDialogAddCategory() {
        addCategoryDialog = Dialog(this)
        addCategoryDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val dialogAddCategoryBinding = DialogTlBinding.inflate(addCategoryDialog.layoutInflater)
        addCategoryDialog.setContentView(dialogAddCategoryBinding.root)
        val window = addCategoryDialog.window ?: return
        window.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val layoutParams = window.attributes
        window.attributes = layoutParams
        addCategoryDialog.setCancelable(true)
        dialogAddCategoryBinding.textView.text = "Thêm thể loại"
        dialogAddCategoryBinding.buttonThem.setOnClickListener {
            val categoryName = dialogAddCategoryBinding.editAddtl.text.toString().trim { it <= ' ' }
            if (categoryName.isEmpty()) {
                toastMessage("Vui lòng nhập tên thể loại!")
            } else {
                saveCategoryToDb(categoryName)
            }
        }
        dialogAddCategoryBinding.buttonHuy.setOnClickListener { addCategoryDialog.dismiss() }
        addCategoryDialog.show()
    }

    private fun showDialogEditCategory(category: Category) {
        editCategoryDialog = Dialog(this)
        editCategoryDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val dialogEditCategoryBinding = DialogTlBinding.inflate(editCategoryDialog.layoutInflater)
        editCategoryDialog.setContentView(dialogEditCategoryBinding.root)
        val window = editCategoryDialog.window ?: return
        window.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val layoutParams = window.attributes
        window.attributes = layoutParams
        editCategoryDialog.setCancelable(true)
        dialogEditCategoryBinding.textView.text = "Sửa thể loại"
        dialogEditCategoryBinding.buttonThem.text = "Sửa"
        dialogEditCategoryBinding.editAddtl.setText(category.name)
        dialogEditCategoryBinding.buttonThem.setOnClickListener {
            val newCategoryName =
                dialogEditCategoryBinding.editAddtl.text.toString().trim { it <= ' ' }
            if (newCategoryName.isEmpty()) {
                toastMessage("Vui lòng nhập tên thể loại!")
            } else if (newCategoryName == category.name) {
                editCategoryDialog.dismiss()
            } else {
                editCategoryToDb(newCategoryName, category)
            }
        }
        dialogEditCategoryBinding.buttonHuy.setOnClickListener { editCategoryDialog.dismiss() }
        editCategoryDialog.show()
    }

    private fun editCategoryToDb(newCategoryName: String, category: Category) {
        showLoading()
        checkCategoryExit(
            newCategoryName,
            isCategoryExit = {
                if (it) {
                    toastMessage("Tên thể loại đã tồn tại!")
                    hiddenLoading()
                } else {
                    val document = db.collection(CollectionName.CATEGORY).document(category.id)
                    val category = Category(id = document.id, name = newCategoryName)
                    document.set(category)
                        .addOnSuccessListener {
                            toastMessage("Sửa thành công!")
                            hiddenLoading()
                            if (this::editCategoryDialog.isInitialized) {
                                editCategoryDialog.dismiss()
                            }
                        }
                        .addOnFailureListener { e ->
                            toastMessage("Error: ${e.message}")
                            hiddenLoading()
                        }
                }
            },
        )
    }

    private fun saveCategoryToDb(categoryName: String) {
        showLoading()
        checkCategoryExit(
            categoryName,
            isCategoryExit = {
                if (it) {
                    toastMessage("Tên thể loại đã tồn tại!")
                    hiddenLoading()
                } else {
                    val document = db.collection(CollectionName.CATEGORY).document()
                    val category = Category(id = document.id, name = categoryName)
                    document.set(category)
                        .addOnSuccessListener {
                            toastMessage("Thêm thành công!")
                            hiddenLoading()
                            if (this::addCategoryDialog.isInitialized) {
                                addCategoryDialog.dismiss()
                            }
                        }
                        .addOnFailureListener { e ->
                            toastMessage("Error: ${e.message}")
                            hiddenLoading()
                        }
                }
            },
        )
    }

    private fun deleteCategory(category: Category) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Bạn có muốn xóa thể loại này không!")
            .setPositiveButton("Xóa") { dialog, id ->
                showLoading()
                db.collection(CollectionName.CATEGORY).document(category.id)
                    .delete()
                    .addOnSuccessListener {
                        toastMessage("Xóa thành công")
                        hiddenLoading()
                    }
                    .addOnFailureListener {
                        toastMessage("Xóa thất bại")
                        hiddenLoading()
                    }
            }
            .setNegativeButton(
                "Hủy"
            ) { dialog, id -> }
        builder.create()
        builder.show()
    }

    private fun checkCategoryExit(categoryName: String, isCategoryExit: (Boolean) -> Unit) {
        val categoryCollection = db.collection(CollectionName.CATEGORY)
        val query = categoryCollection.whereEqualTo("name", categoryName)
        query.get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val querySnapshot = task.result
                    val foundCategories = querySnapshot?.size() ?: 0
                    val categoryExists = foundCategories > 0
                    isCategoryExit(categoryExists)
                } else {
                    isCategoryExit(true)
                }
            }
            .addOnFailureListener { e ->
                isCategoryExit.invoke(true)
                toastMessage("Error: ${e.message}")
                hiddenLoading()
            }
    }

    private fun getAllCategory() {
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
                adapter.submitList(listCategory)
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        destroyLoadingDialog()
    }
}