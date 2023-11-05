package com.example.appdoctruyen.model

data class User(
    val userName: String = "",
    val role: Int = 0
)

enum class UserRole(val role: Int) {
    User(1),
    Admin(2)
}
