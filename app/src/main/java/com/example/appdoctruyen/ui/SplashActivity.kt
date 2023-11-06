package com.example.appdoctruyen.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.appdoctruyen.R
import com.example.appdoctruyen.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {
    var charSequence: CharSequence? = null
    var index = 0
    var delay: Long = 200
    var handler = Handler()
    private lateinit var binding:  ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //Khởi tạo animation
        val animationImageLogo = AnimationUtils.loadAnimation(this, R.anim.top_wave)
        //Bắt đầu animation
        binding.imageLogo.animation = animationImageLogo
        //Khởi tạo animation
        val animationZoomIn = AnimationUtils.loadAnimation(this, R.anim.zoom_in)
        //Bắt đầu animation
        binding.animationViewLogo.animation = animationZoomIn
        animatText("NetTruyen")
        // Khởi tạo handler
        // Khởi tạo handler
        Handler().postDelayed({ //Chuyển hướng tớ trang DangNhapActivity
            startActivity(Intent(this, LoginActivity::class.java))
            //Kết thúc activity
            finish()
        }, 4000)


    }
    var runnable: Runnable = object : Runnable {
        override fun run() {
            //Khi được chạy
            //Set Text
            binding.textAppName.text = charSequence!!.subSequence(0, index++)
            //Kiểm tra tình trạng
            if (index <= charSequence!!.length) {
                handler.postDelayed(this, delay)
            }
        }
    }

    //Tạo phương pháp văn bản động
    fun animatText(cs: CharSequence) {
        //Set text
        charSequence = cs
        //Đặt lại index về 0;
        index = 0
        //Xóa văn bản
        binding.textAppName.text = ""
        //Xóa cuộc gọi lại
        handler.removeCallbacks(runnable)
        //Chayk handler
        handler.postDelayed(runnable, delay)
    }
}