package com.salma.tinderclone.data.model

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class User(
    val uid: String = "",
    val name: String = "",
    val gender: String = "",
    val birthday: String = "",
    val interestedin: String = "",
    val phoneNumber: String = "",
    val photos: List<String> = emptyList(),
    @ServerTimestamp
    val createdAt: Date? = null
) {
    fun getAge(): Int {
        return 25
    }
}