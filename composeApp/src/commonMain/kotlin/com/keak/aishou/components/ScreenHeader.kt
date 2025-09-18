package com.keak.aishou.components

import aishou.composeapp.generated.resources.Res
import aishou.composeapp.generated.resources.back
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.painterResource

@Composable
fun ScreenHeader(
    screenName: String,
    backAction: () -> Unit,
    modifier: Modifier,
    backGroundColor: Color = Color.White,
    textColor: Color = Color.Black,
    fontSize:Int = 18
) {
    NeoBrutalistCardViewWithFlexSize(
        modifier = modifier,
        backgroundColor = backGroundColor
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(Res.drawable.back),
                modifier = Modifier.size(20.dp).clickable() {
                    backAction.invoke()
                },
                contentDescription = null
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = screenName,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                fontWeight = FontWeight.Black,
                color = textColor,
                fontSize = fontSize.sp
            )
        }
    }
}