package com.keak.aishou.screens.friends

import aishou.composeapp.generated.resources.Res
import aishou.composeapp.generated.resources.profile_home
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.keak.aishou.components.NeoBrutalistCardViewWithFlexSize
import com.keak.aishou.components.NeoBrutalistCircleButton
import com.keak.aishou.misc.BackGroundBrush
import com.keak.aishou.navigation.Router
import com.keak.aishou.data.models.FriendInfo
import com.keak.aishou.data.models.FriendTag
import com.keak.aishou.utils.StringResources
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun FriendsScreen(router: Router, viewModel: FriendsViewModel = koinViewModel()) {
    val friendsList by viewModel.friendsList.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()
    val sendRequestLoading by viewModel.sendRequestLoading.collectAsStateWithLifecycle()
    val sendRequestResult by viewModel.sendRequestResult.collectAsStateWithLifecycle()

    var addFriendDisplayName by remember { mutableStateOf("") }
    var selectedTag by remember { mutableStateOf(FriendTag.FRIEND) }
    var showTagSelector by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .background(Color(0xFFFDF29D))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "←",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.clickable(role = Role.Button) {
                        router.goBack()
                    }
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    text = StringResources.friendsTitle(),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black
                )
            }
        }

        // Add Friend Section
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                NeoBrutalistCardViewWithFlexSize(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = Color.White
                ) {
                    // Friend Name TextField
                    TextField(
                        value = addFriendDisplayName,
                        onValueChange = { addFriendDisplayName = it },
                        placeholder = { Text(StringResources.friendNameLabel(), fontSize = 14.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        textStyle = androidx.compose.ui.text.TextStyle(fontSize = 14.sp)
                    )
                }
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    NeoBrutalistCardViewWithFlexSize(
                        modifier = Modifier.width(100.dp).clickable(role = Role.Button){
                            showTagSelector = !showTagSelector
                        },
                        backgroundColor = Color(0xFFffb3F1)
                    ) {
                        Text(
                            text = selectedTag.name.lowercase().replaceFirstChar { it.uppercase() },
                            color = Color.White,
                            fontSize = 12.sp
                        )
                    }
                    NeoBrutalistCircleButton(
                        onMainClick = {
                            if (!sendRequestLoading && addFriendDisplayName.isNotBlank()){
                                viewModel.sendFriendRequest(
                                    displayName = addFriendDisplayName,
                                    tag = selectedTag
                                )
                                addFriendDisplayName = ""
                            }
                        }
                    ) {
                        if (sendRequestLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("+", color = Color.White, fontSize = 16.sp)
                        }
                    }
                }

                // Tag Selector
                if (showTagSelector) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            FriendTag.values().forEach { tag ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            selectedTag = tag
                                            showTagSelector = false
                                        }
                                        .padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = selectedTag == tag,
                                        onClick = {
                                            selectedTag = tag
                                            showTagSelector = false
                                        }
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = tag.name.lowercase().replaceFirstChar { it.uppercase() },
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Friends List Header
        item {
            Text(
                text = "Friends (${friendsList.size})",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }

        // Error message
        error?.let { errorMessage ->
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = errorMessage,
                            fontSize = 12.sp,
                            color = Color(0xFFD32F2F),
                            modifier = Modifier.weight(1f)
                        )
                        TextButton(onClick = { viewModel.clearError() }) {
                            Text(StringResources.dismiss(), fontSize = 10.sp)
                        }
                    }
                }
            }
        }

        // Send request result
        sendRequestResult?.let { result ->
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (result.contains("success", ignoreCase = true))
                            Color(0xFFE8F5E8) else Color(0xFFFFEBEE)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = result,
                            fontSize = 12.sp,
                            color = if (result.contains("success", ignoreCase = true))
                                Color(0xFF2E7D32) else Color(0xFFD32F2F),
                            modifier = Modifier.weight(1f)
                        )
                        TextButton(onClick = { viewModel.clearSendRequestResult() }) {
                            Text(StringResources.dismiss(), fontSize = 10.sp)
                        }
                    }
                }
            }
        }

        // Loading indicator
        if (isLoading) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }

        // Friends List
        if (friendsList.isEmpty() && !isLoading) {
            item {
                Text(
                    text = "No friends yet. Add friends by their ID above.",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(16.dp)
                )
            }
        } else {
            items(friendsList) { friend ->
                NeoBrutalistCardViewWithFlexSize(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = Color(0xFFCEE977),
                    cornerRadius = 8.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = friend.displayName ?: "User ${friend.userId}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            val info = listOfNotNull(friend.mbtiType, friend.zodiacSign)
                                .joinToString(" • ")
                            if (info.isNotBlank()) {
                                Text(text = info, fontSize = 12.sp, color = Color.Gray)
                            }
                            friend.tag?.let { tag ->
                                Text(
                                    text = tag.lowercase().replaceFirstChar { it.uppercase() },
                                    fontSize = 10.sp,
                                    color = Color(0xFF9C27B0),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Button(
                            onClick = { viewModel.removeFriend(friend.userId) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5722)),
                            modifier = Modifier.size(32.dp),
                            contentPadding = PaddingValues(0.dp),
                            enabled = !isLoading
                        ) {
                            Text("×", color = Color.White, fontSize = 14.sp)
                        }
                    }
                }

            }
        }
    }
}


