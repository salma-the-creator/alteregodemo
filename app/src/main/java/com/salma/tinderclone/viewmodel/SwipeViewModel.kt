package com.salma.tinderclone.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue
import com.salma.tinderclone.data.model.User
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class SwipeViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    var potentialMatches by mutableStateOf<List<User>>(emptyList())
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    // توقيع جديد لدالة التنقل بعد المطابقة
    var onMatchFound: ((matchedUser: User, matchId: String) -> Unit)? = null

    init {
        loadPotentialMatches()
    }

    private fun loadPotentialMatches() {
        val currentUserId = auth.currentUser?.uid ?: return
        isLoading = true
        errorMessage = null

        viewModelScope.launch {
            try {
                val currentUserDoc = firestore.collection("users").document(currentUserId).get().await()
                val currentUser = currentUserDoc.toObject(User::class.java)

                if (currentUser == null) {
                    errorMessage = "Could not load current user profile."
                    isLoading = false
                    return@launch
                }

                val query = firestore.collection("users")
                    .whereEqualTo("gender", currentUser.interestedin)
                    .get()
                    .await()

                val filteredUserList = query.documents.mapNotNull { doc ->
                    doc.toObject(User::class.java)
                }.filter { it.uid != currentUserId }

                potentialMatches = filteredUserList
                isLoading = false

            } catch (e: Exception) {
                errorMessage = e.message ?: "Failed to fetch matches"
                isLoading = false
                Log.e("SwipeViewModel", "Error fetching matches", e)
            }
        }
    }

    fun recordSwipe(targetUserId: String, isLiked: Boolean) {
        val currentUserId = auth.currentUser?.uid ?: return
        val swipeData = hashMapOf(
            "swiperId" to currentUserId,
            "targetId" to targetUserId,
            "type" to if (isLiked) "like" else "pass",
            "timestamp" to FieldValue.serverTimestamp()
        )

        firestore.collection("swipes").add(swipeData)
            .addOnSuccessListener {
                potentialMatches = potentialMatches.filter { it.uid != targetUserId }

                if (isLiked) {
                    checkForMatch(currentUserId, targetUserId)
                }
            }
            .addOnFailureListener { e ->
                errorMessage = "Swipe failed: ${e.message}"
            }
    }

    private fun checkForMatch(currentUserId: String, targetUserId: String) {
        firestore.collection("swipes")
            .whereEqualTo("swiperId", targetUserId)
            .whereEqualTo("targetId", currentUserId)
            .whereEqualTo("type", "like")
            .limit(1)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    viewModelScope.launch {
                        try {
                            val matchedUserDoc = firestore.collection("users").document(targetUserId).get().await()
                            val matchedUser = matchedUserDoc.toObject(User::class.java)
                            if (matchedUser != null) {
                                createMatch(currentUserId, matchedUser)
                            }
                        } catch (e: Exception) {
                            Log.e("SwipeVM", "Failed to get matched user data: $e")
                        }
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.e("SwipeVM", "Match check failed: ${e.message}", e)
            }
    }

    private fun createMatch(user1Id: String, matchedUser: User) {
        val user2Id = matchedUser.uid
        val matchData = hashMapOf(
            "users" to listOf(user1Id, user2Id).sorted(),
            "createdAt" to FieldValue.serverTimestamp()
        )

        firestore.collection("matches").add(matchData)
            .addOnSuccessListener { docRef ->
                Log.d("SwipeViewModel", "New Match Created: ${docRef.id}")

                onMatchFound?.invoke(matchedUser, docRef.id)
            }
            .addOnFailureListener { e ->
                errorMessage = "Match creation failed: ${e.message}"
            }
    }
}