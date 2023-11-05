package com.example.appdoctruyen.ui.admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.appdoctruyen.databinding.ActivityAdminHomeBinding
import com.example.appdoctruyen.ui.LoginActivity
import com.example.appdoctruyen.ui.admin.managerCategory.AllCategoryActivity
import com.example.appdoctruyen.ui.admin.managerComic.allComic.AllComicActivity
import com.example.appdoctruyen.util.destroyLoadingDialog
import com.google.firebase.auth.FirebaseAuth

class AdminHomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAdminHomeBinding
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setOnClick()
    }

    private fun setOnClick() {
        binding.buttonDangXuat.setOnClickListener {
            auth.signOut()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
        binding.cardViewQlTruyen.setOnClickListener {
            val intent = Intent(this, AllComicActivity::class.java)
            startActivity(intent)
        }
        binding.cardViewQlTl.setOnClickListener {
            val intent = Intent(this, AllCategoryActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        destroyLoadingDialog()
    }

}