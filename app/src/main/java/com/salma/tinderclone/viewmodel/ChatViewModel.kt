package com.salma.tinderclone.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class Chat(
    val matchId: String = "",
    val userId: String = "",
    val userName: String = "",
    val userPhoto: String = "",
    val lastMessage: String = "",
    val timestamp: Long = 0L
)

data class Message(
    val id: String = "",
    val senderId: String = "",
    val text: String = "",
    val timestamp: Long = 0L
)

class ChatViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    var chats by mutableStateOf<List<Chat>>(emptyList())
    var messages by mutableStateOf<List<Message>>(emptyList())
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    init {
        loadChats()
    }

    fun loadChats() {
        val currentUserId = auth.currentUser?.uid ?: return
        isLoading = true
        errorMessage = null

        viewModelScope.launch {
            try {
                val matchesQuery = firestore.collection("matches")
                    .whereArrayContains("users", currentUserId)
                    .get()
                    .await()

                val chatList = mutableListOf<Chat>()
                for (matchDoc in matchesQuery.documents) {
                    val users = matchDoc.get("users") as? List<*> ?: continue
                    val otherUserId = users.firstOrNull { it != currentUserId } as? String ?: continue
                    
                    val otherUserDoc = firestore.collection("users").document(otherUserId).get().await()
                    val otherUser = otherUserDoc.toObject(com.salma.tinderclone.data.model.User::class.java)
                    
                    if (otherUser != null) {
                        val lastMessageQuery = firestore.collection("matches")
                            .document(matchDoc.id)
                            .collection("messages")
                            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                            .limit(1)
                            .get()
                            .await()

                        val lastMessage = lastMessageQuery.documents.firstOrNull()?.getString("text") ?: ""
                        val timestamp = lastMessageQuery.documents.firstOrNull()?.getLong("timestamp") ?: 0L

                        chatList.add(
                            Chat(
                                matchId = matchDoc.id,
                                userId = otherUserId,
                                userName = otherUser.name,
                                userPhoto = otherUser.photos.firstOrNull() ?: "",
                                lastMessage = lastMessage,
                                timestamp = timestamp
                            )
                        )
                    }
                }

                chats = chatList.sortedByDescending { it.timestamp }
                isLoading = false
            } catch (e: Exception) {
                errorMessage = e.message ?: "Failed to load chats"
                isLoading = false
            }
        }
    }

    fun loadMessages(matchId: String) {
        isLoading = true
        errorMessage = null

        viewModelScope.launch {
            try {
                val messagesQuery = firestore.collection("matches")
                    .document(matchId)
                    .collection("messages")
                    .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.ASCENDING)
                    .get()
                    .await()

                messages = messagesQuery.documents.mapNotNull { doc ->
                    Message(
                        id = doc.id,
                        senderId = doc.getString("senderId") ?: "",
                        text = doc.getString("text") ?: "",
                        timestamp = doc.getLong("timestamp") ?: 0L
                    )
                }
                isLoading = false
            } catch (e: Exception) {
                errorMessage = e.message ?: "Failed to load messages"
                isLoading = false
            }
        }
    }

    fun sendMessage(matchId: String, text: String) {
        val currentUserId = auth.currentUser?.uid ?: return
        if (text.isBlank()) return

        val messageData = hashMapOf(
            "senderId" to currentUserId,
            "text" to text,
            "timestamp" to FieldValue.serverTimestamp()
        )

        firestore.collection("matches")
            .document(matchId)
            .collection("messages")
            .add(messageData)
            .addOnSuccessListener {
                loadMessages(matchId)
            }
            .addOnFailureListener { e ->
                errorMessage = "Failed to send message: ${e.message}"
            }
    }
}



