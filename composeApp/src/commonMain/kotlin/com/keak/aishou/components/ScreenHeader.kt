package com.keak.aishou.components

import aishou.composeapp.generated.resources.Res
import aishou.composeapp.generated.resources.back
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
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
fun ScreenHeader(screenName: String, backAction:()-> Unit){
    NeoBrutalistCardViewWithFlexSize(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = Color.White
    ){
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(Res.drawable.back),
                modifier = Modifier.size(30.dp).clickable(){
                    backAction.invoke()
                },
                contentDescription = null
            )
            Text(
                text = screenName,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                fontWeight = FontWeight.Black,
                color = Color.Black,
                fontSize = 18.sp
            )
        }
    }
}