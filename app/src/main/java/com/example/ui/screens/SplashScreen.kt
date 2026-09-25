package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Text
import com.example.R
import com.example.ui.theme.KfuBackground
import com.example.ui.theme.KfuBlue
import com.example.ui.theme.KfuBlueTint
import com.example.ui.theme.KfuGreen
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onSplashFinished: () -> Unit) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        visible = true
        delay(800)
        onSplashFinished()
    }

    val logoScale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.3f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 100f),
        label = "logoScale"
    )
    val logoAlpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(600), label = "logoAlpha"
    )

    Box(
        modifier = Modifier.fillMaxSize().background(KfuBackground),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(180.dp)
                    .clip(CircleShape)
                    .background(KfuBlueTint)
                    .scale(logoScale)
                    .alpha(logoAlpha),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo_university),
                    contentDescription = "شعار جامعة كفر الشيخ",
                    modifier = Modifier.size(120.dp)
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "جامعة كفر الشيخ",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = KfuBlue,
                textAlign = TextAlign.Center
            )
            Text(
                text = "البوابة الرسمية للطلاب",
                fontSize = 13.sp,
                color = KfuGreen,
                textAlign = TextAlign.Center
            )
        }
    }
}