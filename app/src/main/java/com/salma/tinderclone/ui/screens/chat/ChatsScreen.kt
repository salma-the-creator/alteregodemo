package com.salma.tinderclone.ui.screens.chat

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.auth.FirebaseAuth
import com.salma.tinderclone.viewmodel.ChatViewModel
import com.salma.tinderclone.viewmodel.Message
import java.text.SimpleDateFormat
import java.util.*

// Colors
private val BlueAccent = Color(0xFF9C27B0)
private val GrayLight = Color(0xFFF2F2F7)
private val GrayText = Color(0xFF8E8E93)
private val MessageBubbleReceived = Color(0xFFF2F2F7)
private val MessageBubbleSent = Color(0xFFAA8BE0)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    matchId: String,
    viewModel: ChatViewModel,
    onBack: () -> Unit
) {
    val messages = viewModel.messages
    val isLoading = viewModel.isLoading
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    LaunchedEffect(matchId) {
        viewModel.loadMessages(matchId)
    }

    var messageText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    Scaffold(
        containerColor = Color.White,
        topBar = {
            ChatTopBar(
                userName = "Chat",
                userPhoto = "",
                isOnline = true,
                onBack = onBack
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color.White)
        ) {
            if (isLoading && messages.isEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = BlueAccent)
                }
            } else {
                // Messages List
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    state = listState,
                    reverseLayout = false
                ) {
                    items(messages) { message ->
                        MessageBubble(
                            message = message,
                            isFromCurrentUser = message.senderId == currentUserId
                        )
                    }
                }

                LaunchedEffect(messages.size) {
                    if (messages.isNotEmpty()) {
                        listState.animateScrollToItem(messages.size - 1)
                    }
                }
            }

            // Message Input
            MessageInputBar(
                messageText = messageText,
                onMessageChange = { messageText = it },
                onSend = {
                    if (messageText.isNotBlank()) {
                        viewModel.sendMessage(matchId, messageText)
                        messageText = ""
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChatTopBar(
    userName: String,
    userPhoto: String,
    isOnline: Boolean,
    onBack: () -> Unit
) {
    Surface(
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Back Button
            IconButton(onClick = onBack) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(BlueAccent),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // User Avatar
            Box {
                if (userPhoto.isNotEmpty()) {
                    Image(
                        painter = rememberAsyncImagePainter(userPhoto),
                        contentDescription = userName,
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(GrayLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Filled.Person,
                            contentDescription = null,
                            tint = GrayText,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                if (isOnline) {
                    Box(
                        modifier = Modifier
                            .size(14.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .padding(2.dp)
                            .align(Alignment.BottomEnd)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                                .background(Color(0xFF34C759))
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // User Name and Status
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = userName,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                )
                if (isOnline) {
                    Text(
                        text = "Active now",
                        fontSize = 13.sp,
                        color = Color(0xFF34C759)
                    )
                }
            }

            // Action Buttons
            IconButton(onClick = { /* Phone call */ }) {
                Icon(
                    Icons.Outlined.Phone,
                    contentDescription = "Call",
                    tint = BlueAccent
                )
            }
            IconButton(onClick = { /* Video call */ }) {
                Icon(
                    Icons.Outlined.Videocam,
                    contentDescription = "Video Call",
                    tint = BlueAccent
                )
            }
        }
    }
}

@Composable
private fun MessageBubble(
    message: Message,
    isFromCurrentUser: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isFromCurrentUser) Arrangement.End else Arrangement.Start
    ) {
        Column(
            horizontalAlignment = if (isFromCurrentUser) Alignment.End else Alignment.Start
        ) {
            Box(
                modifier = Modifier
                    .widthIn(max = 280.dp)
                    .clip(
                        RoundedCornerShape(
                            topStart = 20.dp,
                            topEnd = 20.dp,
                            bottomStart = if (isFromCurrentUser) 20.dp else 4.dp,
                            bottomEnd = if (isFromCurrentUser) 4.dp else 20.dp
                        )
                    )
                    .background(
                        if (isFromCurrentUser) MessageBubbleSent else MessageBubbleReceived
                    )
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Text(
                    text = message.text,
                    fontSize = 16.sp,
                    color = if (isFromCurrentUser) Color.White else Color.Black
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = formatTime(message.timestamp),
                fontSize = 11.sp,
                color = GrayText
            )
        }
    }
}

@Composable
private fun MessageInputBar(
    messageText: String,
    onMessageChange: (String) -> Unit,
    onSend: () -> Unit
) {
    Surface(
        color = Color.White,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Attachment Button
            IconButton(
                onClick = { /* Add attachment */ },
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    Icons.Outlined.Add,
                    contentDescription = "Add",
                    tint = BlueAccent,
                    modifier = Modifier.size(28.dp)
                )
            }

            // Text Field
            TextField(
                value = messageText,
                onValueChange = onMessageChange,
                placeholder = {
                    Text(
                        "Type a message...",
                        color = GrayText
                    )
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = GrayLight,
                    unfocusedContainerColor = GrayLight,
                    disabledContainerColor = GrayLight,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = BlueAccent
                ),
                shape = RoundedCornerShape(24.dp),
                singleLine = false,
                maxLines = 4,
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 44.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Send Button
            IconButton(
                onClick = onSend,
                enabled = messageText.isNotBlank(),
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(
                        if (messageText.isNotBlank()) BlueAccent else GrayLight
                    )
            ) {
                Icon(
                    Icons.Filled.Send,
                    contentDescription = "Send",
                    tint = if (messageText.isNotBlank()) Color.White else GrayText,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

private fun formatTime(timestamp: Long): String {
    if (timestamp == 0L) return ""
    val date = Date(timestamp)
    return SimpleDateFormat("h:mm a", Locale.getDefault()).format(date)
}