package com.example.appdoctruyen.util

import android.app.Activity
import android.content.Intent
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Parcelable
import android.widget.Toast

fun Activity.toastMessage(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Activity.showLoading(){
    val abc = LoadingDialog.getInstance(this)
    if(abc != null){
        abc.show()
    } else{
        toastMessage("Null r")
    }
}

fun Activity.hiddenLoading(){
    LoadingDialog.getInstance(this)?.hidden()
}

fun Activity.destroyLoadingDialog(){
    LoadingDialog.getInstance(this)?.destroyLoadingDialog()
}

inline fun <reified T : Parcelable> Intent.parcelable(key: String): T? = when {
    SDK_INT >= 33 -> getParcelableExtra(key, T::class.java)
    else -> @Suppress("DEPRECATION") getParcelableExtra(key) as? T
}

inline fun <reified T : Parcelable> Bundle.parcelable(key: String): T? = when {
    SDK_INT >= 33 -> getParcelable(key, T::class.java)
    else -> @Suppress("DEPRECATION") getParcelable(key) as? T
}