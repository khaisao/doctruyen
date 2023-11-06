package com.example.appdoctruyen.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Chap(
    val id: String = "",
    val name: String = "",
    val comicId: String = "",
    val createAt: Long = 0,
    val listImage: List<String> = emptyList()
) : Parcelable
