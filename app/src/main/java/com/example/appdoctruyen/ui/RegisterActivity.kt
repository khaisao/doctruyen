package com.example.appdoctruyen.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.appdoctruyen.databinding.ActivityRegisterBinding
import com.example.appdoctruyen.model.UserRole
import com.example.appdoctruyen.util.CollectionName
import com.example.appdoctruyen.util.destroyLoadingDialog
import com.example.appdoctruyen.util.hiddenLoading
import com.example.appdoctruyen.util.showLoading
import com.example.appdoctruyen.util.toastMessage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private var auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        setOnClick()
    }

    private fun setOnClick() {
        binding.buttonDangKy.setOnClickListener {
            val userName = binding.editHoTen.text.toString()
            val email = binding.editTaikhoan.text.toString()
            val password = binding.editMatKhau.text.toString()
            registerUser(userName, email, password)
        }
        binding.textDangNhap.setOnClickListener {
            finish()
        }
    }

    //Đăng ký vào firebase auth
    private fun registerUser(userName: String?, email: String?, password: String?) {
        if (userName.isNullOrBlank()) {
            toastMessage("Username rỗng")
            return
        }
        if (email.isNullOrBlank()) {
            toastMessage("Email rỗng")
            return
        }
        if (password.isNullOrBlank()) {
            toastMessage("Password rỗng")
            return
        }
        showLoading()
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {

                    //Set tên cho tài khoản
                    val userProfileChangeRequest = UserProfileChangeRequest.Builder()
                        .setDisplayName(userName)
                        .build()
                    auth.currentUser?.updateProfile(userProfileChangeRequest)
                        ?.addOnCompleteListener {
                            saveUserToDb(userName, email, password, UserRole.User.role)
//                            Đăng ký với quyền Admin
//                            saveUserToDb(userName, email, password, UserRole.Admin.role)
                        }?.addOnCanceledListener {
                            toastMessage("Lỗi: ${task.exception?.message}")
                            hiddenLoading()
                        }
                } else {
                    toastMessage("Lỗi: ${task.exception?.message}")
                    hiddenLoading()
                }
            }.addOnFailureListener {
                toastMessage("Lỗi: ${it.message}")
                hiddenLoading()
            }
    }

    //Lưu tài khoản vào database
    private fun saveUserToDb(userName: String?, email: String?, password: String?, role: Int) {
        val userData = hashMapOf(
            "userName" to userName,
            "email" to email,
            "password" to password,
            "role" to role
        )
        auth.currentUser?.let {
            db.collection(CollectionName.USER).document(it.uid)
                .set(userData)
                .addOnSuccessListener {
                    toastMessage("Đăng ký thành công")
                    hiddenLoading()
                    finish()
                }
                .addOnFailureListener { e ->
                    toastMessage("Lỗi: ${e.message}")
                }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        destroyLoadingDialog()
    }
}