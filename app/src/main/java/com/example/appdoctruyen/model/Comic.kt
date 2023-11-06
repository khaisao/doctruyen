package com.example.appdoctruyen.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Comic(
    var id: String = "",
    val name: String = "",
    val createAt: Long = 0,
    val status: Int = ComicStatus.NOT_SET.status,
    val categoryId: String = "",
    val introduce: String = "",
    var thumbnailUrl: String = "",
) : Parcelable

enum class ComicStatus(val status: Int) {
    NOT_SET(-1),
    IN_PROGRESS(0),
    DONE(1)
}