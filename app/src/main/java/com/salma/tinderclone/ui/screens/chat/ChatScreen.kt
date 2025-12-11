package com.salma.tinderclone.ui.screens.chat

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Videocam
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.salma.tinderclone.viewmodel.ChatViewModel
import java.text.SimpleDateFormat
import java.util.*

// Colors from design
private val BlueAccent = Color(0xFF673AB7)
private val GrayText = Color(0xFF8E8E93)
private val GrayLight = Color(0xFFF2F2F7)
private val GrayDivider = Color(0xFFE5E5EA)
private val RedBadge = Color(0xFFFF3B30)
private val GreenOnline = Color(0xFF34C759)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
     fun ChatsScreen(
    viewModel: ChatViewModel,
    onChatClick: (String, String) -> Unit
) {
    val chats = viewModel.chats
    val isLoading = viewModel.isLoading
    val error = viewModel.errorMessage

    var selectedTab by remember { mutableStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.loadChats()
    }

    Scaffold(
        containerColor = Color.White,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* New chat */ },
                containerColor = BlueAccent,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.size(56.dp)
            ) {
                Icon(
                    Icons.Filled.Add,
                    contentDescription = "New Chat",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color.White)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
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
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Chats",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }

                Icon(
                    Icons.Outlined.Videocam,
                    contentDescription = "Video",
                    tint = Color.Black,
                    modifier = Modifier.size(24.dp)
                )
            }

            // Search Bar
            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = {
                    Text(
                        "Search people and groups",
                        color = GrayText,
                        fontSize = 16.sp
                    )
                },
                leadingIcon = {
                    Icon(
                        Icons.Filled.Search,
                        contentDescription = "Search",
                        tint = GrayText
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
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(48.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Tabs: Chats, Groups, Communities
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                TabItem(
                    text = "Chats",
                    isSelected = selectedTab == 0,
                    onClick = { selectedTab = 0 }
                )
                Spacer(modifier = Modifier.width(32.dp))
                TabItem(
                    text = "Groups",
                    isSelected = selectedTab == 1,
                    onClick = { selectedTab = 1 }
                )
                Spacer(modifier = Modifier.width(32.dp))
                TabItem(
                    text = "Communities",
                    isSelected = selectedTab == 2,
                    onClick = { selectedTab = 2 }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Chat List
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = BlueAccent)
                }
            } else if (error != null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Error: $error", color = RedBadge)
                }
            } else if (chats.isEmpty()) {
                // Show sample data for demo
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(sampleChats) { chat ->
                        ChatListItem(
                            chat = chat,
                            onClick = { onChatClick(chat.id, chat.name) }
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(chats) { chat ->
                        ChatListItem(
                            chat = ChatItemData(
                                id = chat.matchId,
                                name = chat.userName,
                                message = chat.lastMessage,
                                time = formatTimestamp(chat.timestamp),
                                photoUrl = chat.userPhoto,
                                unreadCount = 0,
                                isOnline = false,
                                isVideoCall = false
                            ),
                            onClick = { onChatClick(chat.matchId, chat.userName) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TabItem(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier.clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            color = if (isSelected) Color.Black else GrayText
        )
        Spacer(modifier = Modifier.height(8.dp))
        if (isSelected) {
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(2.dp)
                    .background(Color.Black)
            )
        }
    }
}

@Composable
private fun ChatListItem(
    chat: ChatItemData,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar
        Box {
            Image(
                painter = rememberAsyncImagePainter(chat.photoUrl),
                contentDescription = chat.name,
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            // Online indicator
            if (chat.isOnline) {
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
                            .background(GreenOnline)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Name and Message
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = chat.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (chat.isVideoCall) {
                    Icon(
                        Icons.Outlined.Videocam,
                        contentDescription = null,
                        tint = BlueAccent,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Video call",
                        fontSize = 14.sp,
                        color = BlueAccent
                    )
                    Text(
                        text = " • In call",
                        fontSize = 14.sp,
                        color = GrayText
                    )
                } else {
                    Text(
                        text = chat.message,
                        fontSize = 14.sp,
                        color = GrayText,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Time and Badge
        Column(
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = chat.time,
                fontSize = 12.sp,
                color = GrayText
            )

            Spacer(modifier = Modifier.height(4.dp))

            when {
                chat.unreadCount > 0 -> {
                    // Red badge with count
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(RedBadge),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = chat.unreadCount.toString(),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
                chat.isOnline -> {
                    // Blue dot
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(BlueAccent)
                    )
                }
            }
        }
    }
}

// Data class for chat items
data class ChatItemData(
    val id: String,
    val name: String,
    val message: String,
    val time: String,
    val photoUrl: String,
    val unreadCount: Int = 0,
    val isOnline: Boolean = false,
    val isVideoCall: Boolean = false
)

// Sample data matching the design
private val sampleChats = listOf(
    ChatItemData(
        id = "1",
        name = "Emily Carter",
        message = "Hey, are we still on for tonight?",
        time = "9:15 AM",
        photoUrl = "https://randomuser.me/api/portraits/women/1.jpg",
        unreadCount = 1
    ),
    ChatItemData(
        id = "2",
        name = "Daniel Ross",
        message = "Just sent you the files.",
        time = "9:32 AM",
        photoUrl = "https://randomuser.me/api/portraits/men/2.jpg",
        unreadCount = 2
    ),
    ChatItemData(
        id = "3",
        name = "Sophie Miller",
        message = "",
        time = "Monday",
        photoUrl = "https://randomuser.me/api/portraits/women/3.jpg",
        isVideoCall = true,
        isOnline = true
    ),
    ChatItemData(
        id = "4",
        name = "James Lee",
        message = "Can you hop on a quick call?",
        time = "Sunday",
        photoUrl = "https://randomuser.me/api/portraits/men/4.jpg"
    ),
    ChatItemData(
        id = "5",
        name = "Olivia Brown",
        message = "See you at the gym later 💪",
        time = "Saturday",
        photoUrl = "https://randomuser.me/api/portraits/women/5.jpg"
    ),
    ChatItemData(
        id = "6",
        name = "Weekend Trip 🌴",
        message = "Sarah: Who's bringing snacks?",
        time = "Saturday",
        photoUrl = "https://randomuser.me/api/portraits/women/6.jpg"
    ),
    ChatItemData(
        id = "7",
        name = "Book Club",
        message = "Emma: Next week's read is \"Atomic Habits\"",
        time = "Saturday",
        photoUrl = "https://randomuser.me/api/portraits/women/7.jpg"
    ),
    ChatItemData(
        id = "8",
        name = "Michael Green",
        message = "On my way.",
        time = "Jun 12",
        photoUrl = "https://randomuser.me/api/portraits/men/8.jpg"
    ),
    ChatItemData(
        id = "9",
        name = "Hannah Wilson",
        message = "Did you check the new update?",
        time = "Jun 11",
        photoUrl = "https://randomuser.me/api/portraits/women/9.jpg"
    ),
    ChatItemData(
        id = "10",
        name = "Chris Taylor",
        message = "Happy Birthday! 🎉",
        time = "Jun 10",
        photoUrl = "https://randomuser.me/api/portraits/men/10.jpg"
    )
)

private fun formatTimestamp(timestamp: Long): String {
    if (timestamp == 0L) return ""
    val date = Date(timestamp)
    val now = Date()
    val diff = now.time - date.time
    val days = diff / (1000 * 60 * 60 * 24)

    return when {
        days == 0L -> SimpleDateFormat("h:mm a", Locale.getDefault()).format(date)
        days == 1L -> "Yesterday"
        days < 7L -> SimpleDateFormat("EEEE", Locale.getDefault()).format(date)
        else -> SimpleDateFormat("MMM d", Locale.getDefault()).format(date)
    }
}