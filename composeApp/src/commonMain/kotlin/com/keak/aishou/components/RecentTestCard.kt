package com.keak.aishou.components

import aishou.composeapp.generated.resources.Res
import aishou.composeapp.generated.resources.empty_star
import aishou.composeapp.generated.resources.hearth_desc
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.keak.aishou.screens.homescreen.TesterType
import org.jetbrains.compose.resources.painterResource

@Composable
fun RecentTestCard(
    testerName: String,
    testerMbti: String,
    testResult: String,
    modifier: Modifier = Modifier,
    testerType: TesterType,
    bgColor: Color,
    clickAction:()-> Unit

) {
    val cardImage =
        if (testerType == TesterType.PARTNER) Res.drawable.hearth_desc else Res.drawable.empty_star
    val cardBgColor = if (testerType == TesterType.PARTNER) Color(0xFFEC407A) else Color(0xFF4DD0E1)
    NeoBrutalistCardViewWithFlexSize(
        backgroundColor = Color.White,
        modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp).clickable(){
            clickAction.invoke()
        }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(50.dp).background(cardBgColor).border(1.dp, Color.Black),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(cardImage),
                    modifier = Modifier.size(20.dp),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(if (testerType == TesterType.PARTNER) Color.White else Color.Black)
                )
            }
            Spacer(Modifier.width(8.dp))
            Text(
                text = testerName,
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                modifier = Modifier,
            )
            Spacer(Modifier.width(8.dp))
            Box(
                modifier = Modifier.background(Color(0xFFBA68C8)).border(1.dp, Color.Black)
                    .wrapContentSize().padding(4.dp)
            ) {
                Text(
                    text = testerMbti,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier,
                    color = Color.White
                )
            }
            Spacer(Modifier.weight(1f))
            Box(
                modifier = Modifier.background(bgColor).border(1.dp, Color.Black).wrapContentSize()
                    .padding(8.dp)
            ) {
                Text(
                    text = "$testResult%",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier,
                )
            }
        }


    }
}