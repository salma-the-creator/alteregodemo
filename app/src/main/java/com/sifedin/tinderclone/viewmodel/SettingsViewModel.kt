package com.sifedin.tinderclone.viewmodel

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.sifedin.tinderclone.data.model.User
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class SettingsViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    var currentUser by mutableStateOf<User?>(null)
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    var name by mutableStateOf("")
    var gender by mutableStateOf<String?>(null)
    var birthday by mutableStateOf<String?>(null)
    var interestedIn by mutableStateOf<String?>(null)
    var photoUris by mutableStateOf<List<Uri>>(emptyList())

    init {
        loadCurrentUser()
    }

    fun loadCurrentUser() {
        val currentUserId = auth.currentUser?.uid ?: return
        isLoading = true
        errorMessage = null

        viewModelScope.launch {
            try {
                val userDoc = firestore.collection("users").document(currentUserId).get().await()
                val user = userDoc.toObject(User::class.java)?.copy(uid = currentUserId)
                
                if (user != null) {
                    currentUser = user
                    name = user.name
                    gender = user.gender
                    birthday = user.birthday
                    interestedIn = user.interestedin
                }
                isLoading = false
            } catch (e: Exception) {
                errorMessage = e.message ?: "Failed to load user profile"
                isLoading = false
            }
        }
    }

    fun updateProfile(onSuccess: () -> Unit) {
        val currentUserId = auth.currentUser?.uid ?: return
        if (name.isBlank()) {
            errorMessage = "Name cannot be empty"
            return
        }

        isLoading = true
        errorMessage = null

        viewModelScope.launch {
            try {
                val updateData = hashMapOf<String, Any>(
                    "name" to name
                )

                gender?.let { updateData["gender"] = it }
                birthday?.let { updateData["birthday"] = it }
                interestedIn?.let { updateData["interestedin"] = it }

                firestore.collection("users")
                    .document(currentUserId)
                    .update(updateData)
                    .await()

                isLoading = false
                onSuccess()
            } catch (e: Exception) {
                errorMessage = e.message ?: "Failed to update profile"
                isLoading = false
            }
        }
    }

    fun deleteAccount(onSuccess: () -> Unit) {
        val currentUserId = auth.currentUser?.uid ?: return
        isLoading = true
        errorMessage = null

        viewModelScope.launch {
            try {
                // Delete user data from Firestore
                firestore.collection("users").document(currentUserId).delete().await()
                
                // Delete user account from Firebase Auth
                auth.currentUser?.delete()?.await()

                isLoading = false
                onSuccess()
            } catch (e: Exception) {
                errorMessage = e.message ?: "Failed to delete account"
                isLoading = false
            }
        }
    }

    fun logout() {
        auth.signOut()
    }
}



