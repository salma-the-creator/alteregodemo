package com.salma.tinderclone.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.salma.tinderclone.data.model.User
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class LikesViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    var likedUsers by mutableStateOf<List<User>>(emptyList())
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    init {
        loadLikedUsers()
    }

    fun loadLikedUsers() {
        val currentUserId = auth.currentUser?.uid ?: return
        isLoading = true
        errorMessage = null

        viewModelScope.launch {
            try {
                val swipesQuery = firestore.collection("swipes")
                    .whereEqualTo("swiperId", currentUserId)
                    .whereEqualTo("type", "like")
                    .get()
                    .await()

                val userIds = swipesQuery.documents.mapNotNull { it.getString("targetId") }
                
                if (userIds.isEmpty()) {
                    likedUsers = emptyList()
                    isLoading = false
                    return@launch
                }

                val usersList = mutableListOf<User>()
                for (userId in userIds) {
                    val userDoc = firestore.collection("users").document(userId).get().await()
                    val user = userDoc.toObject(User::class.java)?.copy(uid = userId)
                    if (user != null) {
                        usersList.add(user)
                    }
                }

                likedUsers = usersList
                isLoading = false
            } catch (e: Exception) {
                errorMessage = e.message ?: "Failed to load liked users"
                isLoading = false
            }
        }
    }
}



