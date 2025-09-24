package com.keak.aishou.screens.homescreen

import aishou.composeapp.generated.resources.Res
import aishou.composeapp.generated.resources.crown
import aishou.composeapp.generated.resources.settings_already_changed_name
import aishou.composeapp.generated.resources.settings_can_change_name_once
import aishou.composeapp.generated.resources.settings_cancel_button
import aishou.composeapp.generated.resources.settings_change_name
import aishou.composeapp.generated.resources.settings_change_name_dialog_title
import aishou.composeapp.generated.resources.settings_character_counter
import aishou.composeapp.generated.resources.settings_contact_us
import aishou.composeapp.generated.resources.settings_email_body
import aishou.composeapp.generated.resources.settings_email_subject
import aishou.composeapp.generated.resources.settings_enter_new_name
import aishou.composeapp.generated.resources.settings_name_change_warning
import aishou.composeapp.generated.resources.settings_name_max_chars_label
import aishou.composeapp.generated.resources.settings_privacy_policy
import aishou.composeapp.generated.resources.settings_rate_us
import aishou.composeapp.generated.resources.settings_save_button
import aishou.composeapp.generated.resources.settings_terms_conditions
import aishou.composeapp.generated.resources.settings_title
import aishou.composeapp.generated.resources.settings_upgrade_to_premium
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.keak.aishou.components.NeoBrutalistCardViewWithFlexSize
import com.keak.aishou.navigation.Router
import com.keak.aishou.utils.PlatformActions
import com.skydoves.flexible.bottomsheet.material3.FlexibleBottomSheet
import com.skydoves.flexible.core.FlexibleSheetSize
import com.skydoves.flexible.core.rememberFlexibleBottomSheetState
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun SettingsBottomSheet(
    router: Router,
    isPremium: Boolean,
    currentName: String,
    hasChangedName: Boolean,
    onDismiss: () -> Unit,
    onNameChange: (String) -> Unit,
    onUpgradeTapped: () -> Unit
) {
    var showNameDialog by remember { mutableStateOf(false) }
    var tempName by remember { mutableStateOf(currentName) }

    FlexibleBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberFlexibleBottomSheetState(
            flexibleSheetSize = FlexibleSheetSize(
                fullyExpanded = 0.85f
            ),
            isModal = true,
        ),
        scrimColor = Color.Transparent
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Black)
                .padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 32.dp)
                .verticalScroll(rememberScrollState())
        ) {
                // Header
                Text(
                    text = stringResource(Res.string.settings_title),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                // Premium Upgrade Section (only if not premium)
                if (!isPremium) {
                    NeoBrutalistCardViewWithFlexSize(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .rotate(-1f)
                            .clickable(role = Role.Button) { onUpgradeTapped() },
                        backgroundColor = Color(0xFFFFD700), // Gold
                        borderColor = Color.Black,
                        shadowColor = Color(0xFF8B4513) // Brown shadow
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(Res.drawable.crown),
                                contentDescription = "Premium Crown",
                                tint = Color.Black,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = stringResource(Res.string.settings_upgrade_to_premium),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.Black,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = "✨",
                                fontSize = 20.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }

                    // Name Change Section
                    NeoBrutalistCardViewWithFlexSize(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .clickable(
                                enabled = !hasChangedName,
                                role = Role.Button
                            ) {
                                if (!hasChangedName) {
                                    showNameDialog = true
                                }
                            },
                        backgroundColor =  Color.White,
                        borderColor = Color.Black,
                        shadowColor = if (hasChangedName) Color(0xFF333333) else Color(0xFF4FFFB3)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = stringResource(Res.string.settings_change_name),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = stringResource(if (hasChangedName)
                                    Res.string.settings_already_changed_name
                                else
                                    Res.string.settings_can_change_name_once),
                                fontSize = 12.sp,
                                color = if (hasChangedName) Color(0xFF666666) else Color(0xFF424242)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))


                // Menu Items
                MenuSection(
                    contactUsTitle = stringResource(Res.string.settings_contact_us),
                    rateUsTitle = stringResource(Res.string.settings_rate_us),
                    privacyTitle = stringResource(Res.string.settings_privacy_policy),
                    termsTitle = stringResource(Res.string.settings_terms_conditions),
                    emailSubject = stringResource(Res.string.settings_email_subject),
                    emailBody = stringResource(Res.string.settings_email_body)
                )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    // Name Change Dialog
    if (showNameDialog) {
        AlertDialog(
            onDismissRequest = {
                showNameDialog = false
                tempName = currentName
            },
            title = {
                Text(
                    text = stringResource(Res.string.settings_change_name_dialog_title),
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            },
            text = {
                Column {
                    Text(
                        text = stringResource(Res.string.settings_enter_new_name),
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = tempName,
                        onValueChange = { newValue ->
                            // Always allow changes, but limit to 13 characters
                            tempName = if (newValue.length <= 13) newValue else newValue.take(13)
                        },
                        label = { Text(stringResource(Res.string.settings_name_max_chars_label)) },
                        supportingText = {
                            Text(stringResource(Res.string.settings_character_counter, tempName.length))
                        },
                        isError = tempName.length > 13,
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = stringResource(Res.string.settings_name_change_warning),
                        fontSize = 12.sp,
                        color = Color(0xFFFF6B35),
                        fontWeight = FontWeight.Medium
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (tempName.isNotBlank() && tempName != currentName) {
                            onNameChange(tempName)
                        }
                        showNameDialog = false
                    },
                    enabled = tempName.isNotBlank() && tempName != currentName
                ) {
                    Text(
                        text = stringResource(Res.string.settings_save_button),
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showNameDialog = false
                        tempName = currentName
                    }
                ) {
                    Text(
                        text = stringResource(Res.string.settings_cancel_button),
                        color = Color(0xFF666666)
                    )
                }
            },
            containerColor = Color.White
        )
    }
}

@Composable
private fun MenuSection(
    contactUsTitle: String,
    rateUsTitle: String,
    privacyTitle: String,
    termsTitle: String,
    emailSubject: String,
    emailBody: String
) {
    val items = listOf(
        MenuItem(
            title = contactUsTitle,
            icon = "📧",
            onClick = {
                PlatformActions.sendEmail(
                    to = "dev.akinkeskinbas@gmail.com",
                    subject = emailSubject,
                    body = emailBody
                )
            }
        ),
        MenuItem(
            title = rateUsTitle,
            icon = "⭐",
            onClick = {
                PlatformActions.openStore()
            }
        ),
        MenuItem(
            title = privacyTitle,
            icon = "🔒",
            onClick = {
                PlatformActions.openUrl("https://aishou.site/privacy")
            }
        ),
        MenuItem(
            title = termsTitle,
            icon = "📄",
            onClick = {
                PlatformActions.openUrl("https://aishou.site/terms")
            }
        )
    )
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        items.forEach { item ->
            NeoBrutalistCardViewWithFlexSize(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .clickable(role = Role.Button) { item.onClick() },
                backgroundColor = Color.White,
                borderColor = Color.Black,
                shadowColor = Color(0xFF34D399)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = item.icon,
                        fontSize = 20.sp,
                        modifier = Modifier.size(24.dp),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Text(
                        text = item.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black,
                        modifier = Modifier.weight(1f)
                    )

                    Text(
                        text = "→",
                        fontSize = 16.sp,
                        color = Color.Black
                    )
                }
            }
        }
    }
}

private data class MenuItem(
    val title: String,
    val icon: String,
    val onClick: () -> Unit
)