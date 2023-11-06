package com.example.appdoctruyen.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.appdoctruyen.R
import com.example.appdoctruyen.databinding.ActivityLoginBinding
import com.example.appdoctruyen.model.User
import com.example.appdoctruyen.model.UserRole
import com.example.appdoctruyen.ui.admin.AdminHomeActivity
import com.example.appdoctruyen.ui.user.home.HomeActivity
import com.example.appdoctruyen.util.CollectionName
import com.example.appdoctruyen.util.destroyLoadingDialog
import com.example.appdoctruyen.util.hiddenLoading
import com.example.appdoctruyen.util.showLoading
import com.example.appdoctruyen.util.toastMessage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.math.log

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private var auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setOnClick()
    }

    private fun setOnClick() {
        binding.textDangKy.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        binding.buttonDangNhap.setOnClickListener {
            val email = binding.editTaikhoan.text.toString()
            val password = binding.editMatKhau.text.toString()
            login(email, password)
        }
    }

    //Đăng nhập
    private fun login(email: String?, password: String?) {
        //Nếu rỗng thì thông báo
        if (email.isNullOrBlank()) {
            toastMessage("Email rỗnh")
            return
        }
        if (password.isNullOrBlank()) {
            toastMessage("Password rỗng")
            return
        }
        showLoading()
        // Dùng firebase auth để đăng nhập
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    if (auth.currentUser != null) {
                        val docRef =
                            db.collection(CollectionName.USER).document(auth.currentUser!!.uid)
                        docRef.get()
                            .addOnSuccessListener { document ->
                                if (document != null) {
                                    val user = document.toObject(User::class.java)
                                    if (user != null) {
                                        val role = user.role
                                        //Đăng nhập thành công, nếu quyền là Admin thì chuyển sang màn của Admin
                                        if (role == UserRole.Admin.role) {
                                            val intent = Intent(this, AdminHomeActivity::class.java)
                                            startActivity(intent)
                                            finish()
                                        }
                                        //Đăng nhập thành công, nếu quyền là User thì chuyển sang màn của User
                                        if (role == UserRole.User.role) {
                                            val intent = Intent(this, HomeActivity::class.java)
                                            startActivity(intent)
                                            finish()
                                        }
                                        hiddenLoading()
                                    }
                                } else {
                                    toastMessage("Tài khoản không tồn tại")
                                    hiddenLoading()
                                }
                            }
                            .addOnFailureListener { e ->
                                toastMessage("Lỗi: ${e.message}")
                                hiddenLoading()
                            }
                    } else {
                        toastMessage("Lỗi")
                        hiddenLoading()
                    }
                } else {
                    toastMessage("Lỗi: ${task.exception?.message}")
                    hiddenLoading()
                }
            }
            .addOnCanceledListener {
                toastMessage("Lỗi")
                hiddenLoading()
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        destroyLoadingDialog()
    }
}